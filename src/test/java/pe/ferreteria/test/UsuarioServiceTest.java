
package pe.ferreteria.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pe.ferreteria.controlador.LoginController;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @AfterEach
    void limpiarSesion() {
        SesionUsuario.cerrarSesion();
    }

    @Test
    void usuarioWebNoDebeIniciarSesionEnEscritorio() {
        UsuarioService servicio = mock(UsuarioService.class);

        Usuario usuarioWeb = new Usuario();
        usuarioWeb.setUsername("OperacionWEB");
        usuarioWeb.setRol("WEB");

        when(servicio.autenticar("OperacionWEB", "WebColorin123"))
                .thenReturn(usuarioWeb);

        LoginController controlador = new LoginController(servicio);

        boolean resultado = controlador.iniciarSesion(
                "OperacionWEB",
                "WebColorin123"
        );

        assertFalse(resultado);
        assertNull(SesionUsuario.getUsuarioActual());
    }

    @Test
    void usuarioActivoConRolVendedorPuedeIniciarSesion() {
        UsuarioService servicio = mock(UsuarioService.class);

        Usuario vendedor = new Usuario();
        vendedor.setUsername("vendedor1");
        vendedor.setRol("Vendedor");

        when(servicio.autenticar("vendedor1", "123"))
                .thenReturn(vendedor);

        LoginController controlador = new LoginController(servicio);

        boolean resultado = controlador.iniciarSesion("vendedor1", "123");

        assertTrue(resultado);
        assertSame(vendedor, SesionUsuario.getUsuarioActual());
    }
}
