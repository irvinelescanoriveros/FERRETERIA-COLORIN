
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
        this(new CategoriaDAO());
    }

    /*
     * Constructor con inyección de dependencias.
     * Permite probar el servicio con un DAO simulado (Mockito).
     */
    public CategoriaService(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public boolean registrar(Categoria categoria) {
        return categoriaDAO.insertar(categoria);
    }

    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    public boolean existeNombre(String nombre, int idCategoria) {
        return categoriaDAO.existeNombre(nombre, idCategoria);
    }

    public boolean actualizar(Categoria categoria) {

        if (categoria == null || categoria.getIdCategoria() <= 0) {
            return false;
        }

        String nombre = categoria.getNombre() == null
                ? ""
                : categoria.getNombre().trim();

        if (nombre.isEmpty()) {
            return false;
        }

        // No se permite repetir el nombre de otro registro
        if (categoriaDAO.existeNombre(nombre, categoria.getIdCategoria())) {
            return false;
        }

        Categoria cambio = new Categoria();
        cambio.setIdCategoria(categoria.getIdCategoria());
        cambio.setNombre(nombre);

        return categoriaDAO.actualizar(cambio);
    }
}