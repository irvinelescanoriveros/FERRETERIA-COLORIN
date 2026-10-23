package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Producto;
import pe.ferreteria.servicio.ProductoService;
import java.util.List;

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

    public boolean actualizar(Producto producto) {
        return productoService.actualizar(producto);
    }

    public boolean eliminar(int idProducto) {
        return productoService.eliminar(idProducto);
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