package pe.ferreteria.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas del cambio de estado activo/inactivo de usuarios. */
class UsuarioServiceEstadoTest {

    @AfterEach
    void limpiarSesion() {
        SesionUsuario.cerrarSesion();
    }

    private void iniciarSesionComo(int idUsuario) {
        Usuario actual = new Usuario();
        actual.setIdUsuario(idUsuario);
        actual.setRol("Administrador");
        actual.setActivo(true);
        SesionUsuario.iniciarSesion(actual);
    }

    @Test
    @DisplayName("No se puede desactivar la propia cuenta")
    void noPuedeDesactivarSuPropiaCuenta() {
        UsuarioDAO dao = mock(UsuarioDAO.class);
        UsuarioService servicio = new UsuarioService(dao);
        iniciarSesionComo(1);

        assertFalse(servicio.cambiarEstado(1, false));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Se puede desactivar a otro usuario")
    void puedeDesactivarOtroUsuario() {
        UsuarioDAO dao = mock(UsuarioDAO.class);
        when(dao.cambiarEstado(2, false)).thenReturn(true);
        UsuarioService servicio = new UsuarioService(dao);
        iniciarSesionComo(1);

        assertTrue(servicio.cambiarEstado(2, false));
        verify(dao).cambiarEstado(2, false);
    }

    @Test
    @DisplayName("Se puede reactivar un usuario")
    void puedeActivarUnUsuario() {
        UsuarioDAO dao = mock(UsuarioDAO.class);
        when(dao.cambiarEstado(2, true)).thenReturn(true);
        UsuarioService servicio = new UsuarioService(dao);
        iniciarSesionComo(1);

        assertTrue(servicio.cambiarEstado(2, true));
        verify(dao).cambiarEstado(2, true);
    }

    @Test
    @DisplayName("Un identificador inválido no llega a la base de datos")
    void identificadorInvalidoNoCambiaNada() {
        UsuarioDAO dao = mock(UsuarioDAO.class);
        UsuarioService servicio = new UsuarioService(dao);

        assertFalse(servicio.cambiarEstado(0, false));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }
}
