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

/**
 * Capa de servicio de Recepcion: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
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
            int idRecepcion, int idUsuario, int cantidadRecibida) {
        if (compra == null || detalleCompra == null || producto == null
                || detalleRecepcion == null || cantidadRecibida <= 0) return 0;
        DetalleRecepcion actual = detalleRecepcionDAO.buscarPorRecepcionYDetalleCompra(
                idRecepcion, detalleCompra.getIdDetalle());
        int registradas = actual == null || actual.getCantidadRecibida() == null
                ? 0 : actual.getCantidadRecibida();
        while (registradas < cantidadRecibida) {
            int nuevoTotal = procesarEscaneo(compra, detalleCompra, producto, idRecepcion, idUsuario);
            if (nuevoTotal <= registradas) return 0;
            registradas = nuevoTotal;
        }
        return registradas;
    }
    /** Cada lectura registra una unidad y actualiza inventario/Kardex en una transacción. */
    public int procesarEscaneo(Compra compra,
            pe.ferreteria.modelo.DetalleCompra detalleCompra,
            pe.ferreteria.modelo.Producto producto, int idRecepcion, int idUsuario) {
        if (compra == null || detalleCompra == null || producto == null
                || idRecepcion <= 0 || idUsuario <= 0
                || detalleCompra.getIdCompra() != compra.getIdCompra()
                || detalleCompra.getIdProducto() != producto.getIdProducto()) return 0;
        Connection con = null;
        try {
            con = ConexionBD.conectar();
            con.setAutoCommit(false);
            try (java.sql.PreparedStatement ps = con.prepareStatement(
                    "SELECT id_compra FROM Recepcion WHERE id_recepcion = ? FOR UPDATE")) {
                ps.setInt(1, idRecepcion);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != compra.getIdCompra())
                        throw new IllegalStateException("La recepción no corresponde a la compra seleccionada.");
                }
            }
            DetalleRecepcion detalle = detalleRecepcionDAO.buscarPorRecepcionYDetalleCompra(
                    con, idRecepcion, detalleCompra.getIdDetalle());
            if (detalle == null) {
                detalle = new DetalleRecepcion();
                detalle.setIdRecepcion(idRecepcion);
                detalle.setIdDetalleCompra(detalleCompra.getIdDetalle());
                detalle.setEstado("Pendiente");
                detalle.setCantidadRecibida(0);
                if (!detalleRecepcionDAO.insertar(con, detalle))
                    throw new IllegalStateException("No se pudo crear el detalle de recepción.");
            }
            if ("Completada".equalsIgnoreCase(detalle.getEstado()))
                throw new IllegalStateException("Este producto ya está completado.");
            int recibidas = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
            if (recibidas >= detalleCompra.getCantidad() - detalle.getCantidadDefectuosa())
                throw new IllegalStateException("Ya se registró toda la cantidad permitida para este lote.");

            java.math.BigDecimal precio = detalleCompra.getPrecioCompra() == null
                    ? java.math.BigDecimal.ZERO : detalleCompra.getPrecioCompra();
            pe.ferreteria.modelo.Inventario inv = inventarioDAO.buscarPorProductoYLote(
                    con, producto.getIdProducto(), detalleCompra.getLote());
            int idInventario, stockAntes;
            if (inv == null) {
                inv = new pe.ferreteria.modelo.Inventario();
                inv.setIdProducto(producto.getIdProducto());
                inv.setIdProveedor(compra.getIdProveedor());
                inv.setLote(detalleCompra.getLote());
                inv.setStock(1);
                inv.setStockDefectuoso(0);
                inv.setPrecioCompra(precio);
                inv.setPrecioVenta(precio.multiply(new java.math.BigDecimal("1.20")));
                inv.setDescuentoActivo(false);
                inv.setDescuentoPorcentaje(java.math.BigDecimal.ZERO);
                inv.setFechaVencimiento(detalleCompra.getFechaVencimiento());
                inv.setEstante(producto.getEstante());
                inv.setNivel(producto.getNivel());
                inv.setActivo(true);
                idInventario = inventarioDAO.insertar(con, inv);
                stockAntes = 0;
            } else {
                idInventario = inv.getIdInventario();
                stockAntes = inv.getStock();
                if (!inventarioDAO.aumentarStock(con, idInventario, 1))
                    throw new IllegalStateException("No se pudo incrementar el inventario.");
            }
            if (idInventario <= 0) throw new IllegalStateException("No se pudo registrar el lote en inventario.");

            detalle.setCantidadRecibida(recibidas + 1);
            detalle.setFechaConfirmacion(java.time.LocalDateTime.now());
            if (recibidas + 1 >= detalleCompra.getCantidad()
                    && detalle.getCantidadDefectuosa() == 0) {
                detalle.setEstado("Completada");
            } else if (detalle.getCantidadDefectuosa() > 0) {
                detalle.setEstado("Observada");
            } else {
                detalle.setEstado("Pendiente");
            }
            if (!detalleRecepcionDAO.actualizar(con, detalle))
                throw new IllegalStateException("No se pudo actualizar el detalle de recepción.");

            pe.ferreteria.modelo.Kardex entrada = kardexDAO.buscarPorDetalleRecepcion(
                    con, detalle.getIdDetalleRecepcion());
            if (entrada == null) {
                entrada = new pe.ferreteria.modelo.Kardex();
                entrada.setIdProducto(producto.getIdProducto());
                entrada.setIdInventario(idInventario);
                entrada.setIdDetalleRecepcion(detalle.getIdDetalleRecepcion());
                entrada.setIdUsuario(idUsuario);
                entrada.setFechaMovimiento(java.time.LocalDateTime.now());
                entrada.setMovimiento("Entrada");
                entrada.setMotivoMovimiento("Compra");
                entrada.setCantidad(1);
                entrada.setStockAntes(stockAntes);
                entrada.setStockDespues(stockAntes + 1);
                entrada.setStockDefectuosoAntes(0);
                entrada.setStockDefectuosoDespues(0);
                entrada.setPrecioUnitario(precio);
                entrada.setTipoReferencia("Compra");
                entrada.setIdReferencia(compra.getIdCompra());
                entrada.setNroComprobante(compra.getNroComprobante());
                if (!kardexDAO.insertarEntradaRecepcion(con, entrada))
                    throw new IllegalStateException("No se pudo registrar la entrada en Kardex.");
            } else {
                entrada.setCantidad(entrada.getCantidad() + 1);
                entrada.setStockDespues(stockAntes + 1);
                if (!kardexDAO.actualizarEntradaRecepcion(con, entrada))
                    throw new IllegalStateException("No se pudo actualizar Kardex.");
            }
            if (!recepcionDAO.actualizarEstadoSegunDetalles(con, idRecepcion))
                throw new IllegalStateException("No se pudo actualizar el estado de recepción.");
            con.commit();
            return recibidas + 1;
        } catch (IllegalStateException e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) { }
            return 0;
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) { }
            e.printStackTrace();
            return 0;
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception ignored) { }
        }
    }

    public String corregirRecepcion(int idRecepcion,
            pe.ferreteria.modelo.Compra compra,
            pe.ferreteria.modelo.DetalleCompra detalleCompra,
            pe.ferreteria.modelo.Producto producto,
            int idUsuario, boolean ingresarComoBueno, String comentario) {
        if (idRecepcion <= 0 || compra == null || detalleCompra == null
                || producto == null || idUsuario <= 0)
            return "Datos de la corrección inválidos.";
        if (comentario == null || comentario.trim().isEmpty())
            return "Escriba su comentario sobre la decisión.";
        comentario = comentario.trim();
        if (comentario.length() > 255)
            return "El comentario no puede superar 255 caracteres.";

        Connection con = null;
        try {
            con = ConexionBD.conectar();
            con.setAutoCommit(false);
            try (java.sql.PreparedStatement ps = con.prepareStatement(
                    "SELECT id_compra FROM Recepcion WHERE id_recepcion = ? FOR UPDATE")) {
                ps.setInt(1, idRecepcion);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != compra.getIdCompra())
                        throw new IllegalStateException("La recepción no corresponde a la compra.");
                }
            }
            pe.ferreteria.modelo.DetalleRecepcion detalle =
                    detalleRecepcionDAO.buscarPorRecepcionYDetalleCompra(
                            con, idRecepcion, detalleCompra.getIdDetalle());
            if (detalle == null || !"Observada".equalsIgnoreCase(detalle.getEstado())
                    || detalle.getCantidadDefectuosa() <= 0)
                throw new IllegalStateException("Esta fila no tiene unidades observadas para corregir.");

            int observadas = detalle.getCantidadDefectuosa();
            int recibidas = detalle.getCantidadRecibida() == null ? 0 : detalle.getCantidadRecibida();
            int faltanBuenas = detalleCompra.getCantidad() - observadas - recibidas;
            if (faltanBuenas > 0)
                throw new IllegalStateException("Faltan " + faltanBuenas + " unidades buenas por recibir.");

            java.math.BigDecimal precio = detalleCompra.getPrecioCompra() == null
                    ? java.math.BigDecimal.ZERO : detalleCompra.getPrecioCompra();
            pe.ferreteria.modelo.Inventario inventario = inventarioDAO.buscarPorProductoYLote(
                    con, producto.getIdProducto(), detalleCompra.getLote());
            int idInventario, stockAntes, defectuosoAntes;
            if (inventario == null) {
                inventario = new pe.ferreteria.modelo.Inventario();
                inventario.setIdProducto(producto.getIdProducto());
                inventario.setIdProveedor(compra.getIdProveedor());
                inventario.setLote(detalleCompra.getLote());
                inventario.setStock(ingresarComoBueno ? observadas : 0);
                inventario.setStockDefectuoso(ingresarComoBueno ? 0 : observadas);
                inventario.setPrecioCompra(precio);
                inventario.setPrecioVenta(precio.multiply(new java.math.BigDecimal("1.20")));
                inventario.setDescuentoActivo(false);
                inventario.setDescuentoPorcentaje(java.math.BigDecimal.ZERO);
                inventario.setFechaVencimiento(detalleCompra.getFechaVencimiento());
                inventario.setEstante(producto.getEstante());
                inventario.setNivel(producto.getNivel());
                inventario.setActivo(true);
                idInventario = inventarioDAO.insertar(con, inventario);
                stockAntes = 0;
                defectuosoAntes = 0;
            } else {
                if (!inventario.isActivo())
                    throw new IllegalStateException("El lote está inactivo en Inventario.");
                idInventario = inventario.getIdInventario();
                stockAntes = inventario.getStock();
                defectuosoAntes = inventario.getStockDefectuoso();
                boolean actualizado = ingresarComoBueno
                        ? inventarioDAO.aumentarStock(con, idInventario, observadas)
                        : inventarioDAO.aumentarStockDefectuoso(con, idInventario, observadas);
                if (!actualizado)
                    throw new IllegalStateException("No se pudo actualizar el inventario.");
            }
            if (idInventario <= 0)
                throw new IllegalStateException("No se pudo guardar el lote en Inventario.");

            pe.ferreteria.modelo.Kardex movimiento = new pe.ferreteria.modelo.Kardex();
            movimiento.setIdProducto(producto.getIdProducto());
            movimiento.setIdInventario(idInventario);
            movimiento.setIdUsuario(idUsuario);
            movimiento.setFechaMovimiento(java.time.LocalDateTime.now());
            movimiento.setCantidad(observadas);
            movimiento.setPrecioUnitario(precio);
            movimiento.setTipoReferencia("Compra");
            movimiento.setIdReferencia(compra.getIdCompra());
            movimiento.setNroComprobante(compra.getNroComprobante());
            movimiento.setMotivo(comentario);
            if (ingresarComoBueno) {
                movimiento.setMovimiento("Entrada");
                movimiento.setMotivoMovimiento("Compra");
                movimiento.setStockAntes(stockAntes);
                movimiento.setStockDespues(stockAntes + observadas);
                movimiento.setStockDefectuosoAntes(defectuosoAntes);
                movimiento.setStockDefectuosoDespues(defectuosoAntes);
            } else {
                movimiento.setMovimiento("Ajuste");
                movimiento.setMotivoMovimiento("Producto Defectuoso");
                movimiento.setStockAntes(stockAntes);
                movimiento.setStockDespues(stockAntes);
                movimiento.setStockDefectuosoAntes(defectuosoAntes);
                movimiento.setStockDefectuosoDespues(defectuosoAntes + observadas);
            }
            if (!kardexDAO.insertar(con, movimiento))
                throw new IllegalStateException("No se pudo registrar el movimiento correctivo en Kardex.");

            if (ingresarComoBueno) detalle.setCantidadRecibida(detalleCompra.getCantidad());
            detalle.setEstado("Completada");
            detalle.setFechaConfirmacion(java.time.LocalDateTime.now());
            if (!detalleRecepcionDAO.actualizar(con, detalle))
                throw new IllegalStateException("No se pudo cerrar el detalle de recepción.");
            if (!recepcionDAO.actualizarEstadoSegunDetalles(con, idRecepcion))
                throw new IllegalStateException("No se pudo actualizar el estado general de recepción.");
            con.commit();
            return "OK";
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) { }
            e.printStackTrace();
            return e.getMessage() == null ? "No se pudo corregir la recepción." : e.getMessage();
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception ignored) { }
        }
    }

    public String observarRecepcion(int idRecepcion, int idDetalleCompra,
            int cantidadDefectuosa, String observacion) {
        if (idRecepcion <= 0 || idDetalleCompra <= 0)
            return "Datos de la recepción inválidos.";
        if (cantidadDefectuosa <= 0)
            return "La cantidad observada debe ser mayor que cero.";
        if (observacion == null || observacion.trim().isEmpty())
            return "Escriba el motivo de la observación.";

        Connection con = null;
        try {
            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            int cantidadComprada;
            try (java.sql.PreparedStatement ps = con.prepareStatement("""
                    SELECT dc.cantidad
                    FROM Recepcion r
                    JOIN Detalle_Compra dc ON dc.id_compra = r.id_compra
                    WHERE r.id_recepcion = ? AND dc.id_detalle = ?
                    FOR UPDATE
                    """)) {
                ps.setInt(1, idRecepcion);
                ps.setInt(2, idDetalleCompra);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return "El producto no pertenece a esta recepción.";
                    }
                    cantidadComprada = rs.getInt("cantidad");
                }
            }

            DetalleRecepcion detalle = detalleRecepcionDAO
                    .buscarPorRecepcionYDetalleCompra(
                            con, idRecepcion, idDetalleCompra);
            boolean nuevo = detalle == null;
            if (nuevo) {
                detalle = new DetalleRecepcion();
                detalle.setIdRecepcion(idRecepcion);
                detalle.setIdDetalleCompra(idDetalleCompra);
                detalle.setCantidadRecibida(0);
                detalle.setEstado("Pendiente");
            } else if ("Completada".equalsIgnoreCase(detalle.getEstado())) {
                con.rollback();
                return "Esta fila ya está completada.";
            }

            int recibidas = detalle.getCantidadRecibida() == null
                    ? 0 : detalle.getCantidadRecibida();
            int maximoObservable = cantidadComprada - recibidas;
            if (cantidadDefectuosa > maximoObservable) {
                con.rollback();
                return "Cantidad observada inválida. Máximo permitido: "
                        + maximoObservable;
            }

            detalle.setCantidadDefectuosa(cantidadDefectuosa);
            detalle.setObservacion(observacion.trim());
            detalle.setEstado("Observada");
            if (nuevo) {
                if (!detalleRecepcionDAO.insertar(con, detalle))
                    throw new IllegalStateException("No se pudo registrar el detalle observado.");
            } else if (!detalleRecepcionDAO.actualizar(con, detalle)) {
                throw new IllegalStateException("No se pudo actualizar el detalle observado.");
            }
            if (!recepcionDAO.actualizarEstadoSegunDetalles(con, idRecepcion))
                throw new IllegalStateException("No se pudo actualizar el estado general de recepción.");
            con.commit();
            return "OK";
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) { }
            e.printStackTrace();
            return e.getMessage() == null ? "No se pudo registrar la observación." : e.getMessage();
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception ignored) { }
        }
    }
}
