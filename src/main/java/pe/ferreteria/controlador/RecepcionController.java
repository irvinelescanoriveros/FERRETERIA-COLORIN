package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.Recepcion;
import pe.ferreteria.servicio.RecepcionService;

import java.util.List;

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
}