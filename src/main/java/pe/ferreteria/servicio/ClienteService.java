package pe.ferreteria.servicio;

import pe.ferreteria.dao.ClienteDAO;
import pe.ferreteria.modelo.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        clienteDAO = new ClienteDAO();
    }

    public boolean registrar(Cliente cliente) {
        return clienteDAO.insertar(cliente);
    }

    public List<Cliente> listar() {
        return clienteDAO.listar();
    }

    public boolean actualizar(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    public boolean eliminar(int idCliente) {
        return clienteDAO.eliminar(idCliente);
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