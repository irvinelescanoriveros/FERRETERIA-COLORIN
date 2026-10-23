package pe.ferreteria.servicio;

import pe.ferreteria.dao.ProveedorDAO;
import pe.ferreteria.modelo.Proveedor;

import java.util.List;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;

    public ProveedorService() {
        proveedorDAO = new ProveedorDAO();
    }

    public boolean registrar(Proveedor proveedor) {
        return proveedorDAO.insertar(proveedor);
    }

    public List<Proveedor> listar() {
        return proveedorDAO.listar();
    }

    public boolean actualizar(Proveedor proveedor) {
        return proveedorDAO.actualizar(proveedor);
    }

    public boolean eliminar(int idProveedor) {
        return proveedorDAO.eliminar(idProveedor);
    }

    public boolean existeRuc(String ruc, int idProveedor) {
        return proveedorDAO.existeRuc(ruc, idProveedor);
    }
}