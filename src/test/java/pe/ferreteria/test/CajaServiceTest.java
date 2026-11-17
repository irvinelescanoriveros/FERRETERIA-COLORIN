package pe.ferreteria.test;

import java.math.BigDecimal;
import java.sql.Connection;
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

import pe.ferreteria.dao.CajaDAO;
import pe.ferreteria.dao.MovimientoCajaDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.MovimientoCaja;
import pe.ferreteria.servicio.CajaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del módulo Caja (RF-09).
 * Los DAO y la conexión se simulan con Mockito, por lo que
 * no se necesita MySQL para ejecutarlas.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CajaServiceTest {

    @Mock
    private CajaDAO cajaDAO;

    @Mock
    private MovimientoCajaDAO movimientoCajaDAO;

    @Mock
    private Connection con;

    private CajaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new CajaService(cajaDAO, movimientoCajaDAO);
    }

    // ---------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------

    private Caja cajaDeApertura(String monto) {
        Caja caja = new Caja();
        caja.setIdUsuarioApertura(2);
        caja.setMontoApertura(
                monto == null ? null : new BigDecimal(monto));
        return caja;
    }

    private MovimientoCaja movimiento(
            int idCaja, int idUsuario, String monto) {

        MovimientoCaja m = new MovimientoCaja();
        m.setIdCaja(idCaja);
        m.setIdUsuario(idUsuario);
        m.setMonto(monto == null ? null : new BigDecimal(monto));
        m.setTipoMovimiento("Salida");
        m.setConcepto("Pago de movilidad");
        return m;
    }

    private boolean abrirCaja(Caja caja) {
        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar).thenReturn(con);
            return servicio.registrarApertura(caja);
        }
    }

    private boolean registrarMovimiento(MovimientoCaja m) {
        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar).thenReturn(con);
            return servicio.registrarMovimiento(m);
        }
    }

    private boolean cerrar(
            int idCaja, int idUsuario, String contado) {

        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar).thenReturn(con);
            return servicio.cerrarCaja(
                    idCaja,
                    idUsuario,
                    contado == null ? null : new BigDecimal(contado),
                    BigDecimal.ZERO,
                    "Cierre normal");
        }
    }

    /** Verifica que no se llegó a abrir una conexión. */
    private void assertRechazadaSinConectar(Runnable accion) {
        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            accion.run();
            bd.verifyNoInteractions();
        }
    }

    // ---------------------------------------------------------
    // Apertura de caja
    // ---------------------------------------------------------

    @Test
    @DisplayName("Apertura: una caja nula se rechaza")
    void aperturaConCajaNulaSeRechaza() {
        assertRechazadaSinConectar(
                () -> assertFalse(servicio.registrarApertura(null)));
    }

    @Test
    @DisplayName("Apertura: se exige un usuario válido")
    void aperturaSinUsuarioSeRechaza() {
        Caja caja = cajaDeApertura("100.00");
        caja.setIdUsuarioApertura(0);

        assertRechazadaSinConectar(
                () -> assertFalse(servicio.registrarApertura(caja)));
    }

    @Test
    @DisplayName("Apertura: el monto inicial es obligatorio")
    void aperturaSinMontoSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.registrarApertura(cajaDeApertura(null))));
    }

    @Test
    @DisplayName("Apertura: no se acepta un monto inicial negativo")
    void aperturaConMontoNegativoSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.registrarApertura(cajaDeApertura("-1.00"))));
    }

    @Test
    @DisplayName("Apertura: un monto inicial de cero es válido")
    void aperturaConMontoCeroEsValida() {
        when(cajaDAO.insertar(eq(con), any(Caja.class)))
                .thenReturn(true);

        assertTrue(abrirCaja(cajaDeApertura("0.00")));
    }

    @Test
    @DisplayName("Apertura: asigna valores iniciales y confirma la transacción")
    void aperturaValidaAsignaValoresInicialesYConfirma()
            throws Exception {

        when(cajaDAO.insertar(eq(con), any(Caja.class)))
                .thenReturn(true);

        Caja caja = cajaDeApertura("100.00");

        assertTrue(abrirCaja(caja));

        assertEquals("Abierta", caja.getEstado());
        assertEquals(0,
                BigDecimal.ZERO.compareTo(caja.getTotalIngresos()));
        assertEquals(0,
                BigDecimal.ZERO.compareTo(caja.getTotalSalidas()));
        assertEquals(0,
                new BigDecimal("100.00")
                        .compareTo(caja.getMontoEsperado()));

        verify(con).setAutoCommit(false);
        verify(con).commit();
        verify(con, never()).rollback();
        verify(con).close();
    }

    @Test
    @DisplayName("Apertura: si el DAO falla se revierte la transacción")
    void aperturaConFalloDelDaoRevierte() throws Exception {
        when(cajaDAO.insertar(eq(con), any(Caja.class)))
                .thenReturn(false);

        assertFalse(abrirCaja(cajaDeApertura("100.00")));

        verify(con).rollback();
        verify(con, never()).commit();
        verify(con).close();
    }

    // ---------------------------------------------------------
    // Movimientos de caja
    // ---------------------------------------------------------

    @Test
    @DisplayName("Movimiento: un movimiento nulo se rechaza")
    void movimientoNuloSeRechaza() {
        assertRechazadaSinConectar(
                () -> assertFalse(servicio.registrarMovimiento(null)));
    }

    @Test
    @DisplayName("Movimiento: se exige una caja válida")
    void movimientoSinCajaSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.registrarMovimiento(
                        movimiento(0, 2, "10.00"))));
    }

    @Test
    @DisplayName("Movimiento: se exige un usuario válido")
    void movimientoSinUsuarioSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.registrarMovimiento(
                        movimiento(1, 0, "10.00"))));
    }

    @Test
    @DisplayName("Movimiento: el monto es obligatorio")
    void movimientoSinMontoSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.registrarMovimiento(
                        movimiento(1, 2, null))));
    }

    @Test
    @DisplayName("Movimiento: el monto debe ser mayor que cero")
    void movimientoConMontoCeroOMenorSeRechaza() {
        assertRechazadaSinConectar(() -> {
            assertFalse(servicio.registrarMovimiento(
                    movimiento(1, 2, "0.00")));
            assertFalse(servicio.registrarMovimiento(
                    movimiento(1, 2, "-5.00")));
        });
    }

    @Test
    @DisplayName("Movimiento: un movimiento válido se registra y confirma")
    void movimientoValidoSeRegistra() throws Exception {
        MovimientoCaja m = movimiento(1, 2, "25.50");
        when(movimientoCajaDAO.insertar(eq(con), eq(m)))
                .thenReturn(true);

        assertTrue(registrarMovimiento(m));

        verify(movimientoCajaDAO).insertar(con, m);
        verify(con).commit();
        verify(con, never()).rollback();
    }

    @Test
    @DisplayName("Movimiento: si el DAO falla se revierte")
    void movimientoConFalloDelDaoRevierte() throws Exception {
        MovimientoCaja m = movimiento(1, 2, "25.50");
        when(movimientoCajaDAO.insertar(eq(con), eq(m)))
                .thenReturn(false);

        assertFalse(registrarMovimiento(m));

        verify(con).rollback();
        verify(con, never()).commit();
    }

    // ---------------------------------------------------------
    // Cierre de caja
    // ---------------------------------------------------------

    @Test
    @DisplayName("Cierre: se exige una caja válida")
    void cierreSinCajaSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.cerrarCaja(0, 2, new BigDecimal("50"),
                        BigDecimal.ZERO, "")));
    }

    @Test
    @DisplayName("Cierre: se exige el usuario que cierra")
    void cierreSinUsuarioSeRechaza() {
        assertRechazadaSinConectar(() -> assertFalse(
                servicio.cerrarCaja(1, 0, new BigDecimal("50"),
                        BigDecimal.ZERO, "")));
    }

    @Test
    @DisplayName("Cierre: el monto contado es obligatorio y no puede ser negativo")
    void cierreConMontoInvalidoSeRechaza() {
        assertRechazadaSinConectar(() -> {
            assertFalse(servicio.cerrarCaja(1, 2, null,
                    BigDecimal.ZERO, ""));
            assertFalse(servicio.cerrarCaja(1, 2,
                    new BigDecimal("-0.01"), BigDecimal.ZERO, ""));
        });
    }

    @Test
    @DisplayName("Cierre: un cierre válido se confirma")
    void cierreValidoSeConfirma() throws Exception {
        when(cajaDAO.cerrarCaja(
                eq(con), eq(1), eq(2),
                any(BigDecimal.class), any(), any()))
                .thenReturn(true);

        assertTrue(cerrar(1, 2, "130.00"));

        verify(con).commit();
        verify(con, never()).rollback();
        verify(con).close();
    }

    @Test
    @DisplayName("Cierre: si el DAO falla se revierte")
    void cierreConFalloDelDaoRevierte() throws Exception {
        when(cajaDAO.cerrarCaja(
                eq(con), eq(1), eq(2),
                any(BigDecimal.class), any(), any()))
                .thenReturn(false);

        assertFalse(cerrar(1, 2, "130.00"));

        verify(con).rollback();
        verify(con, never()).commit();
    }

    // ---------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------

    @Test
    @DisplayName("Consulta: caja abierta de un usuario inválido devuelve null")
    void cajaAbiertaDeUsuarioInvalidoDevuelveNull() {
        assertNull(servicio.buscarCajaAbiertaPorUsuario(0));
        assertNull(servicio.buscarCajaAbiertaPorUsuario(-3));

        verify(cajaDAO, never()).buscarCajaAbiertaPorUsuario(anyInt());
    }

    @Test
    @DisplayName("Consulta: caja abierta de un usuario válido se delega al DAO")
    void cajaAbiertaDeUsuarioValidoSeDelega() {
        Caja caja = new Caja();
        caja.setIdCaja(7);
        when(cajaDAO.buscarCajaAbiertaPorUsuario(2)).thenReturn(caja);

        assertSame(caja, servicio.buscarCajaAbiertaPorUsuario(2));
    }

    @Test
    @DisplayName("Consulta: listar movimientos se delega al DAO")
    void listarMovimientosSeDelega() {
        List<MovimientoCaja> lista =
                List.of(movimiento(1, 2, "10.00"));
        when(movimientoCajaDAO.listarPorCaja(1)).thenReturn(lista);

        assertSame(lista, servicio.listarMovimientosPorCaja(1));
    }
}
