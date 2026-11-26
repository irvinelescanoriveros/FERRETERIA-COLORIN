
package pe.ferreteria.servicio;

import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.Registro;
import pe.ferreteria.infraestructura.Seguridad;
import pe.ferreteria.modelo.Usuario;

import java.util.List;

public class UsuarioService {

    private static final java.util.logging.Logger LOG =
            Registro.de(UsuarioService.class);

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        usuarioDAO = new UsuarioDAO();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public boolean registrar(Usuario usuario) {

        if (usuario == null || usuario.getPassword() == null
                || usuario.getPassword().isEmpty()) {
            return false;
        }

        usuario.setPassword(Seguridad.hashear(usuario.getPassword()));

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

        if (idUsuario <= 0 || nuevaPassword == null
                || nuevaPassword.isEmpty()) {
            return false;
        }

        return usuarioDAO.cambiarPassword(
                idUsuario,
                Seguridad.hashear(nuevaPassword)
        );
    }

    public Usuario autenticar(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDAO.buscarActivoPorUsername(username);

        if (usuario == null) {
            Seguridad.simularVerificacion(password);
            LOG.warning("Login fallido: usuario inexistente o inactivo usuario=" + username);
            return null;
        }

        String guardada = usuario.getPassword();

        if (!Seguridad.verificar(password, guardada)) {
            LOG.warning("Login fallido: contrasena incorrecta usuario=" + username);
            return null;
        }

        if (Seguridad.requiereActualizacion(guardada)) {
            usuarioDAO.cambiarPassword(
                    usuario.getIdUsuario(),
                    Seguridad.hashear(password)
            );
        }

        LOG.info("Login correcto usuario=" + username + " rol=" + usuario.getRol());

        usuario.setPassword(null);

        return usuario;
    }
}
