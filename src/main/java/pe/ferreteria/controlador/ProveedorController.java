package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Proveedor;
import pe.ferreteria.servicio.ProveedorService;

import java.util.List;

/**
 * Controlador (MVC) de Proveedor: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController() {
        proveedorService = new ProveedorService();
    }

    public boolean registrar(Proveedor proveedor) {
        return proveedorService.registrar(proveedor);
    }

    public List<Proveedor> listar() {
        return proveedorService.listar();
    }

    public boolean actualizar(Proveedor proveedor) {
        return proveedorService.actualizar(proveedor);
    }

    public boolean eliminar(int idProveedor) {
        return proveedorService.eliminar(idProveedor);
    }

    public boolean existeRuc(String ruc, int idProveedor) {
        return proveedorService.existeRuc(ruc, idProveedor);
    }
}