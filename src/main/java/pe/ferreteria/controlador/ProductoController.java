package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Producto;
import pe.ferreteria.servicio.ProductoService;
import java.util.List;

/**
 * Controlador (MVC) de Producto: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController() {
        productoService = new ProductoService();
    }

    public boolean registrar(Producto producto) {
        return productoService.registrar(producto);
    }

    public List<Producto> listar() {
        return productoService.listar();
    }

    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    public boolean actualizar(Producto producto) {
        return productoService.actualizar(producto);
    }

    public boolean cambiarEstado(int idProducto, boolean activo) {
        return productoService.cambiarEstado(idProducto, activo);
    }

    public boolean existeCodigo(String codigo, int idProducto) {
        return productoService.existeCodigo(codigo, idProducto);
    }
    public Producto buscarParaDashboard(int idProducto) {

    return productoService.buscarParaDashboard(
            idProducto
    );
}
}
