
package pe.ferreteria.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.Seguridad;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class UsuarioServiceMiCuentaTest {

    private UsuarioDAO dao;
    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        dao = mock(UsuarioDAO.class);
        servicio = new UsuarioService(dao);

        Usuario sesion = new Usuario();
        sesion.setIdUsuario(5);
        sesion.setUsername("vendedor1");
        sesion.setRol("Vendedor");
        SesionUsuario.iniciarSesion(sesion);

        when(dao.obtenerPassword(5)).thenReturn(Seguridad.hashear("clave123"));
    }

    @AfterEach
    void limpiar() {
        SesionUsuario.cerrarSesion();
    }

    @Test
    @DisplayName("Guarda los datos sin cambiar la contrasena")
    void guardaDatosSinCambiarPassword() {
        when(dao.actualizarPerfil(eq(5), eq("Ana"), eq("Lopez"),
                eq("ana@correo.com"), isNull())).thenReturn(true);

        assertNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "clave123", ""));

        assertEquals("Ana", SesionUsuario.getUsuarioActual().getNombre());
    }

    @Test
    @DisplayName("Una contrasena actual incorrecta no guarda nada")
    void passwordActualIncorrectaNoGuarda() {
        assertNotNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "equivocada", ""));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("La contrasena nueva se guarda hasheada")
    void passwordNuevaSeGuardaHasheada() {
        when(dao.actualizarPerfil(eq(5), anyString(), anyString(),
                anyString(), anyString())).thenReturn(true);

        assertNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "clave123", "nueva456"));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), eq("nueva456"));
    }

    @Test
    @DisplayName("Una contrasena nueva corta no guarda nada")
    void passwordNuevaCortaNoGuarda() {
        assertNotNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "clave123", "abc"));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("La contrasena nueva igual a la actual no guarda nada")
    void passwordNuevaIgualNoGuarda() {
        assertNotNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "clave123", "clave123"));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Sin nombre no guarda nada")
    void nombreVacioNoGuarda() {
        assertNotNull(servicio.actualizarMiCuenta(
                " ", "Lopez", "ana@correo.com", "clave123", ""));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Sin sesion iniciada no guarda nada")
    void sinSesionNoGuarda() {
        SesionUsuario.cerrarSesion();

        assertNotNull(servicio.actualizarMiCuenta(
                "Ana", "Lopez", "ana@correo.com", "clave123", ""));

        verify(dao, never()).actualizarPerfil(
                anyInt(), anyString(), anyString(), anyString(), anyString());
    }
}
