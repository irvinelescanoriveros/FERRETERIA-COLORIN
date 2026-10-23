package pe.ferreteria.servicio;

import java.math.BigDecimal;
import java.util.List;
import java.sql.Connection;
import pe.ferreteria.dao.CajaDAO;

import pe.ferreteria.dao.KardexDAO;
import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.dao.DetalleVentaDAO;
import pe.ferreteria.dao.MovimientoCajaDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Kardex;
import pe.ferreteria.reporte.MermaReporte;


public class KardexService {

    private final KardexDAO kardexDAO;
private final InventarioDAO inventarioDAO;
private final DetalleVentaDAO detalleVentaDAO;
private final MovimientoCajaDAO movimientoCajaDAO;
private final CajaDAO cajaDAO;


    public KardexService() {
    kardexDAO = new KardexDAO();
    inventarioDAO = new InventarioDAO();
    detalleVentaDAO = new DetalleVentaDAO();
    movimientoCajaDAO = new MovimientoCajaDAO();
    cajaDAO = new CajaDAO();
}

    /*
     * Registrar movimiento Kardex.
     */
    public boolean registrar(Kardex kardex) {

        if (kardex == null) {
            return false;
        }

        return kardexDAO.insertar(
                kardex
        );
    }

    /*
     * Listar todo el Kardex.
     */
    public List<Kardex> listar() {

        return kardexDAO.listar();
    }

    /*
     * Buscar movimiento por ID.
     */
    public Kardex buscarPorId(
            int idKardex) {

        return kardexDAO.buscarPorId(
                idKardex
        );
    }

    /*
     * Listar movimientos de un lote.
     */
    public List<Kardex> listarPorInventario(
            int idInventario) {

        return kardexDAO.listarPorInventario(
                idInventario
        );
    }

    /*
     * Listar movimientos de un producto.
     */
    public List<Kardex> listarPorProducto(
            int idProducto) {

        return kardexDAO.listarPorProducto(
                idProducto
        );
    }

    /*
     * Obtener cuánto de una venta y lote
     * ya fue ingresado a stock defectuoso.
     */
    public int obtenerCantidadDefectuosaPorVenta(
            int idVenta,
            int idInventario) {

        return kardexDAO
                .obtenerCantidadDefectuosaPorVenta(
                        idVenta,
                        idInventario
                );
    }

    /*
     * Revertir una salida de venta.
     */
    public boolean revertirVenta(
            int idKardex,
            int idUsuario,
            String motivo) {

        Connection con = null;

        try {

            con = ConexionBD.conectar();

            con.setAutoCommit(false);

            Kardex original
                    = kardexDAO.buscarPorId(
                            con,
                            idKardex
                    );

            if (original == null) {

                throw new RuntimeException(
                        "No se encontró el movimiento seleccionado."
                );
            }

            if (!"Salida".equalsIgnoreCase(
                    original.getMovimiento())) {

                throw new RuntimeException(
                        "Solo se puede revertir una salida por venta."
                );
            }

            if (!"Venta".equalsIgnoreCase(
                    original.getTipoReferencia())) {

                throw new RuntimeException(
                        "El movimiento seleccionado no corresponde a una venta."
                );
            }

            if (kardexDAO.existeReversion(
                    con,
                    original.getIdReferencia(),
                    original.getIdInventario()
            )) {

                throw new RuntimeException(
                        "Este movimiento ya fue revertido."
                );
            }

            pe.ferreteria.modelo.Inventario inventario
                    = inventarioDAO.buscarPorId(
                            con,
                            original.getIdInventario()
                    );

            if (inventario == null) {

                throw new RuntimeException(
                        "No se encontró el lote en Inventario."
                );
            }

            int stockAntes
                    = inventario.getStock();

            int stockDespues
                    = stockAntes
                    + original.getCantidad();

            boolean aumentado
                    = inventarioDAO.aumentarStock(
                            con,
                            original.getIdInventario(),
                            original.getCantidad()
                    );

            if (!aumentado) {

                throw new RuntimeException(
                        "No se pudo recuperar el stock del lote."
                );
            }

            Kardex reversion
                    = new Kardex();

            reversion.setIdProducto(
                    original.getIdProducto()
            );

            reversion.setIdInventario(
                    original.getIdInventario()
            );

            reversion.setIdUsuario(
                    idUsuario
            );

            reversion.setFechaMovimiento(
                    java.time.LocalDateTime.now()
            );

            /*
             * Movimiento físico:
             * Reversion
             */
            reversion.setMovimiento(
                    "Reversion"
            );

            /*
             * Motivo estructurado:
             * Reversion
             */
            reversion.setMotivoMovimiento(
                    "Reversion"
            );

            reversion.setCantidad(
                    original.getCantidad()
            );

            reversion.setStockAntes(
                    stockAntes
            );

            reversion.setStockDespues(
                    stockDespues
            );

            reversion.setStockDefectuosoAntes(
                    inventario.getStockDefectuoso()
            );

            reversion.setStockDefectuosoDespues(
                    inventario.getStockDefectuoso()
            );

            reversion.setPrecioUnitario(
                    original.getPrecioUnitario()
            );

            reversion.setTipoReferencia(
                    "Venta"
            );

            reversion.setIdReferencia(
                    original.getIdReferencia()
            );

            reversion.setNroComprobante(
                    original.getNroComprobante()
            );

            reversion.setMotivo(
                    motivo
            );

            boolean registrado
                    = kardexDAO.insertar(
                            con,
                            reversion
                    );

            if (!registrado) {

                throw new RuntimeException(
                        "No se pudo registrar la reversión en Kardex."
                );
            }

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

    /*
     * Enviar producto vendido a stock defectuoso.
     */
    public boolean enviarStockDefectuoso(
            int idKardex,
            int idUsuario,
            int cantidad,
            String motivo) {

        Connection con = null;

        try {

            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            Kardex salida
                    = kardexDAO.buscarPorId(
                            con,
                            idKardex
                    );

            if (salida == null
                    || !"Salida".equalsIgnoreCase(
                            salida.getMovimiento())
                    || !"Venta".equalsIgnoreCase(
                            salida.getTipoReferencia())) {

                throw new RuntimeException(
                        "Seleccione una salida de venta."
                );
            }

            if (kardexDAO.existeReversion(
                    con,
                    salida.getIdReferencia(),
                    salida.getIdInventario())) {

                throw new RuntimeException(
                        "Esta venta ya fue revertida."
                );
            }

            pe.ferreteria.modelo.DetalleVenta detalle
                    = detalleVentaDAO
                            .buscarPorVentaEInventario(
                                    salida.getIdReferencia(),
                                    salida.getIdInventario()
                            );

            if (detalle == null) {

                throw new RuntimeException(
                        "No se encontró el detalle de la venta."
                );
            }

            int yaDefectuosos
                    = kardexDAO
                            .obtenerCantidadDefectuosaPorVenta(
                                    salida.getIdReferencia(),
                                    salida.getIdInventario()
                            );

            int disponible
                    = detalle.getCantidad()
                    - yaDefectuosos;

            if (cantidad <= 0
                    || cantidad > disponible) {

                throw new RuntimeException(
                        "La cantidad supera lo disponible "
                        + "para enviar a defectuoso."
                );
            }

            pe.ferreteria.modelo.Inventario inventario
                    = inventarioDAO.buscarPorId(
                            con,
                            salida.getIdInventario()
                    );

            if (inventario == null) {

                throw new RuntimeException(
                        "No se encontró el lote."
                );
            }

            int stockAntes
                    = inventario.getStock();

            int stockDefectuosoAntes
                    = inventario.getStockDefectuoso();

            int stockDefectuosoDespues
                    = stockDefectuosoAntes
                    + cantidad;

            boolean actualizado
                    = inventarioDAO.actualizarStockDefectuoso(
                            con,
                            inventario.getIdInventario(),
                            stockDefectuosoDespues
                    );

            if (!actualizado) {

                throw new RuntimeException(
                        "No se pudo actualizar el stock defectuoso."
                );
            }

            Kardex ajuste
                    = new Kardex();

            ajuste.setIdProducto(
                    salida.getIdProducto()
            );

            ajuste.setIdInventario(
                    salida.getIdInventario()
            );

            ajuste.setIdUsuario(
                    idUsuario
            );

            ajuste.setFechaMovimiento(
                    java.time.LocalDateTime.now()
            );

            ajuste.setMovimiento(
                    "Ajuste"
            );

            ajuste.setMotivoMovimiento(
                    "Producto Defectuoso"
            );

            ajuste.setCantidad(
                    cantidad
            );

            ajuste.setStockAntes(
                    stockAntes
            );

            ajuste.setStockDespues(
                    stockAntes
            );

            ajuste.setStockDefectuosoAntes(
                    stockDefectuosoAntes
            );

            ajuste.setStockDefectuosoDespues(
                    stockDefectuosoDespues
            );

            ajuste.setPrecioUnitario(
                    salida.getPrecioUnitario()
            );

            ajuste.setTipoReferencia(
                    "Venta"
            );

            ajuste.setIdReferencia(
                    salida.getIdReferencia()
            );

            ajuste.setNroComprobante(
                    salida.getNroComprobante()
            );

            ajuste.setMotivo(
                    motivo
            );

            if (!kardexDAO.insertar(
                    con,
                    ajuste
            )) {

                throw new RuntimeException(
                        "No se pudo registrar el ajuste en Kardex."
                );
            }

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

    /*
     * Ajustar manualmente el stock normal.
     */
    public boolean ajustarStock(
        int idInventario,
        int idUsuario,
        int nuevoStock,
        String motivo) {

    if (idInventario <= 0
            || idUsuario <= 0
            || nuevoStock < 0
            || motivo == null
            || motivo.trim().isEmpty()) {

        return false;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();
        con.setAutoCommit(false);

        pe.ferreteria.modelo.Inventario inventario
                = inventarioDAO.buscarPorId(
                        con,
                        idInventario
                );

        if (inventario == null) {

            throw new RuntimeException(
                    "No se encontró el lote en Inventario."
            );
        }

        int stockAntes
                = inventario.getStock();

        int stockDespues
                = nuevoStock;

        if (stockAntes == stockDespues) {

            throw new RuntimeException(
                    "El stock no presenta cambios."
            );
        }

        boolean actualizado
                = inventarioDAO.actualizarStock(
                        con,
                        idInventario,
                        stockDespues
                );

        if (!actualizado) {

            throw new RuntimeException(
                    "No se pudo actualizar el stock."
            );
        }

        Kardex ajuste
                = new Kardex();

        ajuste.setIdProducto(
                inventario.getIdProducto()
        );

        ajuste.setIdInventario(
                inventario.getIdInventario()
        );

        ajuste.setIdUsuario(
                idUsuario
        );

        ajuste.setFechaMovimiento(
                java.time.LocalDateTime.now()
        );

        ajuste.setMovimiento(
                "Ajuste"
        );

        /*
         * El motivo seleccionado en la interfaz
         * determina el motivo del movimiento.
         */
        ajuste.setMotivoMovimiento(
                motivo.trim()
        );

        ajuste.setCantidad(
                Math.abs(
                        stockDespues
                        - stockAntes
                )
        );

        ajuste.setStockAntes(
                stockAntes
        );

        ajuste.setStockDespues(
                stockDespues
        );

        ajuste.setStockDefectuosoAntes(
                inventario.getStockDefectuoso()
        );

        ajuste.setStockDefectuosoDespues(
                inventario.getStockDefectuoso()
        );

        ajuste.setPrecioUnitario(
                inventario.getPrecioCompra()
        );

        ajuste.setTipoReferencia(
                "Ajuste"
        );

        /*
         * Este ajuste no depende de otro movimiento.
         */
        ajuste.setIdReferencia(
                null
        );

        ajuste.setNroComprobante(
                null
        );

        /*
         * Observación escrita por el usuario.
         */
        ajuste.setMotivo(
                motivo.trim()
        );

        if (!kardexDAO.insertar(
                con,
                ajuste
        )) {

            throw new RuntimeException(
                    "No se pudo registrar el ajuste en Kardex."
            );
        }

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
    /*
     * Retirar productos del stock defectuoso.
     */
    public boolean retirarStockDefectuoso(
            int idInventario,
            int idUsuario,
            int idKardexDefectuoso,
            int cantidad) {

        if (idInventario <= 0
                || idUsuario <= 0
                || idKardexDefectuoso <= 0
                || cantidad <= 0) {

            return false;
        }

        Connection con = null;

        try {

            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            /*
             * Buscar el movimiento original que envió
             * la unidad a stock defectuoso.
             */
            Kardex ingresoDefectuoso
                    = kardexDAO.buscarPorId(
                            con,
                            idKardexDefectuoso
                    );

            if (ingresoDefectuoso == null) {

                throw new RuntimeException(
                        "No se encontró el movimiento de ingreso a defectuoso."
                );
            }

            if (!"Ajuste".equalsIgnoreCase(
                    ingresoDefectuoso.getMovimiento())) {

                throw new RuntimeException(
                        "El movimiento seleccionado no es un ajuste."
                );
            }

            if (!"Venta".equalsIgnoreCase(
                    ingresoDefectuoso.getTipoReferencia())) {

                throw new RuntimeException(
                        "El movimiento seleccionado no corresponde "
                        + "a un producto defectuoso de una venta."
                );
            }

            if (ingresoDefectuoso.getIdInventario()
                    != idInventario) {

                throw new RuntimeException(
                        "El movimiento seleccionado no corresponde "
                        + "al lote actual."
                );
            }

            /*
             * Cantidad original enviada a defectuoso.
             */
            int cantidadOriginal
                    = ingresoDefectuoso.getStockDefectuosoDespues()
                    - ingresoDefectuoso.getStockDefectuosoAntes();

            if (cantidadOriginal <= 0) {

                throw new RuntimeException(
                        "El movimiento seleccionado no representa "
                        + "un ingreso válido a defectuoso."
                );
            }

            /*
             * Consultar cuánto de esa bolsa ya fue retirado.
             */
            int cantidadRetirada
                    = kardexDAO.obtenerCantidadRetiradaDefectuoso(
                            con,
                            idKardexDefectuoso
                    );

            int disponible
                    = cantidadOriginal
                    - cantidadRetirada;

            if (disponible <= 0) {

                throw new RuntimeException(
                        "Este producto defectuoso ya fue retirado."
                );
            }

            if (cantidad > disponible) {

                throw new RuntimeException(
                        "La cantidad a retirar supera la cantidad "
                        + "disponible de esta bolsa."
                );
            }

            /*
             * Buscar lote actual.
             */
            pe.ferreteria.modelo.Inventario inventario
                    = inventarioDAO.buscarPorId(
                            con,
                            idInventario
                    );

            if (inventario == null) {

                throw new RuntimeException(
                        "No se encontró el lote en Inventario."
                );
            }

            int stockDefectuosoAntes
                    = inventario.getStockDefectuoso();

            if (cantidad > stockDefectuosoAntes) {

                throw new RuntimeException(
                        "El stock defectuoso actual no es suficiente."
                );
            }

            int stockDefectuosoDespues
                    = stockDefectuosoAntes
                    - cantidad;

            /*
             * Actualizar Inventario.
             */
            boolean actualizado
                    = inventarioDAO.actualizarStockDefectuoso(
                            con,
                            idInventario,
                            stockDefectuosoDespues
                    );

            if (!actualizado) {

                throw new RuntimeException(
                        "No se pudo actualizar el stock defectuoso."
                );
            }

            /*
             * Registrar retiro en Kardex.
             */
            Kardex retiro
                    = new Kardex();

            retiro.setIdProducto(
                    inventario.getIdProducto()
            );

            retiro.setIdInventario(
                    inventario.getIdInventario()
            );

            retiro.setIdUsuario(
                    idUsuario
            );

            retiro.setFechaMovimiento(
                    java.time.LocalDateTime.now()
            );

            retiro.setMovimiento(
                    "Ajuste"
            );

            retiro.setMotivoMovimiento(
                    "Retiro Defectuoso"
            );

            retiro.setCantidad(
                    cantidad
            );

            /*
             * El stock normal NO cambia.
             */
            retiro.setStockAntes(
                    inventario.getStock()
            );

            retiro.setStockDespues(
                    inventario.getStock()
            );

            /*
             * El cambio ocurre en stock defectuoso.
             */
            retiro.setStockDefectuosoAntes(
                    stockDefectuosoAntes
            );

            retiro.setStockDefectuosoDespues(
                    stockDefectuosoDespues
            );

            retiro.setPrecioUnitario(
                    inventario.getPrecioCompra()
            );

            /*
             * Apunta al Kardex del ingreso defectuoso original.
             */
            retiro.setTipoReferencia(
                    "Ajuste"
            );

            retiro.setIdReferencia(
                    idKardexDefectuoso
            );

            retiro.setNroComprobante(
                    ingresoDefectuoso.getNroComprobante()
            );

            retiro.setMotivo(
                    "Retiro de stock defectuoso"
            );

            if (!kardexDAO.insertar(
                    con,
                    retiro
            )) {

                throw new RuntimeException(
                        "No se pudo registrar el retiro en Kardex."
                );
            }

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
public boolean reingresarProductoDefectuoso(
        int idInventario,
        int idUsuario,
        int idKardexDefectuoso,
        int cantidad,
        String motivoMovimiento,
        String observacion) {

    if (idInventario <= 0
            || idUsuario <= 0
            || idKardexDefectuoso <= 0
            || cantidad <= 0) {

        return false;
    }

    if (motivoMovimiento == null
            || motivoMovimiento.trim().isEmpty()) {

        return false;
    }

    String motivo = motivoMovimiento.trim();

    if (!motivo.equals("Reingreso")
            && !motivo.equals("Garantia")
            && !motivo.equals("Otros")) {

        return false;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();
        con.setAutoCommit(false);

        /*
         * Buscar el movimiento original
         * que envió el producto a defectuoso.
         */
        Kardex ingresoDefectuoso =
                kardexDAO.buscarPorId(
                        con,
                        idKardexDefectuoso
                );

        if (ingresoDefectuoso == null) {

            throw new RuntimeException(
                    "No se encontró el movimiento "
                    + "de producto defectuoso."
            );
        }

        /*
         * Debe ser el ajuste que creó
         * el stock defectuoso desde una venta.
         */
        if (!"Ajuste".equalsIgnoreCase(
                ingresoDefectuoso.getMovimiento())) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no es un ajuste."
            );
        }

        if (!"Venta".equalsIgnoreCase(
                ingresoDefectuoso.getTipoReferencia())) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no corresponde a una venta."
            );
        }

        /*
         * Debe pertenecer al mismo lote.
         */
        if (ingresoDefectuoso.getIdInventario()
                != idInventario) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no corresponde al lote actual."
            );
        }

        /*
         * Cantidad original enviada a defectuoso.
         */
        int cantidadOriginal =
                ingresoDefectuoso
                        .getStockDefectuosoDespues()
                - ingresoDefectuoso
                        .getStockDefectuosoAntes();

        if (cantidadOriginal <= 0) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no representa un ingreso válido "
                    + "a stock defectuoso."
            );
        }

        /*
         * Cantidad ya retirada físicamente.
         */
        int cantidadRetirada =
                kardexDAO
                        .obtenerCantidadRetiradaDefectuoso(
                                con,
                                idKardexDefectuoso
                        );

        /*
         * Cantidad ya reingresada.
         */
        int cantidadReingresada =
                kardexDAO
                        .obtenerCantidadReingresadaDefectuoso(
                                con,
                                idKardexDefectuoso
                        );

        int disponible =
                cantidadOriginal
                - cantidadRetirada
                - cantidadReingresada;

        if (disponible <= 0) {

            throw new RuntimeException(
                    "Esta bolsa defectuosa "
                    + "ya no tiene unidades disponibles."
            );
        }

        if (cantidad > disponible) {

            throw new RuntimeException(
                    "La cantidad a reingresar supera "
                    + "la cantidad disponible de esta bolsa: "
                    + disponible
            );
        }

        /*
         * Buscar el mismo lote.
         */
        pe.ferreteria.modelo.Inventario inventario =
                inventarioDAO.buscarPorId(
                        con,
                        idInventario
                );

        if (inventario == null) {

            throw new RuntimeException(
                    "No se encontró el lote en Inventario."
            );
        }

        /*
         * Stock actual.
         */
        int stockAntes =
                inventario.getStock();

        int stockDefectuosoAntes =
                inventario.getStockDefectuoso();

        if (cantidad > stockDefectuosoAntes) {

            throw new RuntimeException(
                    "El stock defectuoso actual "
                    + "no es suficiente."
            );
        }

        /*
         * Calcular nuevos stocks.
         */
        int stockDespues =
                stockAntes + cantidad;

        int stockDefectuosoDespues =
                stockDefectuosoAntes - cantidad;

        /*
         * Actualizar stock normal.
         */
        boolean actualizadoStock =
                inventarioDAO.actualizarStock(
                        con,
                        idInventario,
                        stockDespues
                );

        if (!actualizadoStock) {

            throw new RuntimeException(
                    "No se pudo actualizar "
                    + "el stock normal."
            );
        }

        /*
         * Actualizar stock defectuoso.
         */
        boolean actualizadoDefectuoso =
                inventarioDAO.actualizarStockDefectuoso(
                        con,
                        idInventario,
                        stockDefectuosoDespues
                );

        if (!actualizadoDefectuoso) {

            throw new RuntimeException(
                    "No se pudo actualizar "
                    + "el stock defectuoso."
            );
        }

        /*
         * Registrar movimiento en Kardex.
         */
        Kardex reingreso =
                new Kardex();

        reingreso.setIdProducto(
                inventario.getIdProducto()
        );

        reingreso.setIdInventario(
                inventario.getIdInventario()
        );

        reingreso.setIdUsuario(
                idUsuario
        );

        reingreso.setFechaMovimiento(
                java.time.LocalDateTime.now()
        );

        reingreso.setMovimiento(
                "Entrada"
        );

        reingreso.setMotivoMovimiento(
                motivo
        );

        reingreso.setCantidad(
                cantidad
        );

        /*
         * Stock normal.
         */
        reingreso.setStockAntes(
                stockAntes
        );

        reingreso.setStockDespues(
                stockDespues
        );

        /*
         * Stock defectuoso.
         */
        reingreso.setStockDefectuosoAntes(
                stockDefectuosoAntes
        );

        reingreso.setStockDefectuosoDespues(
                stockDefectuosoDespues
        );

        reingreso.setPrecioUnitario(
                inventario.getPrecioCompra()
        );

        /*
         * Referencia al movimiento
         * defectuoso original.
         */
        reingreso.setTipoReferencia(
                "Ajuste"
        );

        reingreso.setIdReferencia(
                idKardexDefectuoso
        );

        reingreso.setNroComprobante(
                ingresoDefectuoso.getNroComprobante()
        );

        /*
         * Observación escrita por el usuario.
         */
        reingreso.setMotivo(
                observacion == null
                ? ""
                : observacion.trim()
        );

        if (!kardexDAO.insertar(
                con,
                reingreso
        )) {

            throw new RuntimeException(
                    "No se pudo registrar "
                    + "el reingreso en Kardex."
            );
        }

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
    /*
     * Listar ingresos a stock defectuoso por lote.
     */
    public List<Kardex> listarIngresosDefectuososPorInventario(
            int idInventario) {

        if (idInventario <= 0) {
            return new java.util.ArrayList<>();
        }

        return kardexDAO.listarIngresosDefectuososPorInventario(
                idInventario
        );
    }
    
    /*
 * Obtener cantidad pendiente de resolución
 * de un producto defectuoso.
 *
 * Pendiente =
 * cantidad defectuosa original
 * - cantidad repuesta
 * - cantidad devuelta en dinero.
 */
public int obtenerCantidadPendienteDevolucion(
        int idKardexDefectuoso) {

    if (idKardexDefectuoso <= 0) {
        return 0;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();

        /*
         * Buscar el movimiento original
         * de producto defectuoso.
         */
        Kardex defectuoso =
                kardexDAO.buscarPorId(
                        con,
                        idKardexDefectuoso
                );

        if (defectuoso == null) {

            throw new RuntimeException(
                    "No se encontró el movimiento "
                    + "de producto defectuoso."
            );
        }

        /*
         * Cantidad originalmente enviada
         * a stock defectuoso.
         */
        int cantidadDefectuosa =
                defectuoso.getStockDefectuosoDespues()
                - defectuoso.getStockDefectuosoAntes();

        /*
         * Cantidad ya resuelta mediante
         * reposición de producto.
         */
        int cantidadRepuesta =
                kardexDAO.obtenerCantidadRepuestaDefectuoso(
                        con,
                        idKardexDefectuoso
                );

        /*
         * Monto ya devuelto en dinero.
         */
        BigDecimal totalDevuelto =
                movimientoCajaDAO
                        .obtenerTotalDevolucionDefectuoso(
                                con,
                                idKardexDefectuoso
                        );

        /*
         * Precio unitario del producto
         * defectuoso.
         */
        BigDecimal precioUnitario =
                defectuoso.getPrecioUnitario();

        if (precioUnitario == null
                || precioUnitario.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new RuntimeException(
                    "El movimiento defectuoso "
                    + "no tiene un precio válido."
            );
        }

        /*
         * Convertir el dinero devuelto
         * a cantidad de unidades.
         */
        int cantidadDevueltaDinero =
                totalDevuelto
                        .divide(
                                precioUnitario,
                                0,
                                java.math.RoundingMode.HALF_UP
                        )
                        .intValue();

        /*
         * Cantidad total ya resuelta.
         */
        int cantidadResuelta =
                cantidadRepuesta
                + cantidadDevueltaDinero;

        /*
         * Cantidad que todavía puede resolverse.
         */
        int cantidadPendiente =
                cantidadDefectuosa
                - cantidadResuelta;

        return Math.max(
                cantidadPendiente,
                0
        );

    } catch (Exception e) {

        throw new RuntimeException(
                "Error al consultar la cantidad "
                + "pendiente de resolución.",
                e
        );

    } finally {

        if (con != null) {

            try {
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

    /*
 * Reponer producto al cliente por producto defectuoso.
     */
    public boolean reponerProductoDefectuoso(
        int idKardexDefectuoso,
        int idUsuario,
        int cantidad,
        String motivo) {

    if (idKardexDefectuoso <= 0
            || idUsuario <= 0
            || cantidad <= 0) {

        return false;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();
        con.setAutoCommit(false);

        /*
         * Buscar el movimiento original
         * de producto defectuoso.
         */
        Kardex defectuoso =
                kardexDAO.buscarPorId(
                        con,
                        idKardexDefectuoso
                );

        if (defectuoso == null) {

            throw new RuntimeException(
                    "No se encontró el movimiento "
                    + "de producto defectuoso."
            );
        }

        /*
         * Debe ser un ingreso de producto
         * defectuoso generado por una venta.
         */
        if (!"Ajuste".equalsIgnoreCase(
                defectuoso.getMovimiento())
                || !"Producto Defectuoso".equalsIgnoreCase(
                        defectuoso.getMotivoMovimiento())
                || !"Venta".equalsIgnoreCase(
                        defectuoso.getTipoReferencia())) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no corresponde a un producto defectuoso."
            );
        }

        /*
         * Obtener el lote original.
         */
        int idInventario =
                defectuoso.getIdInventario();

        pe.ferreteria.modelo.Inventario inventario
                = inventarioDAO.buscarPorId(
                        con,
                        idInventario
                );

        if (inventario == null) {

            throw new RuntimeException(
                    "No se encontró el lote en Inventario."
            );
        }

        /*
         * Cantidad original enviada a defectuosos.
         */
        int cantidadDefectuosa =
                defectuoso.getStockDefectuosoDespues()
                - defectuoso.getStockDefectuosoAntes();

        if (cantidadDefectuosa <= 0) {

            throw new RuntimeException(
                    "El movimiento no tiene cantidad "
                    + "defectuosa válida."
            );
        }

        /*
         * Cantidad que ya fue repuesta.
         */
        int cantidadRepuesta =
                kardexDAO.obtenerCantidadRepuestaDefectuoso(
                        con,
                        idKardexDefectuoso
                );

        /*
         * Cantidad pendiente de resolución
         * mediante reposición.
         */
        int cantidadDisponible =
                cantidadDefectuosa
                - cantidadRepuesta;

        if (cantidadDisponible <= 0) {

            throw new RuntimeException(
                    "La devolución ya fue repuesta "
                    + "en su totalidad."
            );
        }

        if (cantidad > cantidadDisponible) {

            throw new RuntimeException(
                    "La cantidad solicitada supera "
                    + "la cantidad pendiente de reposición. "
                    + "Pendiente: "
                    + cantidadDisponible
            );
        }

        /*
         * La reposición debe salir del mismo lote.
         */
        int stockAntes =
                inventario.getStock();

        if (cantidad > stockAntes) {

            throw new RuntimeException(
                    "No hay suficiente stock disponible "
                    + "en el mismo lote para realizar "
                    + "la reposición."
            );
        }

        int stockDespues =
                stockAntes - cantidad;

        /*
         * Disminuir stock normal.
         *
         * IMPORTANTE:
         * El stock defectuoso NO se modifica.
         */
        boolean actualizado =
                inventarioDAO.reducirStock(
                        con,
                        idInventario,
                        cantidad
                );

        if (!actualizado) {

            throw new RuntimeException(
                    "No se pudo descontar el stock "
                    + "para la reposición."
            );
        }

        /*
         * Registrar salida por reposición.
         */
        Kardex reposicion =
                new Kardex();

        reposicion.setIdProducto(
                inventario.getIdProducto()
        );

        reposicion.setIdInventario(
                inventario.getIdInventario()
        );

        reposicion.setIdUsuario(
                idUsuario
        );

        reposicion.setFechaMovimiento(
                java.time.LocalDateTime.now()
        );

        /*
         * Movimiento físico.
         */
        reposicion.setMovimiento(
                "Salida"
        );

        /*
         * Motivo estructurado.
         */
        reposicion.setMotivoMovimiento(
        "Reposicion p. client."
);

        reposicion.setCantidad(
                cantidad
        );

        reposicion.setStockAntes(
                stockAntes
        );

        reposicion.setStockDespues(
                stockDespues
        );

        reposicion.setStockDefectuosoAntes(
                inventario.getStockDefectuoso()
        );

        reposicion.setStockDefectuosoDespues(
                inventario.getStockDefectuoso()
        );

        reposicion.setPrecioUnitario(
                inventario.getPrecioVenta()
        );

        /*
         * IMPORTANTE:
         * La reposición queda vinculada
         * directamente al Kardex defectuoso.
         */
        reposicion.setTipoReferencia(
                "Ajuste"
        );

        reposicion.setIdReferencia(
                idKardexDefectuoso
        );

        /*
         * Mantener trazabilidad del comprobante
         * de la venta original.
         */
        reposicion.setNroComprobante(
                defectuoso.getNroComprobante()
        );

        if (motivo == null
                || motivo.trim().isEmpty()) {

            reposicion.setMotivo(
                    "Reposición por producto defectuoso"
            );

        } else {

            reposicion.setMotivo(
                    motivo.trim()
            );
        }

        /*
         * Registrar movimiento en Kardex.
         */
        if (!kardexDAO.insertar(
                con,
                reposicion
        )) {

            throw new RuntimeException(
                    "No se pudo registrar la reposición "
                    + "en Kardex."
            );
        }

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
    public boolean devolverDineroDefectuoso(
        int idKardexDefectuoso,
        int idUsuario,
        int cantidad,
        String metodoPago,
        String motivo) {

    if (idKardexDefectuoso <= 0
            || idUsuario <= 0
            || cantidad <= 0) {

        return false;
    }

    Connection con = null;

    try {

        con = ConexionBD.conectar();
        con.setAutoCommit(false);

        /*
         * Buscar el movimiento original
         * de producto defectuoso.
         */
        Kardex defectuoso =
                kardexDAO.buscarPorId(
                        con,
                        idKardexDefectuoso
                );

        if (defectuoso == null) {

            throw new RuntimeException(
                    "No se encontró el movimiento "
                    + "de producto defectuoso."
            );
        }

        /*
         * Validar que realmente sea un
         * movimiento de producto defectuoso.
         */
        if (!"Ajuste".equalsIgnoreCase(
                defectuoso.getMovimiento())
                || !"Producto Defectuoso".equalsIgnoreCase(
                        defectuoso.getMotivoMovimiento())
                || !"Venta".equalsIgnoreCase(
                        defectuoso.getTipoReferencia())) {

            throw new RuntimeException(
                    "El movimiento seleccionado "
                    + "no corresponde a un producto defectuoso."
            );
        }

        /*
         * Cantidad originalmente enviada
         * a stock defectuoso.
         */
        int cantidadDefectuosa =
                defectuoso.getStockDefectuosoDespues()
                - defectuoso.getStockDefectuosoAntes();

        if (cantidadDefectuosa <= 0) {

            throw new RuntimeException(
                    "El movimiento no tiene una "
                    + "cantidad defectuosa válida."
            );
        }

        /*
         * Ya devuelto anteriormente en dinero.
         */
        BigDecimal totalDevuelto =
                movimientoCajaDAO
                        .obtenerTotalDevolucionDefectuoso(
                                con,
                                idKardexDefectuoso
                        );
/*
 * Cantidad ya repuesta mediante
 * entrega de producto nuevo.
 */
int cantidadRepuesta =
        kardexDAO.obtenerCantidadRepuestaDefectuoso(
                con,
                idKardexDefectuoso
        );
        /*
         * Valor total de la devolución original.
         */
        BigDecimal precioUnitario =
                defectuoso.getPrecioUnitario();

        if (precioUnitario == null
                || precioUnitario.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "El movimiento defectuoso "
                    + "no tiene un precio válido."
            );
        }

        BigDecimal totalOriginal =
                precioUnitario.multiply(
                        BigDecimal.valueOf(
                                cantidadDefectuosa
                        )
                );

        /*
        /*
 * Calcular cuántas unidades ya fueron
 * resueltas mediante devolución de dinero.
 */
int cantidadDevueltaDinero =
        totalDevuelto
                .divide(
                        precioUnitario,
                        0,
                        java.math.RoundingMode.HALF_UP
                )
                .intValue();

/*
 * Cantidad total de unidades ya resueltas:
 * reposición + devolución de dinero.
 */
int cantidadResuelta =
        cantidadRepuesta
        + cantidadDevueltaDinero;

/*
 * Cantidad que todavía puede resolverse.
 */
int cantidadDisponible =
        cantidadDefectuosa
        - cantidadResuelta;

if (cantidadDisponible <= 0) {

    throw new RuntimeException(
            "La devolución ya fue completada."
    );
}

/*
 * No permitir resolver más unidades
 * de las que todavía están pendientes.
 */
if (cantidad > cantidadDisponible) {

    throw new RuntimeException(
            "La cantidad solicitada supera "
            + "la cantidad pendiente de resolución. "
            + "Pendiente: "
            + cantidadDisponible
    );
}

        /*
         * Monto de esta devolución.
         */
        BigDecimal montoDevolucion =
                precioUnitario.multiply(
                        BigDecimal.valueOf(
                                cantidad
                        )
                );

        BigDecimal nuevoTotalDevuelto =
                totalDevuelto.add(
                        montoDevolucion
                );

        /*
         * No permitir devolver más dinero
         * que el valor de las unidades defectuosas.
         */
        if (nuevoTotalDevuelto.compareTo(
                totalOriginal) > 0) {

            throw new RuntimeException(
                    "El monto de devolución supera "
                    + "el valor pendiente."
            );
        }

        /*
         * Buscar Caja abierta usando
         * la misma conexión.
         */
        pe.ferreteria.modelo.Caja caja =
                cajaDAO.buscarCajaAbierta(
                        con
                );

        if (caja == null) {

            throw new RuntimeException(
                    "No existe una Caja abierta "
                    + "para registrar la devolución."
            );
        }

        /*
         * Nuevos totales de Caja.
         */
        BigDecimal nuevoTotalSalidas =
                caja.getTotalSalidas()
                .add(montoDevolucion);

        BigDecimal montoEsperado =
                caja.getMontoApertura()
                .add(caja.getTotalIngresos())
                .subtract(nuevoTotalSalidas);

        /*
         * Crear movimiento de Caja.
         */
        pe.ferreteria.modelo.MovimientoCaja movimiento =
                new pe.ferreteria.modelo.MovimientoCaja();

        movimiento.setIdCaja(
                caja.getIdCaja()
        );

        movimiento.setIdUsuario(
                idUsuario
        );

        movimiento.setFechaMovimiento(
                java.time.LocalDateTime.now()
        );

        movimiento.setTipoMovimiento(
                "Salida"
        );

        movimiento.setConcepto(
                "Devolucion"
        );

        movimiento.setMonto(
                montoDevolucion
        );

        movimiento.setMetodoPago(
                metodoPago == null
                        || metodoPago.trim().isEmpty()
                        ? "Efectivo"
                        : metodoPago.trim()
        );

        /*
         * Trazabilidad:
         * Movimiento de Caja
         * → Kardex defectuoso
         * → Venta original.
         */
        movimiento.setTipoReferencia(
                "Kardex"
        );

        movimiento.setIdReferencia(
                idKardexDefectuoso
        );

        movimiento.setIdVenta(
                defectuoso.getIdReferencia()
        );

        movimiento.setNroComprobante(
                defectuoso.getNroComprobante()
        );

        if (motivo == null
                || motivo.trim().isEmpty()) {

            movimiento.setDescripcion(
                    "Devolución de dinero "
                    + "por producto defectuoso"
            );

        } else {

            movimiento.setDescripcion(
                    motivo.trim()
            );
        }

        movimiento.setActivo(true);

        /*
         * Registrar Movimiento_Caja.
         */
        if (!movimientoCajaDAO.insertar(
                con,
                movimiento
        )) {

            throw new RuntimeException(
                    "No se pudo registrar "
                    + "la devolución en Caja."
            );
        }

        /*
         * Actualizar totales de Caja.
         */
        if (!cajaDAO.actualizarTotales(
                con,
                caja.getIdCaja(),
                caja.getTotalIngresos(),
                nuevoTotalSalidas,
                montoEsperado
        )) {

            throw new RuntimeException(
                    "No se pudieron actualizar "
                    + "los totales de la Caja."
            );
        }

        /*
         * IMPORTANTE:
         * Aquí NO se modifica Kardex.
         * La devolución es solamente monetaria.
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
    public List<Kardex> listarMermasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return kardexDAO.listarMermasParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
    public List<MermaReporte> listarMermaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return kardexDAO.listarMermaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
}
