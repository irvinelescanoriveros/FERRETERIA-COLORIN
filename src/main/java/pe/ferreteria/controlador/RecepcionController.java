package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.Recepcion;
import pe.ferreteria.servicio.RecepcionService;

import java.util.List;

/**
 * Controlador (MVC) de Recepcion: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class RecepcionController {

    private final RecepcionService recepcionService;

    public RecepcionController() {
        recepcionService = new RecepcionService();
    }

    public List<Compra> listarComprasPendientesRecepcion() {
        return recepcionService.listarComprasPendientesRecepcion();
    }

    public int registrarRecepcion(Recepcion recepcion) {
        return recepcionService.registrarRecepcion(recepcion);
    }
    public List<pe.ferreteria.modelo.DetalleRecepcion> listarDetallesPorRecepcion(
        int idRecepcion) {

    return recepcionService.listarDetallesPorRecepcion(idRecepcion);
}

public boolean registrarDetalle(
        pe.ferreteria.modelo.DetalleRecepcion detalle) {

    return recepcionService.registrarDetalle(detalle);
}
public java.util.List<pe.ferreteria.modelo.DetalleCompra> listarDetallesCompra(
        int idCompra) {

    return recepcionService.listarDetallesCompra(idCompra);
}
public Recepcion buscarUltimaPorCompra(int idCompra) {
    return recepcionService.buscarUltimaPorCompra(idCompra);
}
public pe.ferreteria.modelo.DetalleRecepcion buscarDetalleRecepcion(
        int idRecepcion,
        int idDetalleCompra) {

    return recepcionService.buscarDetalleRecepcion(
            idRecepcion,
            idDetalleCompra
    );
}
public boolean actualizarDetalle(
        pe.ferreteria.modelo.DetalleRecepcion detalle) {

    return recepcionService.actualizarDetalle(detalle);
}
public boolean actualizarEstadoSegunDetalles(int idRecepcion) {

    return recepcionService.actualizarEstadoSegunDetalles(idRecepcion);
}
public int completarRecepcionConInventarioYKardex(
        pe.ferreteria.modelo.Compra compra,
        pe.ferreteria.modelo.DetalleCompra detalleCompra,
        pe.ferreteria.modelo.Producto producto,
        pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion,
        int idRecepcion,
        int idUsuario,
        int cantidadRecibida) {

    return recepcionService
            .completarRecepcionConInventarioYKardex(
                    compra,
                    detalleCompra,
                    producto,
                    detalleRecepcion,
                    idRecepcion,
                    idUsuario,
                    cantidadRecibida
            );
}

public int procesarEscaneo(Compra compra,
        pe.ferreteria.modelo.DetalleCompra detalle,
        pe.ferreteria.modelo.Producto producto,
        int idRecepcion, int idUsuario) {
    return recepcionService.procesarEscaneo(
            compra, detalle, producto, idRecepcion, idUsuario);
}

public String corregirRecepcion(int idRecepcion, Compra compra,
        pe.ferreteria.modelo.DetalleCompra detalleCompra,
        pe.ferreteria.modelo.Producto producto, int idUsuario,
        boolean ingresarComoBueno, String comentario) {
    return recepcionService.corregirRecepcion(idRecepcion, compra,
            detalleCompra, producto, idUsuario, ingresarComoBueno, comentario);
}

public String observarRecepcion(int idRecepcion, int idDetalleCompra,
        int cantidadDefectuosa, String observacion) {
    return recepcionService.observarRecepcion(
            idRecepcion, idDetalleCompra, cantidadDefectuosa, observacion);
}
}
