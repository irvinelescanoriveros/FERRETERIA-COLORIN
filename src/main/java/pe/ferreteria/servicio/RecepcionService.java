package pe.ferreteria.servicio;

import pe.ferreteria.dao.RecepcionDAO;
import pe.ferreteria.dao.DetalleRecepcionDAO;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleRecepcion;
import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.dao.KardexDAO;
import pe.ferreteria.infraestructura.ConexionBD;

import java.sql.Connection;
import java.util.List;

public class RecepcionService {

    private final RecepcionDAO recepcionDAO;
    private final DetalleRecepcionDAO detalleRecepcionDAO;
    private final InventarioDAO inventarioDAO;
    private final KardexDAO kardexDAO;

    public RecepcionService() {
        recepcionDAO = new RecepcionDAO();
        detalleRecepcionDAO = new DetalleRecepcionDAO();
        inventarioDAO = new InventarioDAO();
        kardexDAO = new KardexDAO();
    }

    public List<Compra> listarComprasPendientesRecepcion() {
        return recepcionDAO.listarComprasPendientesRecepcion();
    }

    public int registrarRecepcion(
            pe.ferreteria.modelo.Recepcion recepcion) {

        return recepcionDAO.insertar(recepcion);
    }

    public List<DetalleRecepcion> listarDetallesPorRecepcion(
            int idRecepcion) {

        return detalleRecepcionDAO.listarPorRecepcion(idRecepcion);
    }

    public boolean registrarDetalle(DetalleRecepcion detalle) {
        return detalleRecepcionDAO.insertar(detalle);
    }

    public java.util.List<pe.ferreteria.modelo.DetalleCompra> listarDetallesCompra(
            int idCompra) {

        pe.ferreteria.dao.DetalleCompraDAO detalleCompraDAO =
                new pe.ferreteria.dao.DetalleCompraDAO();

        return detalleCompraDAO.listarPorCompra(idCompra);
    }

    public pe.ferreteria.modelo.Recepcion buscarUltimaPorCompra(
            int idCompra) {

        return recepcionDAO.buscarUltimaPorCompra(idCompra);
    }

    public pe.ferreteria.modelo.DetalleRecepcion buscarDetalleRecepcion(
            int idRecepcion,
            int idDetalleCompra) {

        return detalleRecepcionDAO.buscarPorRecepcionYDetalleCompra(
                idRecepcion,
                idDetalleCompra
        );
    }

    public boolean actualizarDetalle(
            pe.ferreteria.modelo.DetalleRecepcion detalle) {

        return detalleRecepcionDAO.actualizar(detalle);
    }

    public boolean actualizarEstadoSegunDetalles(int idRecepcion) {

        return recepcionDAO.actualizarEstadoSegunDetalles(idRecepcion);
    }

    public int completarRecepcionConInventarioYKardex(
            pe.ferreteria.modelo.Compra compra,
            pe.ferreteria.modelo.DetalleCompra detalleCompra,
            pe.ferreteria.modelo.Producto producto,
            pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion,
            int idRecepcion,
            int idUsuario,
            int cantidadRecibida) {

        if (compra == null
                || detalleCompra == null
                || producto == null
                || detalleRecepcion == null
                || idRecepcion <= 0
                || idUsuario <= 0
                || cantidadRecibida <= 0) {

            return 0;
        }

        Connection con = null;

        try {

            con = ConexionBD.conectar();

            con.setAutoCommit(false);

            /*
             * 1. Crear el lote en Inventario.
             */
            pe.ferreteria.modelo.Inventario inventario =
                    new pe.ferreteria.modelo.Inventario();

            java.math.BigDecimal precioCompra =
                    detalleCompra.getPrecioCompra();

            if (precioCompra == null) {
                precioCompra = java.math.BigDecimal.ZERO;
            }

            java.math.BigDecimal precioVenta =
                    precioCompra.multiply(
                            new java.math.BigDecimal("1.20")
                    );

            inventario.setIdProducto(
                    detalleCompra.getIdProducto()
            );

            inventario.setIdProveedor(
                    compra.getIdProveedor()
            );

            inventario.setLote(
                    detalleCompra.getLote()
            );

            inventario.setStock(
                    cantidadRecibida
            );

            inventario.setStockDefectuoso(0);

            inventario.setPrecioCompra(
                    precioCompra
            );

            inventario.setPrecioVenta(
                    precioVenta
            );

            inventario.setDescuentoActivo(false);

            inventario.setDescuentoPorcentaje(
                    java.math.BigDecimal.ZERO
            );

            inventario.setFechaVencimiento(
                    detalleCompra.getFechaVencimiento()
            );

            inventario.setEstante(
                    producto.getEstante()
            );

            inventario.setNivel(
                    producto.getNivel()
            );

            inventario.setActivo(true);

            int idInventario =
                    inventarioDAO.insertar(
                            con,
                            inventario
                    );

            if (idInventario <= 0) {

                throw new RuntimeException(
                        "No se pudo registrar el lote en Inventario."
                );
            }

            /*
             * 2. Completar el detalle de recepción.
             */
            boolean detalleActualizado;

            if (detalleRecepcion.getIdDetalleRecepcion() == 0) {

                detalleActualizado =
                        detalleRecepcionDAO.insertar(
                                con,
                                detalleRecepcion
                        );

            } else {

                detalleActualizado =
                        detalleRecepcionDAO.actualizar(
                                con,
                                detalleRecepcion
                        );
            }

            if (!detalleActualizado) {

                throw new RuntimeException(
                        "No se pudo completar el detalle de recepción."
                );
            }

            /*
             * 3. Registrar Entrada en Kardex.
             */
            pe.ferreteria.modelo.Kardex entrada =
                    new pe.ferreteria.modelo.Kardex();

            entrada.setIdProducto(
                    detalleCompra.getIdProducto()
            );

            entrada.setIdInventario(
                    idInventario
            );

            entrada.setIdUsuario(
                    idUsuario
            );

            entrada.setFechaMovimiento(
                    detalleRecepcion.getFechaConfirmacion()
            );

            entrada.setMovimiento(
                    "Entrada"
            );

            /*
             * Motivo estructurado de la entrada.
             */
            entrada.setMotivoMovimiento(
                    "Compra"
            );

            entrada.setCantidad(
                    cantidadRecibida
            );

            entrada.setStockAntes(0);

            entrada.setStockDespues(
                    cantidadRecibida
            );

            entrada.setStockDefectuosoAntes(0);

            entrada.setStockDefectuosoDespues(0);

            entrada.setPrecioUnitario(
                    precioCompra
            );

            entrada.setTipoReferencia(
                    "Compra"
            );

            entrada.setIdReferencia(
                    compra.getIdCompra()
            );

            entrada.setNroComprobante(
                    compra.getNroComprobante()
            );

            entrada.setMotivo(
                    null
            );

            if (!kardexDAO.insertar(
                    con,
                    entrada
            )) {

                throw new RuntimeException(
                        "No se pudo registrar la Entrada en Kardex."
                );
            }

            /*
             * 4. Actualizar estado general de Recepción.
             */
            if (!recepcionDAO.actualizarEstadoSegunDetalles(
                    con,
                    idRecepcion
            )) {

                throw new RuntimeException(
                        "No se pudo actualizar el estado de la recepción."
                );
            }

            /*
             * Todo salió correctamente.
             */
            con.commit();

            return idInventario;

        } catch (Exception e) {

            if (con != null) {

                try {
                    con.rollback();

                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            e.printStackTrace();

            return 0;

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
}