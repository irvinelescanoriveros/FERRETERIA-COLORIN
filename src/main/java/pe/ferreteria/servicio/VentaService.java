package pe.ferreteria.servicio;

import pe.ferreteria.dao.VentaDAO;
import pe.ferreteria.dao.DetalleVentaDAO;
import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.dao.KardexDAO;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Venta;
import pe.ferreteria.modelo.DetalleVenta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.List;
import pe.ferreteria.dao.CajaDAO;
import pe.ferreteria.dao.MovimientoCajaDAO;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.MovimientoCaja;
import pe.ferreteria.reporte.DatosReporteGerencial;
import pe.ferreteria.reporte.DetalleVentaReporte;
import pe.ferreteria.reporte.EvolucionReporte;
import pe.ferreteria.reporte.MetodoPagoReporte;
import pe.ferreteria.reporte.ProductoMasVendidoReporte;
import pe.ferreteria.reporte.RendimientoVendedorReporte;

public class VentaService {

    private final VentaDAO ventaDAO;
private final DetalleVentaDAO detalleVentaDAO;
private final InventarioDAO inventarioDAO;
private final KardexDAO kardexDAO;
private final CajaDAO cajaDAO;
private final MovimientoCajaDAO movimientoCajaDAO;

    public VentaService() {
    this.ventaDAO = new VentaDAO();
    this.detalleVentaDAO = new DetalleVentaDAO();
    this.inventarioDAO = new InventarioDAO();
    this.kardexDAO = new KardexDAO();
    this.cajaDAO = new CajaDAO();
    this.movimientoCajaDAO = new MovimientoCajaDAO();
}

    public int registrarVenta(Venta venta) {
        return ventaDAO.insertar(venta);
    }

    public int registrarDetalleVenta(
            DetalleVenta detalleVenta) {

        return detalleVentaDAO.insertar(
                detalleVenta
        );
    }

public synchronized boolean registrarVentaCompleta(
        Venta venta,
        List<DetalleVenta> detalles) {

    if (venta == null
            || detalles == null
            || detalles.isEmpty()) {

        return false;
    }

    if (venta.getIdCliente() <= 0
            || venta.getIdUsuario() <= 0) {

        return false;
    }

    if (venta.getTipoComprobante() == null
            || venta.getTipoComprobante()
                    .trim()
                    .isEmpty()) {

        return false;
    }

    if (venta.getMetodoPago() == null
            || venta.getMetodoPago()
                    .trim()
                    .isEmpty()) {

        return false;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();

        con.setAutoCommit(false);

        /*
         * Verificar que exista una Caja abierta.
         */
        Caja caja = cajaDAO.buscarCajaAbierta(con);

        if (caja == null) {

            throw new RuntimeException(
                    "No existe una Caja abierta. "
                    + "No se puede registrar la venta."
            );
        }

        /*
         * Generar serie.
         */
        String serie;

        if ("Boleta".equalsIgnoreCase(
                venta.getTipoComprobante())) {

            serie = "B001";

        } else if ("Factura".equalsIgnoreCase(
                venta.getTipoComprobante())) {

            serie = "F001";

        } else {

            throw new IllegalArgumentException(
                    "Tipo de comprobante no válido."
            );
        }

        /*
         * Generar número correlativo.
         */
        int numero
                = ventaDAO.obtenerSiguienteNumero(
                        con,
                        serie
                );

        String nroComprobante
                = String.format(
                        "%s-%08d",
                        serie,
                        numero
                );

        venta.setSerie(serie);
        venta.setNumero(numero);
        venta.setNroComprobante(
                nroComprobante
        );

        /*
         * Fecha de la venta.
         */
        if (venta.getFechaVenta() == null) {

            venta.setFechaVenta(
                    java.time.LocalDateTime.now()
            );
        }

        /*
         * Calcular importes usando el
         * precio real del lote en Inventario.
         */
        BigDecimal importeTotal
                = BigDecimal.ZERO;

        for (DetalleVenta detalle : detalles) {

            if (detalle == null
                    || detalle.getIdInventario() <= 0
                    || detalle.getCantidad() <= 0) {

                throw new IllegalArgumentException(
                        "Existe un detalle de venta inválido."
                );
            }

            /*
             * Obtener lote real.
             */
            pe.ferreteria.modelo.Inventario inventario
                    = inventarioDAO.buscarPorId(
                            con,
                            detalle.getIdInventario()
                    );

            if (inventario == null) {

                throw new IllegalArgumentException(
                        "El lote seleccionado no existe "
                        + "o no está disponible."
                );
            }

            /*
             * Precio oficial del lote.
             */
            BigDecimal precioUnitario
                    = inventario.getPrecioVenta();

            if (precioUnitario == null
                    || precioUnitario.compareTo(
                            BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "El lote no tiene un precio de venta válido."
                );
            }

            /*
             * Validar stock.
             */
            if (detalle.getCantidad()
                    > inventario.getStock()) {

                throw new IllegalArgumentException(
                        "No existe suficiente stock para el lote: "
                        + inventario.getLote()
                );
            }

            /*
             * Subtotal normal.
             */
            BigDecimal subtotalDetalle
                    = precioUnitario.multiply(
                            BigDecimal.valueOf(
                                    detalle.getCantidad()
                            )
                    );

            subtotalDetalle
                    = subtotalDetalle.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            /*
             * Descuento automático del lote.
             */
            BigDecimal descuento
                    = BigDecimal.ZERO;

            if (inventario.isDescuentoActivo()) {

                BigDecimal porcentaje
                        = inventario.getDescuentoPorcentaje();

                if (porcentaje == null) {

                    porcentaje = BigDecimal.ZERO;
                }

                porcentaje
                        = porcentaje.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

                if (porcentaje.compareTo(
                        BigDecimal.ZERO) < 0
                        || porcentaje.compareTo(
                                new BigDecimal("100")) > 0) {

                    throw new IllegalArgumentException(
                            "El porcentaje de descuento "
                            + "del lote no es válido."
                    );
                }

                descuento
                        = subtotalDetalle
                                .multiply(porcentaje)
                                .divide(
                                        new BigDecimal("100"),
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            /*
             * Importe final.
             */
            BigDecimal importeFinal
                    = subtotalDetalle.subtract(
                            descuento
                    );

            importeFinal
                    = importeFinal.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            /*
             * Guardar valores reales usados
             * en la venta.
             */
            detalle.setPrecioUnitario(
                    precioUnitario
            );

            detalle.setSubtotal(
                    subtotalDetalle
            );

            detalle.setDescuento(
                    descuento
            );

            detalle.setImporteTotal(
                    importeFinal
            );

            importeTotal
                    = importeTotal.add(
                            importeFinal
                    );
        }

        /*
         * Separar IGV incluido.
         */
        importeTotal
                = importeTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal valorVenta
                = importeTotal.divide(
                        new BigDecimal("1.18"),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal igv
                = importeTotal.subtract(
                        valorVenta
                );

        igv
                = igv.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        venta.setSubtotal(
                valorVenta
        );

        venta.setIgv(
                igv
        );

        venta.setTotal(
                importeTotal
        );

        /*
         * Pago.
         */
        BigDecimal montoRecibido
                = venta.getMontoRecibido() == null
                ? BigDecimal.ZERO
                : venta.getMontoRecibido();

        BigDecimal vuelto
                = BigDecimal.ZERO;

        if ("Efectivo".equalsIgnoreCase(
                venta.getMetodoPago())) {

            if (montoRecibido.compareTo(
                    importeTotal) < 0) {

                throw new IllegalArgumentException(
                        "El monto recibido es insuficiente."
                );
            }

            vuelto
                    = montoRecibido.subtract(
                            importeTotal
                    );

        } else {

            montoRecibido
                    = BigDecimal.ZERO;

            vuelto
                    = BigDecimal.ZERO;
        }

        venta.setMontoRecibido(
                montoRecibido.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        venta.setVuelto(
                vuelto.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        if (venta.getEstadoComprobante()
                == null
                || venta.getEstadoComprobante()
                        .trim()
                        .isEmpty()) {

            venta.setEstadoComprobante(
                    "Pendiente"
            );
        }

        /*
         * Registrar cabecera de Venta.
         */
        int idVenta
                = ventaDAO.insertar(
                        con,
                        venta
                );

        if (idVenta <= 0) {

            throw new RuntimeException(
                    "No se pudo registrar la venta."
            );
        }

        /*
         * Guardar el ID inmediatamente.
         */
        venta.setIdVenta(
                idVenta
        );

        /*
         * Registrar detalles, reducir stock
         * y registrar salida en Kardex.
         */
        for (DetalleVenta detalle
                : detalles) {

            detalle.setIdVenta(
                    idVenta
            );

            int idDetalle
                    = detalleVentaDAO.insertar(
                            con,
                            detalle
                    );

            if (idDetalle <= 0) {

                throw new RuntimeException(
                        "No se pudo registrar un detalle de venta."
                );
            }

            /*
             * Obtener nuevamente el lote real.
             */
            pe.ferreteria.modelo.Inventario inventario
                    = inventarioDAO.buscarPorId(
                            con,
                            detalle.getIdInventario()
                    );

            if (inventario == null) {

                throw new RuntimeException(
                        "No se pudo obtener el lote "
                        + "para registrar el movimiento Kardex."
                );
            }

            int stockAntes
                    = inventario.getStock();

            int stockDespues
                    = stockAntes
                    - detalle.getCantidad();

            int stockDefectuosoAntes
                    = inventario.getStockDefectuoso();

            int stockDefectuosoDespues
                    = stockDefectuosoAntes;

            /*
             * Reducir stock.
             */
            boolean stockReducido
                    = inventarioDAO.reducirStock(
                            con,
                            detalle.getIdInventario(),
                            detalle.getCantidad()
                    );

            if (!stockReducido) {

                throw new RuntimeException(
                        "El stock disponible cambió "
                        + "o no existe suficiente stock "
                        + "para uno de los lotes."
                );
            }

            /*
             * Registrar salida en Kardex.
             */
            pe.ferreteria.modelo.Kardex kardex
                    = new pe.ferreteria.modelo.Kardex();

            kardex.setIdProducto(
                    inventario.getIdProducto()
            );

            kardex.setIdInventario(
                    inventario.getIdInventario()
            );

            kardex.setIdUsuario(
                    venta.getIdUsuario()
            );

            kardex.setFechaMovimiento(
                    venta.getFechaVenta()
            );

            kardex.setMovimiento(
                    "Salida"
            );

            kardex.setMotivoMovimiento(
                    "Venta"
            );

            kardex.setCantidad(
                    detalle.getCantidad()
            );

            kardex.setStockAntes(
                    stockAntes
            );

            kardex.setStockDespues(
                    stockDespues
            );

            kardex.setStockDefectuosoAntes(
                    stockDefectuosoAntes
            );

            kardex.setStockDefectuosoDespues(
                    stockDefectuosoDespues
            );

            kardex.setPrecioUnitario(
                    detalle.getPrecioUnitario()
            );

            kardex.setTipoReferencia(
                    "Venta"
            );

            kardex.setIdReferencia(
                    idVenta
            );

            kardex.setNroComprobante(
                    venta.getNroComprobante()
            );

            kardex.setMotivo(
                    null
            );

            boolean kardexRegistrado
                    = kardexDAO.insertar(
                            con,
                            kardex
                    );

            if (!kardexRegistrado) {

                throw new RuntimeException(
                        "No se pudo registrar el movimiento Kardex."
                );
            }
        }

        /*
         * Registrar ingreso en Movimiento_Caja.
         */
        MovimientoCaja movimientoCaja
                = new MovimientoCaja();

        movimientoCaja.setIdCaja(
                caja.getIdCaja()
        );

        movimientoCaja.setIdUsuario(
                venta.getIdUsuario()
        );

        movimientoCaja.setFechaMovimiento(
                venta.getFechaVenta()
        );

        movimientoCaja.setTipoMovimiento(
                "Ingreso"
        );

        movimientoCaja.setConcepto(
                "Venta"
        );

        movimientoCaja.setMonto(
                venta.getTotal()
        );

        movimientoCaja.setMetodoPago(
                venta.getMetodoPago()
        );

        movimientoCaja.setTipoReferencia(
                "Venta"
        );

        movimientoCaja.setIdReferencia(
                idVenta
        );

        movimientoCaja.setIdVenta(
                idVenta
        );

        movimientoCaja.setIdCompra(
                null
        );

        movimientoCaja.setNroComprobante(
                venta.getNroComprobante()
        );

        movimientoCaja.setDescripcion(
                null
        );

        movimientoCaja.setActivo(
                true
        );

        boolean movimientoRegistrado
                = movimientoCajaDAO.insertar(
                        con,
                        movimientoCaja
                );

        if (!movimientoRegistrado) {

            throw new RuntimeException(
                    "No se pudo registrar el movimiento de Caja."
            );
        }

        /*
         * Actualizar totales de la Caja.
         */
        BigDecimal totalIngresos
                = movimientoCajaDAO.obtenerTotalIngresos(
                        con,
                        caja.getIdCaja()
                );

        BigDecimal totalSalidas
                = movimientoCajaDAO.obtenerTotalSalidas(
                        con,
                        caja.getIdCaja()
                );

        BigDecimal montoEsperado
                = caja.getMontoApertura()
                        .add(totalIngresos)
                        .subtract(totalSalidas)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        boolean totalesActualizados
                = cajaDAO.actualizarTotales(
                        con,
                        caja.getIdCaja(),
                        totalIngresos,
                        totalSalidas,
                        montoEsperado
                );

        if (!totalesActualizados) {

            throw new RuntimeException(
                    "No se pudieron actualizar "
                    + "los totales de la Caja."
            );
        }

        /*
         * Confirmar toda la transacción.
         */
        con.commit();

        return true;

} catch (Exception e) {

    if (con != null) {

        try {

            con.rollback();

        } catch (Exception rollbackEx) {

            rollbackEx.printStackTrace();
        }
    }

    if (e.getMessage() != null
            && e.getMessage().contains(
                    "No existe una Caja abierta")) {

        throw new RuntimeException(
                "No existe una Caja abierta. "
                + "No se puede registrar la venta."
        );
    }

    e.printStackTrace();

    return false;


    } finally {

        if (con != null) {

            try {

                con.setAutoCommit(true);
                con.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}

    public DetalleVenta buscarDetallePorVentaEInventario(
            int idVenta,
            int idInventario) {

        return detalleVentaDAO.buscarPorVentaEInventario(
                idVenta,
                idInventario
        );
    }
    public List<DetalleVenta> listarDetallesPorVenta(int idVenta) {
    return detalleVentaDAO.listarPorVenta(idVenta);
}
    
public Venta buscarPorId(int idVenta) {
    return ventaDAO.buscarPorId(idVenta);
}
public List<Venta> listarParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.listarParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<DetalleVenta> listarDetallesParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return detalleVentaDAO.listarParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public DatosReporteGerencial obtenerResumenParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.obtenerResumenParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}


public List<ProductoMasVendidoReporte> listarProductosMasVendidosParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return detalleVentaDAO.listarProductosMasVendidosParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<RendimientoVendedorReporte> listarRendimientoVendedoresParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.listarRendimientoVendedoresParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<MetodoPagoReporte> listarMetodosPagoParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.listarMetodosPagoParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<EvolucionReporte> listarEvolucionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.listarEvolucionParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<DetalleVentaReporte> listarDetalleVentasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaDAO.listarDetalleVentasParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
}