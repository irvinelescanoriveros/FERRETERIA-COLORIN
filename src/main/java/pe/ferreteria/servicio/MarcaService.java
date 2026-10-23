package pe.ferreteria.servicio;

import pe.ferreteria.dao.MarcaDAO;
import pe.ferreteria.modelo.Marca;

import java.util.List;

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