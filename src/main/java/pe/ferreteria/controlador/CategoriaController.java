package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.servicio.CategoriaService;

import java.util.List;

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

    public boolean eliminar(int idCategoria) {
        return categoriaService.eliminar(idCategoria);
    }

    public boolean existeNombre(String nombre, int idCategoria) {
        return categoriaService.existeNombre(nombre, idCategoria);
    }
}