package pe.ferreteria.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.Seguridad;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de seguridad de UsuarioService: inicio de sesión con
 * verificación de hash, migración automática de contraseñas antiguas
 * y almacenamiento de contraseñas solo con hash (RNF-03).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsuarioServiceSeguridadTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(usuarioDAO);
    }

    private Usuario usuario(int id, String username, String password) {
        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setUsername(username);
        u.setPassword(password);
        u.setRol("Vendedor");
        return u;
    }

    // ---------------------------------------------------------
    // Inicio de sesión
    // ---------------------------------------------------------

    @Test
    @DisplayName("Login: contraseña correcta con hash inicia sesión")
    void loginConHashCorrectoFunciona() {
        String hash = Seguridad.hashear("Clave#2026");
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1", hash));

        Usuario resultado = servicio.autenticar("vendedor1", "Clave#2026");

        assertNotNull(resultado);
        assertEquals("vendedor1", resultado.getUsername());
    }

    @Test
    @DisplayName("Login: el usuario devuelto no incluye la contraseña")
    void loginNoExponeLaContrasena() {
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1",
                        Seguridad.hashear("Clave#2026")));

        Usuario resultado = servicio.autenticar("vendedor1", "Clave#2026");

        assertNotNull(resultado);
        assertNull(resultado.getPassword());
    }

    @Test
    @DisplayName("Login: contraseña incorrecta devuelve null y no modifica nada")
    void loginConContrasenaIncorrectaFalla() {
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1",
                        Seguridad.hashear("Clave#2026")));

        assertNull(servicio.autenticar("vendedor1", "incorrecta"));

        verify(usuarioDAO, never()).cambiarPassword(anyInt(), anyString());
    }

    @Test
    @DisplayName("Login: un usuario inexistente o inactivo devuelve null")
    void loginConUsuarioInexistenteFalla() {
        when(usuarioDAO.buscarActivoPorUsername("fantasma"))
                .thenReturn(null);

        assertNull(servicio.autenticar("fantasma", "123"));

        verify(usuarioDAO, never()).cambiarPassword(anyInt(), anyString());
    }

    @Test
    @DisplayName("Login: usuario o contraseña vacíos no consultan la base de datos")
    void loginConDatosVaciosNoConsultaLaBase() {
        assertNull(servicio.autenticar(null, "123"));
        assertNull(servicio.autenticar("  ", "123"));
        assertNull(servicio.autenticar("vendedor1", null));
        assertNull(servicio.autenticar("vendedor1", ""));

        verify(usuarioDAO, never()).buscarActivoPorUsername(anyString());
    }

    @Test
    @DisplayName("Login: no se puede entrar usando el hash como contraseña")
    void elHashNoSirveComoContrasena() {
        String hash = Seguridad.hashear("Clave#2026");
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1", hash));

        assertNull(servicio.autenticar("vendedor1", hash));
    }

    // ---------------------------------------------------------
    // Migración de contraseñas antiguas
    // ---------------------------------------------------------

    @Test
    @DisplayName("Migración: una contraseña en texto plano se guarda con hash al iniciar sesión")
    void loginMigraLaContrasenaAntiguaAHash() {
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1", "123"));

        assertNotNull(servicio.autenticar("vendedor1", "123"));

        ArgumentCaptor<String> guardada =
                ArgumentCaptor.forClass(String.class);
        verify(usuarioDAO).cambiarPassword(eq(2), guardada.capture());

        assertTrue(Seguridad.esHash(guardada.getValue()));
        assertTrue(Seguridad.verificar("123", guardada.getValue()));
    }

    @Test
    @DisplayName("Migración: un hash vigente no se vuelve a guardar")
    void loginConHashVigenteNoVuelveAGuardar() {
        when(usuarioDAO.buscarActivoPorUsername("vendedor1"))
                .thenReturn(usuario(2, "vendedor1",
                        Seguridad.hashear("123")));

        assertNotNull(servicio.autenticar("vendedor1", "123"));

        verify(usuarioDAO, never()).cambiarPassword(anyInt(), anyString());
    }

    // ---------------------------------------------------------
    // Registro y cambio de contraseña
    // ---------------------------------------------------------

    @Test
    @DisplayName("Registrar: la contraseña se guarda solo como hash")
    void registrarGuardaSoloElHash() {
        when(usuarioDAO.insertar(any(Usuario.class))).thenReturn(true);

        Usuario nuevo = usuario(0, "nuevo", "Secreta#1");

        assertTrue(servicio.registrar(nuevo));

        ArgumentCaptor<Usuario> guardado =
                ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioDAO).insertar(guardado.capture());

        String almacenada = guardado.getValue().getPassword();

        assertNotEquals("Secreta#1", almacenada);
        assertTrue(Seguridad.esHash(almacenada));
        assertTrue(Seguridad.verificar("Secreta#1", almacenada));
    }

    @Test
    @DisplayName("Registrar: una contraseña vacía o un usuario nulo se rechazan")
    void registrarConContrasenaVaciaSeRechaza() {
        assertFalse(servicio.registrar(null));
        assertFalse(servicio.registrar(usuario(0, "x", null)));
        assertFalse(servicio.registrar(usuario(0, "x", "")));

        verify(usuarioDAO, never()).insertar(any(Usuario.class));
    }

    @Test
    @DisplayName("Cambiar contraseña: se guarda solo el hash")
    void cambiarPasswordGuardaSoloElHash() {
        when(usuarioDAO.cambiarPassword(eq(5), anyString()))
                .thenReturn(true);

        assertTrue(servicio.cambiarPassword(5, "Nueva#2026"));

        ArgumentCaptor<String> guardada =
                ArgumentCaptor.forClass(String.class);
        verify(usuarioDAO).cambiarPassword(eq(5), guardada.capture());

        assertNotEquals("Nueva#2026", guardada.getValue());
        assertTrue(Seguridad.verificar("Nueva#2026", guardada.getValue()));
    }

    @Test
    @DisplayName("Cambiar contraseña: id inválido o contraseña vacía se rechazan")
    void cambiarPasswordConDatosInvalidosSeRechaza() {
        assertFalse(servicio.cambiarPassword(0, "Nueva#2026"));
        assertFalse(servicio.cambiarPassword(5, null));
        assertFalse(servicio.cambiarPassword(5, ""));

        verify(usuarioDAO, never()).cambiarPassword(anyInt(), anyString());
    }
}
