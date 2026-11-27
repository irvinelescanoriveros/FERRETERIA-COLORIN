package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Marca;
import pe.ferreteria.servicio.MarcaService;

import java.util.List;

/**
 * Controlador (MVC) de Marca: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController() {
        marcaService = new MarcaService();
    }

    public boolean registrar(Marca marca) {
        return marcaService.registrar(marca);
    }

    public List<Marca> listar() {
        return marcaService.listar();
    }

    public boolean actualizar(Marca marca) {
        return marcaService.actualizar(marca);
    }

    public boolean existeNombre(String nombre, int idMarca) {
        return marcaService.existeNombre(nombre, idMarca);
    }
}