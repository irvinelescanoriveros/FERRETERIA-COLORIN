package pe.ferreteria.controlador;

import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

/**
 * Controlador (MVC) de Login: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController() {
        usuarioService = new UsuarioService();
    }

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public boolean iniciarSesion(String username, String password) {
        Usuario usuario = usuarioService.autenticar(username, password);

        if (usuario == null) {
            return false;
        }

        if ("WEB".equals(usuario.getRol())) {
            return false;
        }

        SesionUsuario.iniciarSesion(usuario);
        return true;
    }
}
