package pe.ferreteria.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pe.ferreteria.dao.CompraDAO;
import pe.ferreteria.dao.DetalleCompraDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;
import pe.ferreteria.servicio.CompraService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del registro de compras (RF-05).
 * La compra y sus detalles se guardan en una sola transacción:
 * o se registra todo o no se registra nada.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompraServiceTest {

    private static final int ID_COMPRA = 10;
    private static final LocalDateTime FECHA =
            LocalDateTime.of(2026, 11, 10, 9, 30);

    @Mock
    private CompraDAO compraDAO;

    @Mock
    private DetalleCompraDAO detalleCompraDAO;

    @Mock
    private Connection con;

    private CompraService servicio;

    @BeforeEach
    void preparar() {
        servicio = new CompraService(compraDAO, detalleCompraDAO);
    }

    // ---------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------

    private Compra compra() {
        Compra c = new Compra();
        c.setIdProveedor(4);
        c.setIdUsuario(2);
        c.setFechaCompra(FECHA);
        c.setNroComprobante("F001-000123");
        c.setTotal(new BigDecimal("150.00"));
        return c;
    }

    private DetalleCompra detalle(int idProducto, int cantidad) {
        DetalleCompra d = new DetalleCompra();
        d.setIdProducto(idProducto);
        d.setLote("L-" + idProducto);
        d.setCantidad(cantidad);
        d.setPrecioCompra(new BigDecimal("7.50"));
        return d;
    }

    private boolean registrar(
            Compra compra, List<DetalleCompra> detalles) {

        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar).thenReturn(con);
            return servicio.registrarCompra(compra, detalles);
        }
    }

    // ---------------------------------------------------------
    // Registro de compras
    // ---------------------------------------------------------

    @Test
    @DisplayName("Compra: se guarda la cabecera y todos los detalles")
    void compraValidaGuardaCabeceraYDetalles() throws Exception {
        when(compraDAO.insertar(any(Compra.class), eq(con)))
                .thenReturn(ID_COMPRA);

        DetalleCompra d1 = detalle(9, 20);
        DetalleCompra d2 = detalle(12, 5);

        assertTrue(registrar(compra(), List.of(d1, d2)));

        verify(detalleCompraDAO).insertar(
                d1, con, FECHA.toLocalDate());
        verify(detalleCompraDAO).insertar(
                d2, con, FECHA.toLocalDate());

        verify(con).setAutoCommit(false);
        verify(con).commit();
        verify(con, never()).rollback();
    }

    @Test
    @DisplayName("Compra: cada detalle queda asociado al id de la compra")
    void cadaDetalleRecibeElIdDeLaCompra() throws Exception {
        when(compraDAO.insertar(any(Compra.class), eq(con)))
                .thenReturn(ID_COMPRA);

        DetalleCompra d1 = detalle(9, 20);
        DetalleCompra d2 = detalle(12, 5);

        assertTrue(registrar(compra(), List.of(d1, d2)));

        assertEquals(ID_COMPRA, d1.getIdCompra());
        assertEquals(ID_COMPRA, d2.getIdCompra());
    }

    @Test
    @DisplayName("Compra: si falla un detalle se revierte toda la compra")
    void falloEnUnDetalleRevierteTodaLaCompra() throws Exception {
        when(compraDAO.insertar(any(Compra.class), eq(con)))
                .thenReturn(ID_COMPRA);
        doThrow(new SQLException("Lote duplicado"))
                .when(detalleCompraDAO).insertar(
                        any(DetalleCompra.class),
                        any(Connection.class),
                        any(LocalDate.class));

        assertFalse(registrar(compra(), List.of(detalle(9, 20))));

        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    @DisplayName("Compra: si falla la cabecera no se guarda ningún detalle")
    void falloEnLaCabeceraNoGuardaDetalles() throws Exception {
        when(compraDAO.insertar(any(Compra.class), eq(con)))
                .thenThrow(new SQLException("Comprobante repetido"));

        assertFalse(registrar(compra(), List.of(detalle(9, 20))));

        verify(detalleCompraDAO, never()).insertar(
                any(DetalleCompra.class),
                any(Connection.class),
                any(LocalDate.class));
        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    @DisplayName("Compra: una lista de detalles nula se rechaza y revierte")
    void detallesNulosRevierten() throws Exception {
        when(compraDAO.insertar(any(Compra.class), eq(con)))
                .thenReturn(ID_COMPRA);

        assertFalse(registrar(compra(), null));

        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    @DisplayName("Compra: sin conexión a la base de datos devuelve false")
    void sinConexionDevuelveFalse() {
        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar)
                    .thenThrow(new SQLException("Sin conexión"));

            assertFalse(servicio.registrarCompra(
                    compra(), List.of(detalle(9, 20))));
        }

        verifyNoInteractions(compraDAO, detalleCompraDAO);
    }

    // ---------------------------------------------------------
    // Comprobantes repetidos
    // ---------------------------------------------------------

    @Test
    @DisplayName("Comprobante: la consulta de duplicados se delega al DAO")
    void existeNroComprobanteSeDelegaAlDao() {
        when(compraDAO.existeNroComprobante("F001-000123"))
                .thenReturn(true);
        when(compraDAO.existeNroComprobante("F001-999999"))
                .thenReturn(false);

        assertTrue(servicio.existeNroComprobante("F001-000123"));
        assertFalse(servicio.existeNroComprobante("F001-999999"));
    }
}
