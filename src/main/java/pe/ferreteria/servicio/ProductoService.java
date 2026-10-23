package pe.ferreteria.servicio;

import pe.ferreteria.dao.ProductoDAO;
import pe.ferreteria.modelo.Producto;
import java.util.List;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        productoDAO = new ProductoDAO();
    }

    public boolean registrar(Producto producto) {
        return productoDAO.insertar(producto);
    }

    public List<Producto> listar() {
        return productoDAO.listar();
    }

    public boolean actualizar(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public boolean eliminar(int idProducto) {
        return productoDAO.eliminar(idProducto);
    }

    public boolean existeCodigo(String codigo, int idProducto) {
        return productoDAO.existeCodigo(codigo, idProducto);
    }
    public Producto buscarParaDashboard(int idProducto) {

    if (idProducto <= 0) {
        return null;
    }

    return productoDAO.buscarParaDashboard(
            idProducto
    );
}
}