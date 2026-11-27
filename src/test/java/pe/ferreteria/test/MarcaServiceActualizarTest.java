package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.MarcaDAO;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.servicio.MarcaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MarcaServiceActualizarTest {

    private Marca con(int id, String nombre) {
        Marca marca = new Marca();
        marca.setIdMarca(id);
        marca.setNombre(nombre);
        return marca;
    }

    @Test
    @DisplayName("Un cambio de nombre valido se delega al DAO")
    void actualizarSeDelegaAlDao() {
        MarcaDAO dao = mock(MarcaDAO.class);
        when(dao.existeNombre("Nuevo", 3)).thenReturn(false);
        when(dao.actualizar(any(Marca.class))).thenReturn(true);
        MarcaService servicio = new MarcaService(dao);

        assertTrue(servicio.actualizar(con(3, "  Nuevo  ")));
        verify(dao).actualizar(any(Marca.class));
    }

    @Test
    @DisplayName("Un nombre vacio no llega al DAO")
    void nombreVacioNoLlegaAlDao() {
        MarcaDAO dao = mock(MarcaDAO.class);
        MarcaService servicio = new MarcaService(dao);

        assertFalse(servicio.actualizar(con(3, "   ")));
        verify(dao, never()).actualizar(any(Marca.class));
    }

    @Test
    @DisplayName("Un identificador invalido no llega al DAO")
    void identificadorInvalidoNoLlegaAlDao() {
        MarcaDAO dao = mock(MarcaDAO.class);
        MarcaService servicio = new MarcaService(dao);

        assertFalse(servicio.actualizar(con(0, "Nuevo")));
        verify(dao, never()).actualizar(any(Marca.class));
    }

    @Test
    @DisplayName("Un nombre que ya usa otro registro no llega al DAO")
    void nombreRepetidoNoLlegaAlDao() {
        MarcaDAO dao = mock(MarcaDAO.class);
        when(dao.existeNombre(anyString(), anyInt())).thenReturn(true);
        MarcaService servicio = new MarcaService(dao);

        assertFalse(servicio.actualizar(con(3, "Repetido")));
        verify(dao, never()).actualizar(any(Marca.class));
    }
}