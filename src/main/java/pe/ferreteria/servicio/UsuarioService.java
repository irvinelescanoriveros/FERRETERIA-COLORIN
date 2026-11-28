
package pe.ferreteria.servicio;

import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.Registro;
import pe.ferreteria.infraestructura.Seguridad;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;

import java.util.List;

/**
 * Capa de servicio de Usuario: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
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

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public List<Usuario> listarKardex() {
        return usuarioDAO.listarKardex();
    }

    public boolean actualizar(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean cambiarEstado(int idUsuario, boolean activo) {

        if (idUsuario <= 0) {
            return false;
        }

        if (!activo && idUsuario == SesionUsuario.getIdUsuario()) {
            LOG.warning("Intento de desactivar la propia cuenta usuario_id="
                    + idUsuario);
            return false;
        }

        boolean cambiado = usuarioDAO.cambiarEstado(idUsuario, activo);
        if (cambiado) {
            LOG.info("Estado de usuario cambiado usuario_id=" + idUsuario
                    + " activo=" + activo);
        }

        return cambiado;
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
    /*
 * Actualiza la propia cuenta del usuario con sesion iniciada.
 * Devuelve null si todo salio bien, o el mensaje del problema.
 * nuevaPassword vacia o null = la contraseña no cambia.
 */
public String actualizarMiCuenta(String nombre, String apellidos,
        String correo, String passwordActual, String nuevaPassword) {

    int idUsuario = SesionUsuario.getIdUsuario();

    if (idUsuario <= 0) {
        return "No hay una sesión iniciada.";
    }

    if (nombre == null || nombre.isBlank()
            || apellidos == null || apellidos.isBlank()) {
        return "Ingrese nombre y apellidos.";
    }

    String correoLimpio = correo == null ? "" : correo.trim();

    if (!correoLimpio.isEmpty() && !correoLimpio.contains("@")) {
        return "El correo no es válido.";
    }

    if (passwordActual == null || passwordActual.isEmpty()) {
        return "Ingrese su contraseña actual.";
    }

    String guardada = usuarioDAO.obtenerPassword(idUsuario);

    if (guardada == null || !Seguridad.verificar(passwordActual, guardada)) {
        LOG.warning("Cambio de cuenta rechazado: contrasena actual incorrecta usuario_id="
                + idUsuario);
        return "La contraseña actual es incorrecta.";
    }

    String nuevoHash = null;
    boolean cambiaPassword = nuevaPassword != null && !nuevaPassword.isEmpty();

    if (cambiaPassword) {

        if (nuevaPassword.length() < 6) {
            return "La nueva contraseña debe tener al menos 6 caracteres.";
        }

        if (nuevaPassword.equals(passwordActual)) {
            return "La nueva contraseña debe ser distinta de la actual.";
        }

        nuevoHash = Seguridad.hashear(nuevaPassword);
    }

    boolean guardado = usuarioDAO.actualizarPerfil(
            idUsuario,
            nombre.trim(),
            apellidos.trim(),
            correoLimpio,
            nuevoHash
    );

    if (!guardado) {
        return "No se pudieron guardar los cambios.";
    }

    // Se refresca el usuario de la sesion para el encabezado
    Usuario actual = SesionUsuario.getUsuarioActual();

    if (actual != null) {
        actual.setNombre(nombre.trim());
        actual.setApellidos(apellidos.trim());
        actual.setCorreo(correoLimpio);
    }

    LOG.info("Cuenta actualizada usuario_id=" + idUsuario
            + " cambioPassword=" + cambiaPassword);

    return null;
}
}
