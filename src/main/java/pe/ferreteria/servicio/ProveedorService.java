package pe.ferreteria.servicio;

import pe.ferreteria.dao.ProveedorDAO;
import pe.ferreteria.modelo.Proveedor;

import java.util.List;

/**
 * Capa de servicio de Proveedor: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class ProveedorService {

    private final ProveedorDAO proveedorDAO;

    public ProveedorService() {
        this(new ProveedorDAO());
    }

    public ProveedorService(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public boolean registrar(Proveedor proveedor) {
        return proveedorDAO.insertar(proveedor);
    }

    public List<Proveedor> listar() {
        return proveedorDAO.listar();
    }

    public List<Proveedor> listarTodos() {
        return proveedorDAO.listarTodos();
    }

    public boolean actualizar(Proveedor proveedor) {
        return proveedorDAO.actualizar(proveedor);
    }

    public boolean cambiarEstado(int idProveedor, boolean activo) {
        if (idProveedor <= 0) {
            return false;
        }
        return proveedorDAO.cambiarEstado(idProveedor, activo);
    }

    public boolean existeRuc(String ruc, int idProveedor) {
        return proveedorDAO.existeRuc(ruc, idProveedor);
    }
}
