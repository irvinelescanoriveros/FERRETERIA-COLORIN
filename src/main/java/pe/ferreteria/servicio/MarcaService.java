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
        this(new MarcaDAO());
    }

    /*
     * Constructor con inyección de dependencias.
     * Permite probar el servicio con un DAO simulado (Mockito).
     */
    public MarcaService(MarcaDAO marcaDAO) {
        this.marcaDAO = marcaDAO;
    }

    public boolean registrar(Marca marca) {
        return marcaDAO.insertar(marca);
    }

    public List<Marca> listar() {
        return marcaDAO.listar();
    }

    public boolean existeNombre(String nombre, int idMarca) {
        return marcaDAO.existeNombre(nombre, idMarca);
    }

    public boolean actualizar(Marca marca) {

        if (marca == null || marca.getIdMarca() <= 0) {
            return false;
        }

        String nombre = marca.getNombre() == null
                ? ""
                : marca.getNombre().trim();

        if (nombre.isEmpty()) {
            return false;
        }

        // No se permite repetir el nombre de otro registro
        if (marcaDAO.existeNombre(nombre, marca.getIdMarca())) {
            return false;
        }

        Marca cambio = new Marca();
        cambio.setIdMarca(marca.getIdMarca());
        cambio.setNombre(nombre);

        return marcaDAO.actualizar(cambio);
    }
}