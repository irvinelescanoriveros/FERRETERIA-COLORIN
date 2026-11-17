package pe.ferreteria.test;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pe.ferreteria.dao.ProductoDAO;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.servicio.ProductoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del módulo Productos (RF-01).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductoServiceTest {

    @Mock
    private ProductoDAO productoDAO;

    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        servicio = new ProductoService(productoDAO);
    }

    private Producto producto(int id, String codigo) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setCodigo(codigo);
        p.setNombre("Pintura látex 1 galón");
        return p;
    }

    @Test
    @DisplayName("Registrar: se delega al DAO y devuelve su resultado")
    void registrarSeDelegaAlDao() {
        Producto p = producto(0, "PIN-001");
        when(productoDAO.insertar(p)).thenReturn(true);

        assertTrue(servicio.registrar(p));

        verify(productoDAO).insertar(p);
    }

    @Test
    @DisplayName("Registrar: si el DAO falla devuelve false")
    void registrarConFalloDelDaoDevuelveFalse() {
        Producto p = producto(0, "PIN-001");
        when(productoDAO.insertar(p)).thenReturn(false);

        assertFalse(servicio.registrar(p));
    }

    @Test
    @DisplayName("Listar: devuelve la lista del DAO")
    void listarDevuelveLaListaDelDao() {
        List<Producto> lista = List.of(
                producto(1, "PIN-001"), producto(2, "BRO-002"));
        when(productoDAO.listar()).thenReturn(lista);

        assertSame(lista, servicio.listar());
    }

    @Test
    @DisplayName("Actualizar: se delega al DAO")
    void actualizarSeDelegaAlDao() {
        Producto p = producto(1, "PIN-001");
        when(productoDAO.actualizar(p)).thenReturn(true);

        assertTrue(servicio.actualizar(p));
    }

    @Test
    @DisplayName("Eliminar: se delega al DAO con el id indicado")
    void eliminarSeDelegaAlDao() {
        when(productoDAO.eliminar(3)).thenReturn(true);

        assertTrue(servicio.eliminar(3));

        verify(productoDAO).eliminar(3);
    }

    @Test
    @DisplayName("Código repetido: la verificación excluye al propio producto")
    void existeCodigoSeDelegaAlDao() {
        when(productoDAO.existeCodigo("PIN-001", 0)).thenReturn(true);
        when(productoDAO.existeCodigo("PIN-001", 1)).thenReturn(false);

        assertTrue(servicio.existeCodigo("PIN-001", 0));
        assertFalse(servicio.existeCodigo("PIN-001", 1));
    }

    @Test
    @DisplayName("Dashboard: un id inválido devuelve null sin consultar la base")
    void buscarParaDashboardConIdInvalidoDevuelveNull() {
        assertNull(servicio.buscarParaDashboard(0));
        assertNull(servicio.buscarParaDashboard(-1));

        verify(productoDAO, never()).buscarParaDashboard(anyInt());
    }

    @Test
    @DisplayName("Dashboard: un id válido se consulta en el DAO")
    void buscarParaDashboardConIdValidoConsultaElDao() {
        Producto p = producto(5, "PIN-001");
        when(productoDAO.buscarParaDashboard(5)).thenReturn(p);

        assertSame(p, servicio.buscarParaDashboard(5));
    }
}
