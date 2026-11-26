package pe.ferreteria.servicio;

import pe.ferreteria.dao.MarcaDAO;
import pe.ferreteria.modelo.Marca;

import java.util.List;

/**
 * Capa de servicio de Marca: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class MarcaService {

    private final MarcaDAO marcaDAO;

    public MarcaService() {
        marcaDAO = new MarcaDAO();
    }

    public boolean registrar(Marca marca) {
        return marcaDAO.insertar(marca);
    }

    public List<Marca> listar() {
        return marcaDAO.listar();
    }

    public boolean eliminar(int idMarca) {
        return marcaDAO.eliminar(idMarca);
    }

    public boolean existeNombre(String nombre, int idMarca) {
        return marcaDAO.existeNombre(nombre, idMarca);
    }
}