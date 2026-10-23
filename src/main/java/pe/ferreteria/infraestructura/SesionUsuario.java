package pe.ferreteria.infraestructura;

import pe.ferreteria.modelo.Usuario;

public class SesionUsuario {

    private static Usuario usuarioActual;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static int getIdUsuario() {

        if (usuarioActual == null) {
            return 0;
        }

        return usuarioActual.getIdUsuario();
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}