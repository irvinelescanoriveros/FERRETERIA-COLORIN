package pe.ferreteria.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.InventarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del módulo Inventario (RF-07).
 * Cubren el alta de lotes desde una compra, la actualización de
 * ubicación y stock, los descuentos y el retiro de productos
 * defectuosos.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InventarioServiceTest {

    @Mock
    private InventarioDAO inventarioDAO;

    @Mock
    private Connection con;

    private InventarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new InventarioService(inventarioDAO);
    }

    @AfterEach
    void limpiarSesion() {
        SesionUsuario.cerrarSesion();
    }

    // ---------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------

    private void iniciarSesion() {
        Usuario u = new Usuario();
        u.setIdUsuario(1);
        u.setUsername("admin");
        u.setRol("Administrador");
        SesionUsuario.iniciarSesion(u);
    }

    private DetalleCompra detalleCompra(String precioCompra) {
        DetalleCompra d = new DetalleCompra();
        d.setIdProducto(9);
        d.setLote("L-2026-001");
        d.setCantidad(20);
        d.setPrecioCompra(
                precioCompra == null ? null : new BigDecimal(precioCompra));
        d.setFechaVencimiento(LocalDate.of(2027, 5, 31));
        return d;
    }

    private Compra compra() {
        Compra c = new Compra();
        c.setIdProveedor(4);
        return c;
    }

    private Producto producto() {
        Producto p = new Producto();
        p.setIdProducto(9);
        p.setEstante("B2");
        p.setNivel("3");
        return p;
    }

    private Inventario lote(int id, int stockDefectuoso) {
        Inventario i = new Inventario();
        i.setIdInventario(id);
        i.setStockDefectuoso(stockDefectuoso);
        return i;
    }

    // ---------------------------------------------------------
    // Registro de lote desde una compra
    // ---------------------------------------------------------

    @Test
    @DisplayName("Alta de lote: datos nulos o cantidad no positiva devuelven 0")
    void altaDeLoteConDatosInvalidosDevuelveCero() {
        assertEquals(0, servicio.registrarInventario(
                null, compra(), producto(), 5));
        assertEquals(0, servicio.registrarInventario(
                detalleCompra("10.00"), null, producto(), 5));
        assertEquals(0, servicio.registrarInventario(
                detalleCompra("10.00"), compra(), null, 5));
        assertEquals(0, servicio.registrarInventario(
                detalleCompra("10.00"), compra(), producto(), 0));
        assertEquals(0, servicio.registrarInventario(
                detalleCompra("10.00"), compra(), producto(), -2));

        verify(inventarioDAO, never()).insertar(any(Inventario.class));
    }

    @Test
    @DisplayName("Alta de lote: precio de venta = precio de compra + 20 %")
    void altaDeLoteCalculaPrecioDeVentaConMargenDel20() {
        when(inventarioDAO.insertar(any(Inventario.class)))
                .thenReturn(77);

        int id = servicio.registrarInventario(
                detalleCompra("10.00"), compra(), producto(), 15);

        assertEquals(77, id);

        ArgumentCaptor<Inventario> captor =
                ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioDAO).insertar(captor.capture());

        Inventario guardado = captor.getValue();

        assertEquals(0, new BigDecimal("12.00")
                .compareTo(guardado.getPrecioVenta()));
        assertEquals(0, new BigDecimal("10.00")
                .compareTo(guardado.getPrecioCompra()));
    }

    @Test
    @DisplayName("Alta de lote: el lote nuevo nace sin defectuosos ni descuento")
    void altaDeLoteInicializaStockYDescuento() {
        when(inventarioDAO.insertar(any(Inventario.class)))
                .thenReturn(1);

        servicio.registrarInventario(
                detalleCompra("8.50"), compra(), producto(), 15);

        ArgumentCaptor<Inventario> captor =
                ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioDAO).insertar(captor.capture());

        Inventario guardado = captor.getValue();

        assertEquals(15, guardado.getStock());
        assertEquals(0, guardado.getStockDefectuoso());
        assertFalse(guardado.isDescuentoActivo());
        assertEquals(0, BigDecimal.ZERO
                .compareTo(guardado.getDescuentoPorcentaje()));
        assertTrue(guardado.isActivo());
        assertEquals(9, guardado.getIdProducto());
        assertEquals(4, guardado.getIdProveedor());
        assertEquals("L-2026-001", guardado.getLote());
        assertEquals(LocalDate.of(2027, 5, 31),
                guardado.getFechaVencimiento());
    }

    @Test
    @DisplayName("Alta de lote: la ubicación se toma del producto")
    void altaDeLoteTomaLaUbicacionDelProducto() {
        when(inventarioDAO.insertar(any(Inventario.class)))
                .thenReturn(1);

        servicio.registrarInventario(
                detalleCompra("10.00"), compra(), producto(), 5);

        ArgumentCaptor<Inventario> captor =
                ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioDAO).insertar(captor.capture());

        assertEquals("B2", captor.getValue().getEstante());
        assertEquals("3", captor.getValue().getNivel());
    }

    @Test
    @DisplayName("Alta de lote: un detalle sin precio de compra no lanza excepción")
    void altaDeLoteSinPrecioDeCompraNoLanzaExcepcion() {
        when(inventarioDAO.insertar(any(Inventario.class)))
                .thenReturn(3);

        int id = assertDoesNotThrow(() ->
                servicio.registrarInventario(
                        detalleCompra(null), compra(), producto(), 5));

        assertEquals(3, id);

        ArgumentCaptor<Inventario> captor =
                ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioDAO).insertar(captor.capture());

        assertEquals(0, BigDecimal.ZERO
                .compareTo(captor.getValue().getPrecioVenta()));
    }

    @Test
    @DisplayName("Alta de lote dentro de una transacción usa la conexión recibida")
    void altaDeLoteConConexionUsaLaConexionRecibida() {
        when(inventarioDAO.insertar(eq(con), any(Inventario.class)))
                .thenReturn(88);

        int id = servicio.registrarInventario(
                con, detalleCompra("10.00"), compra(), producto(), 5);

        assertEquals(88, id);
        verify(inventarioDAO).insertar(eq(con), any(Inventario.class));
    }

    @Test
    @DisplayName("Alta de lote con conexión: datos inválidos devuelven 0")
    void altaDeLoteConConexionYDatosInvalidosDevuelveCero() {
        assertEquals(0, servicio.registrarInventario(
                con, null, compra(), producto(), 5));
        assertEquals(0, servicio.registrarInventario(
                con, detalleCompra("10.00"), compra(), producto(), 0));

        verify(inventarioDAO, never())
                .insertar(any(Connection.class), any(Inventario.class));
    }

    // ---------------------------------------------------------
    // Ubicación
    // ---------------------------------------------------------

    @Test
    @DisplayName("Ubicación: id inválido, estante o nivel vacíos se rechazan")
    void ubicacionInvalidaSeRechaza() {
        assertFalse(servicio.actualizarUbicacion(0, "A1", "1"));
        assertFalse(servicio.actualizarUbicacion(5, null, "1"));
        assertFalse(servicio.actualizarUbicacion(5, "   ", "1"));
        assertFalse(servicio.actualizarUbicacion(5, "A1", null));
        assertFalse(servicio.actualizarUbicacion(5, "A1", "  "));

        verify(inventarioDAO, never())
                .actualizarUbicacion(anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Ubicación: se eliminan los espacios sobrantes antes de guardar")
    void ubicacionValidaSeGuardaSinEspacios() {
        when(inventarioDAO.actualizarUbicacion(5, "A1", "2"))
                .thenReturn(true);

        assertTrue(servicio.actualizarUbicacion(5, "  A1 ", " 2 "));
    }

    // ---------------------------------------------------------
    // Ajuste de stock
    // ---------------------------------------------------------

    @Test
    @DisplayName("Stock: id inválido o stock negativo se rechazan")
    void ajusteDeStockInvalidoSeRechaza() {
        iniciarSesion();

        assertFalse(servicio.actualizarStock(0, 10));
        assertFalse(servicio.actualizarStock(5, -1));

        verify(inventarioDAO, never()).actualizarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Stock: sin usuario con sesión iniciada no se puede ajustar")
    void ajusteDeStockSinSesionSeRechaza() {
        assertFalse(servicio.actualizarStock(5, 10));

        verify(inventarioDAO, never()).actualizarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Stock: con sesión y datos válidos se delega al DAO")
    void ajusteDeStockValidoSeDelega() {
        iniciarSesion();
        when(inventarioDAO.actualizarStock(5, 0)).thenReturn(true);

        assertTrue(servicio.actualizarStock(5, 0));
    }

    // ---------------------------------------------------------
    // Descuentos
    // ---------------------------------------------------------

    @Test
    @DisplayName("Descuento: porcentajes fuera de (0, 100] se rechazan")
    void descuentoFueraDeRangoSeRechaza() {
        iniciarSesion();

        assertFalse(servicio.activarDescuento(5, null));
        assertFalse(servicio.activarDescuento(5, BigDecimal.ZERO));
        assertFalse(servicio.activarDescuento(5,
                new BigDecimal("-10")));
        assertFalse(servicio.activarDescuento(5,
                new BigDecimal("100.01")));
        assertFalse(servicio.activarDescuento(0,
                new BigDecimal("10")));

        verify(inventarioDAO, never()).actualizarDescuento(
                anyInt(), anyBoolean(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Descuento: sin sesión iniciada se rechaza")
    void descuentoSinSesionSeRechaza() {
        assertFalse(servicio.activarDescuento(5,
                new BigDecimal("10")));
        assertFalse(servicio.desactivarDescuento(5));
    }

    @Test
    @DisplayName("Descuento: el porcentaje se redondea a 2 decimales")
    void descuentoSeRedondeaADosDecimales() {
        iniciarSesion();
        when(inventarioDAO.actualizarDescuento(
                eq(5), eq(true), any(BigDecimal.class)))
                .thenReturn(true);

        assertTrue(servicio.activarDescuento(5,
                new BigDecimal("15.555")));

        verify(inventarioDAO).actualizarDescuento(
                5, true, new BigDecimal("15.56"));
    }

    @Test
    @DisplayName("Descuento: el 100 % es un valor permitido")
    void descuentoDelCienPorCientoEsValido() {
        iniciarSesion();
        when(inventarioDAO.actualizarDescuento(
                eq(5), eq(true), any(BigDecimal.class)))
                .thenReturn(true);

        assertTrue(servicio.activarDescuento(5,
                new BigDecimal("100")));
    }

    @Test
    @DisplayName("Descuento: al desactivarlo el porcentaje vuelve a cero")
    void desactivarDescuentoReiniciaElPorcentaje() {
        iniciarSesion();
        when(inventarioDAO.actualizarDescuento(
                5, false, BigDecimal.ZERO)).thenReturn(true);

        assertTrue(servicio.desactivarDescuento(5));

        verify(inventarioDAO)
                .actualizarDescuento(5, false, BigDecimal.ZERO);
    }

    // ---------------------------------------------------------
    // Productos defectuosos
    // ---------------------------------------------------------

    @Test
    @DisplayName("Defectuosos: id o cantidad inválidos se rechazan")
    void retiroDefectuosoConDatosInvalidosSeRechaza() {
        iniciarSesion();

        assertFalse(servicio.retirarStockDefectuoso(0, 1));
        assertFalse(servicio.retirarStockDefectuoso(5, 0));
        assertFalse(servicio.retirarStockDefectuoso(5, -2));

        verify(inventarioDAO, never())
                .actualizarStockDefectuoso(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Defectuosos: sin sesión iniciada se rechaza")
    void retiroDefectuosoSinSesionSeRechaza() {
        assertFalse(servicio.retirarStockDefectuoso(5, 1));
    }

    @Test
    @DisplayName("Defectuosos: un lote inexistente se rechaza")
    void retiroDefectuosoDeLoteInexistenteSeRechaza() {
        iniciarSesion();
        when(inventarioDAO.listar()).thenReturn(List.of(lote(99, 4)));

        assertFalse(servicio.retirarStockDefectuoso(5, 1));

        verify(inventarioDAO, never())
                .actualizarStockDefectuoso(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Defectuosos: no se puede retirar más de lo registrado")
    void retiroMayorAlStockDefectuosoSeRechaza() {
        iniciarSesion();
        when(inventarioDAO.listar()).thenReturn(List.of(lote(5, 3)));

        assertFalse(servicio.retirarStockDefectuoso(5, 4));

        verify(inventarioDAO, never())
                .actualizarStockDefectuoso(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Defectuosos: el retiro descuenta la cantidad del stock defectuoso")
    void retiroValidoDescuentaDelStockDefectuoso() {
        iniciarSesion();
        when(inventarioDAO.listar())
                .thenReturn(List.of(lote(4, 8), lote(5, 5)));
        when(inventarioDAO.actualizarStockDefectuoso(5, 3))
                .thenReturn(true);

        assertTrue(servicio.retirarStockDefectuoso(5, 2));

        verify(inventarioDAO).actualizarStockDefectuoso(5, 3);
    }

    @Test
    @DisplayName("Defectuosos: se puede retirar todo el stock defectuoso")
    void retiroDeTodoElStockDefectuosoQuedaEnCero() {
        iniciarSesion();
        when(inventarioDAO.listar()).thenReturn(List.of(lote(5, 5)));
        when(inventarioDAO.actualizarStockDefectuoso(5, 0))
                .thenReturn(true);

        assertTrue(servicio.retirarStockDefectuoso(5, 5));
    }

    // ---------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------

    @Test
    @DisplayName("Consulta: buscar para dashboard con id inválido devuelve null")
    void buscarParaDashboardConIdInvalidoDevuelveNull() {
        assertNull(servicio.buscarParaDashboard(0));

        verify(inventarioDAO, never()).buscarParaDashboard(anyInt());
    }
}
