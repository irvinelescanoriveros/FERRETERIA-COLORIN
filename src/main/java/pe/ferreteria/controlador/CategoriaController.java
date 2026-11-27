package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.servicio.CategoriaService;

import java.util.List;

/**
 * Controlador (MVC) de Categoria: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController() {
        categoriaService = new CategoriaService();
    }

    public boolean registrar(Categoria categoria) {
        return categoriaService.registrar(categoria);
    }

    public List<Categoria> listar() {
        return categoriaService.listar();
    }

    public boolean actualizar(Categoria categoria) {
        return categoriaService.actualizar(categoria);
    }

    public boolean existeNombre(String nombre, int idCategoria) {
        return categoriaService.existeNombre(nombre, idCategoria);
    }
}