package pe.ferreteria.test;

import java.util.List;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KardexServiceTest {

    @Test
    void listarKardexDebeIncluirUsuarioWeb() {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);

        Usuario usuarioWeb = new Usuario();
        usuarioWeb.setIdUsuario(6);
        usuarioWeb.setUsername("OperacionWEB");
        usuarioWeb.setNombre("Operacion");
        usuarioWeb.setApellidos("Web");
        usuarioWeb.setRol("WEB");
        usuarioWeb.setActivo(true);

        when(usuarioDAO.listarKardex())
                .thenReturn(List.of(usuarioWeb));

        UsuarioService servicio = new UsuarioService(usuarioDAO);

        List<Usuario> resultado = servicio.listarKardex();

        assertEquals(1, resultado.size());
        assertEquals("OperacionWEB", resultado.get(0).getUsername());
        assertEquals("WEB", resultado.get(0).getRol());

        verify(usuarioDAO).listarKardex();
    }
}