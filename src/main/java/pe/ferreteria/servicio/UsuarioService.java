package pe.ferreteria.servicio;

import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.modelo.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        usuarioDAO = new UsuarioDAO();
    }
    public UsuarioService(UsuarioDAO usuarioDAO) {
    this.usuarioDAO = usuarioDAO;
}

    public boolean registrar(Usuario usuario) {
        return usuarioDAO.insertar(usuario);
    }

    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }
    public List<Usuario> listarKardex() {
    return usuarioDAO.listarKardex();
}

    public boolean actualizar(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminar(int idUsuario) {
        return usuarioDAO.eliminar(idUsuario);
    }
    public boolean existeUsername(String username) {
    return usuarioDAO.existeUsername(username);
}

public boolean cambiarPassword(int idUsuario, String nuevaPassword) {
    return usuarioDAO.cambiarPassword(idUsuario, nuevaPassword);
}
public Usuario autenticar(String username, String password) {

    return usuarioDAO.autenticar(
            username,
            password
    );
}
}