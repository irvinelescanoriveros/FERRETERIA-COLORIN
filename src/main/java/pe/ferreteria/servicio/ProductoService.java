package pe.ferreteria.servicio;

import pe.ferreteria.dao.ProductoDAO;
import pe.ferreteria.modelo.Producto;
import java.util.List;

/**
 * Capa de servicio de Producto: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this(new ProductoDAO());
    }

    /*
 * Constructor con inyección de dependencias.
 * Permite probar el servicio con un DAO simulado (Mockito).
     */
    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public boolean registrar(Producto producto) {
        return productoDAO.insertar(producto);
    }

    public List<Producto> listar() {
        return productoDAO.listar();
    }

    public List<Producto> listarTodos() {
        return productoDAO.listarTodos();
    }

    public boolean actualizar(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public boolean cambiarEstado(int idProducto, boolean activo) {
        if (idProducto <= 0) {
            return false;
        }
        return productoDAO.cambiarEstado(idProducto, activo);
    }

    public boolean existeCodigo(String codigo, int idProducto) {
        return productoDAO.existeCodigo(codigo, idProducto);
    }

    public Producto buscarPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
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
