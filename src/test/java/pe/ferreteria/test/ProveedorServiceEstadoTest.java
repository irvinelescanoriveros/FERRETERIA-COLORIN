package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.ProveedorDAO;
import pe.ferreteria.servicio.ProveedorService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas del cambio de estado activo/inactivo de proveedores. */
class ProveedorServiceEstadoTest {

    @Test
    @DisplayName("Se puede desactivar un proveedor")
    void puedeDesactivarUnProveedor() {
        ProveedorDAO dao = mock(ProveedorDAO.class);
        when(dao.cambiarEstado(2, false)).thenReturn(true);
        ProveedorService servicio = new ProveedorService(dao);

        assertTrue(servicio.cambiarEstado(2, false));
        verify(dao).cambiarEstado(2, false);
    }

    @Test
    @DisplayName("Se puede reactivar un proveedor")
    void puedeActivarUnProveedor() {
        ProveedorDAO dao = mock(ProveedorDAO.class);
        when(dao.cambiarEstado(2, true)).thenReturn(true);
        ProveedorService servicio = new ProveedorService(dao);

        assertTrue(servicio.cambiarEstado(2, true));
        verify(dao).cambiarEstado(2, true);
    }

    @Test
    @DisplayName("Un identificador inválido no llega a la base de datos")
    void identificadorInvalidoNoCambiaNada() {
        ProveedorDAO dao = mock(ProveedorDAO.class);
        ProveedorService servicio = new ProveedorService(dao);

        assertFalse(servicio.cambiarEstado(0, true));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }
}
