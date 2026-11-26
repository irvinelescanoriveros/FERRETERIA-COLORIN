package pe.ferreteria.servicio;

import pe.ferreteria.dao.CategoriaDAO;
import pe.ferreteria.modelo.Categoria;

import java.util.List;

/**
 * Capa de servicio de Categoria: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        categoriaDAO = new CategoriaDAO();
    }

    public boolean registrar(Categoria categoria) {
        return categoriaDAO.insertar(categoria);
    }

    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    public boolean eliminar(int idCategoria) {
        return categoriaDAO.eliminar(idCategoria);
    }

    public boolean existeNombre(String nombre, int idCategoria) {
        return categoriaDAO.existeNombre(nombre, idCategoria);
    }
}