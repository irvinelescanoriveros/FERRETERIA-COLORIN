package pe.ferreteria.servicio;

import pe.ferreteria.dao.ClienteDAO;
import pe.ferreteria.modelo.Cliente;

import java.util.List;

/**
 * Capa de servicio de Cliente: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class ClienteService {

    private static final int ID_CONSUMIDOR_FINAL = 1;

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this(new ClienteDAO());
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public boolean registrar(Cliente cliente) {
        return clienteDAO.insertar(cliente);
    }

    public List<Cliente> listar() {
        return clienteDAO.listar();
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public boolean actualizar(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    public boolean cambiarEstado(int idCliente, boolean activo) {
        if (idCliente <= 0) {
            return false;
        }
        if (!activo && idCliente == ID_CONSUMIDOR_FINAL) {
            return false;
        }
        return clienteDAO.cambiarEstado(idCliente, activo);
    }

    public boolean existeDni(String dni, int idCliente) {
        return clienteDAO.existeDni(dni, idCliente);
    }

    public boolean existeRuc(String ruc, int idCliente) {
        return clienteDAO.existeRuc(ruc, idCliente);
    }
    
public Cliente buscarPorId(int idCliente) {
    return clienteDAO.buscarPorId(idCliente);
}


}
