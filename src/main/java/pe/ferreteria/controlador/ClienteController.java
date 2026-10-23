package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Cliente;
import pe.ferreteria.servicio.ClienteService;

import java.util.List;

public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController() {
        clienteService = new ClienteService();
    }

    public boolean registrar(Cliente cliente) {
        return clienteService.registrar(cliente);
    }

    public List<Cliente> listar() {
        return clienteService.listar();
    }

    public boolean actualizar(Cliente cliente) {
        return clienteService.actualizar(cliente);
    }

    public boolean eliminar(int idCliente) {
        return clienteService.eliminar(idCliente);
    }

    public boolean existeDni(String dni, int idCliente) {
        return clienteService.existeDni(dni, idCliente);
    }

    public boolean existeRuc(String ruc, int idCliente) {
        return clienteService.existeRuc(ruc, idCliente);
    }
    
public Cliente buscarPorId(int idCliente) {
    return clienteService.buscarPorId(idCliente);
}


}