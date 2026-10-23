package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import java.util.List;

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController() {
        usuarioService = new UsuarioService();
    }

    public boolean registrar(Usuario usuario) {
        return usuarioService.registrar(usuario);
    }

    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioService.actualizar(usuario);
    }

    public boolean eliminar(int idUsuario) {
        return usuarioService.eliminar(idUsuario);
    }

    public boolean existeUsername(String username) {
        return usuarioService.existeUsername(username);
    }

    public boolean cambiarPassword(int idUsuario, String nuevaPassword) {
        return usuarioService.cambiarPassword(idUsuario, nuevaPassword);
    }
}