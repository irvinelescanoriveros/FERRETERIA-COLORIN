package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import java.util.List;

/**
 * Controlador (MVC) de Usuario: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
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

    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioService.actualizar(usuario);
    }

    public boolean cambiarEstado(int idUsuario, boolean activo) {
        return usuarioService.cambiarEstado(idUsuario, activo);
    }

    public boolean existeUsername(String username) {
        return usuarioService.existeUsername(username);
    }

    public boolean cambiarPassword(int idUsuario, String nuevaPassword) {
        return usuarioService.cambiarPassword(idUsuario, nuevaPassword);
    }
    public String actualizarMiCuenta(String nombre, String apellidos,
        String correo, String passwordActual, String nuevaPassword) {

    return usuarioService.actualizarMiCuenta(
            nombre, apellidos, correo, passwordActual, nuevaPassword
    );
}
}
