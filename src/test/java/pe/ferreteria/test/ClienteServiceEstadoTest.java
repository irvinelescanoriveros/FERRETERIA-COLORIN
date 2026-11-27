package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.ClienteDAO;
import pe.ferreteria.servicio.ClienteService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas del cambio de estado activo/inactivo de clientes. */
class ClienteServiceEstadoTest {

    @Test
    @DisplayName("El cliente Consumidor Final no se puede desactivar")
    void consumidorFinalNoSeDesactiva() {
        ClienteDAO dao = mock(ClienteDAO.class);
        ClienteService servicio = new ClienteService(dao);

        assertFalse(servicio.cambiarEstado(1, false));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Se puede desactivar a otro cliente")
    void puedeDesactivarOtroCliente() {
        ClienteDAO dao = mock(ClienteDAO.class);
        when(dao.cambiarEstado(5, false)).thenReturn(true);
        ClienteService servicio = new ClienteService(dao);

        assertTrue(servicio.cambiarEstado(5, false));
        verify(dao).cambiarEstado(5, false);
    }

    @Test
    @DisplayName("Se puede reactivar un cliente")
    void puedeActivarUnCliente() {
        ClienteDAO dao = mock(ClienteDAO.class);
        when(dao.cambiarEstado(5, true)).thenReturn(true);
        ClienteService servicio = new ClienteService(dao);

        assertTrue(servicio.cambiarEstado(5, true));
        verify(dao).cambiarEstado(5, true);
    }

    @Test
    @DisplayName("Un identificador inválido no llega a la base de datos")
    void identificadorInvalidoNoCambiaNada() {
        ClienteDAO dao = mock(ClienteDAO.class);
        ClienteService servicio = new ClienteService(dao);

        assertFalse(servicio.cambiarEstado(0, true));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }
}
