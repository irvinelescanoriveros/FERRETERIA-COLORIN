package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.ferreteria.dao.CategoriaDAO;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.servicio.CategoriaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CategoriaServiceActualizarTest {

    private Categoria con(int id, String nombre) {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(id);
        categoria.setNombre(nombre);
        return categoria;
    }

    @Test
    @DisplayName("Un cambio de nombre valido se delega al DAO")
    void actualizarSeDelegaAlDao() {
        CategoriaDAO dao = mock(CategoriaDAO.class);
        when(dao.existeNombre("Nuevo", 3)).thenReturn(false);
        when(dao.actualizar(any(Categoria.class))).thenReturn(true);
        CategoriaService servicio = new CategoriaService(dao);

        assertTrue(servicio.actualizar(con(3, "  Nuevo  ")));
        verify(dao).actualizar(any(Categoria.class));
    }

    @Test
    @DisplayName("Un nombre vacio no llega al DAO")
    void nombreVacioNoLlegaAlDao() {
        CategoriaDAO dao = mock(CategoriaDAO.class);
        CategoriaService servicio = new CategoriaService(dao);

        assertFalse(servicio.actualizar(con(3, "   ")));
        verify(dao, never()).actualizar(any(Categoria.class));
    }

    @Test
    @DisplayName("Un identificador invalido no llega al DAO")
    void identificadorInvalidoNoLlegaAlDao() {
        CategoriaDAO dao = mock(CategoriaDAO.class);
        CategoriaService servicio = new CategoriaService(dao);

        assertFalse(servicio.actualizar(con(0, "Nuevo")));
        verify(dao, never()).actualizar(any(Categoria.class));
    }

    @Test
    @DisplayName("Un nombre que ya usa otro registro no llega al DAO")
    void nombreRepetidoNoLlegaAlDao() {
        CategoriaDAO dao = mock(CategoriaDAO.class);
        when(dao.existeNombre(anyString(), anyInt())).thenReturn(true);
        CategoriaService servicio = new CategoriaService(dao);

        assertFalse(servicio.actualizar(con(3, "Repetido")));
        verify(dao, never()).actualizar(any(Categoria.class));
    }
}