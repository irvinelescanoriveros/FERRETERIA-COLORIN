package pe.ferreteria.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pe.ferreteria.dao.CajaDAO;
import pe.ferreteria.dao.DetalleVentaDAO;
import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.dao.KardexDAO;
import pe.ferreteria.dao.MovimientoCajaDAO;
import pe.ferreteria.dao.VentaDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.DetalleVenta;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Kardex;
import pe.ferreteria.modelo.MovimientoCaja;
import pe.ferreteria.modelo.Venta;
import pe.ferreteria.servicio.VentaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del proceso de venta (RF-08).
 *
 * Se valida la regla de negocio de VentaService.registrarVentaCompleta:
 * validaciones de entrada, serie y correlativo, cálculo de importes
 * (descuento del lote, IGV incluido y vuelto), descuento de stock,
 * registro en Kardex y en Caja, y reversión de la transacción ante
 * cualquier error. Los DAO y la conexión se simulan con Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VentaServiceTest {

    private static final int ID_CAJA = 1;
    private static final int ID_VENTA = 55;

    @Mock
    private VentaDAO ventaDAO;

    @Mock
    private DetalleVentaDAO detalleVentaDAO;

    @Mock
    private InventarioDAO inventarioDAO;

    @Mock
    private KardexDAO kardexDAO;

    @Mock
    private CajaDAO cajaDAO;

    @Mock
    private MovimientoCajaDAO movimientoCajaDAO;

    @Mock
    private Connection con;

    private VentaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new VentaService(
                ventaDAO,
                detalleVentaDAO,
                inventarioDAO,
                kardexDAO,
                cajaDAO,
                movimientoCajaDAO);
    }

    // =========================================================
    // Utilidades
    // =========================================================

    private Venta venta(
            String tipoComprobante,
            String metodoPago,
            String montoRecibido) {

        Venta v = new Venta();
        v.setIdCliente(1);
        v.setIdUsuario(2);
        v.setTipoComprobante(tipoComprobante);
        v.setMetodoPago(metodoPago);
        v.setMontoRecibido(
                montoRecibido == null
                        ? null
                        : new BigDecimal(montoRecibido));
        return v;
    }

    private DetalleVenta detalle(int idInventario, int cantidad) {
        DetalleVenta d = new DetalleVenta();
        d.setIdInventario(idInventario);
        d.setCantidad(cantidad);
        return d;
    }

    private Inventario lote(
            int idInventario, int stock, String precioVenta) {

        Inventario i = new Inventario();
        i.setIdInventario(idInventario);
        i.setIdProducto(9);
        i.setLote("L-001");
        i.setStock(stock);
        i.setStockDefectuoso(0);
        i.setPrecioVenta(
                precioVenta == null
                        ? null
                        : new BigDecimal(precioVenta));
        return i;
    }

    private Inventario loteConDescuento(
            int idInventario,
            int stock,
            String precioVenta,
            String porcentaje) {

        Inventario i = lote(idInventario, stock, precioVenta);
        i.setDescuentoActivo(true);
        i.setDescuentoPorcentaje(
                porcentaje == null ? null : new BigDecimal(porcentaje));
        return i;
    }

    /**
     * Configura los DAO simulados para que una venta válida se
     * complete: hay una caja abierta, el correlativo es el indicado
     * y todas las operaciones de escritura responden correctamente.
     */
    private void prepararFlujoCorrecto(
            String serie, int numero, Inventario... lotes) {

        Caja caja = new Caja();
        caja.setIdCaja(ID_CAJA);
        caja.setMontoApertura(new BigDecimal("100.00"));

        when(cajaDAO.buscarCajaAbierta(con)).thenReturn(caja);
        when(ventaDAO.obtenerSiguienteNumero(con, serie))
                .thenReturn(numero);
        when(ventaDAO.insertar(eq(con), any(Venta.class)))
                .thenReturn(ID_VENTA);
        when(detalleVentaDAO.insertar(eq(con), any(DetalleVenta.class)))
                .thenReturn(1);

        for (Inventario lote : lotes) {
            when(inventarioDAO.buscarPorId(con, lote.getIdInventario()))
                    .thenReturn(lote);
        }

        when(inventarioDAO.reducirStock(
                eq(con), anyInt(), anyInt())).thenReturn(true);
        when(kardexDAO.insertar(eq(con), any(Kardex.class)))
                .thenReturn(true);
        when(movimientoCajaDAO.insertar(
                eq(con), any(MovimientoCaja.class))).thenReturn(true);
        when(movimientoCajaDAO.obtenerTotalIngresos(con, ID_CAJA))
                .thenReturn(new BigDecimal("0.00"));
        when(movimientoCajaDAO.obtenerTotalSalidas(con, ID_CAJA))
                .thenReturn(new BigDecimal("0.00"));
        when(cajaDAO.actualizarTotales(
                eq(con), eq(ID_CAJA),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class))).thenReturn(true);
    }

    private boolean registrar(
            Venta venta, List<DetalleVenta> detalles) {

        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar).thenReturn(con);
            return servicio.registrarVentaCompleta(venta, detalles);
        }
    }

    /** La venta debe rechazarse antes de abrir una conexión. */
    private void assertRechazadaSinConectar(
            Venta venta, List<DetalleVenta> detalles) {

        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            assertFalse(
                    servicio.registrarVentaCompleta(venta, detalles));

            bd.verifyNoInteractions();
        }

        verifyNoInteractions(ventaDAO, inventarioDAO, cajaDAO);
    }

    /** La venta falla y la transacción se revierte sin confirmar. */
    private void assertFallaYRevierte(
            Venta venta, List<DetalleVenta> detalles)
            throws Exception {

        assertFalse(registrar(venta, detalles));

        verify(con).rollback();
        verify(con, never()).commit();
        verify(con).close();
    }

    private static void assertMonto(
            String esperado, BigDecimal actual) {

        assertNotNull(actual,
                "Se esperaba " + esperado + " pero fue null");
        assertEquals(0, new BigDecimal(esperado).compareTo(actual),
                "Se esperaba " + esperado + " pero fue " + actual);
    }

    // =========================================================
    // 1. Validaciones previas (no deben abrir conexión)
    // =========================================================

    @Test
    @DisplayName("Validación: una venta nula se rechaza")
    void ventaNulaSeRechaza() {
        assertRechazadaSinConectar(null, List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Validación: la lista de detalles nula se rechaza")
    void detallesNulosSeRechazan() {
        assertRechazadaSinConectar(
                venta("Boleta", "Efectivo", "10"), null);
    }

    @Test
    @DisplayName("Validación: una venta sin detalles se rechaza")
    void ventaSinDetallesSeRechaza() {
        assertRechazadaSinConectar(
                venta("Boleta", "Efectivo", "10"), List.of());
    }

    @Test
    @DisplayName("Validación: el cliente es obligatorio")
    void ventaSinClienteSeRechaza() {
        Venta v = venta("Boleta", "Efectivo", "10");
        v.setIdCliente(0);

        assertRechazadaSinConectar(v, List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Validación: el usuario vendedor es obligatorio")
    void ventaSinUsuarioSeRechaza() {
        Venta v = venta("Boleta", "Efectivo", "10");
        v.setIdUsuario(0);

        assertRechazadaSinConectar(v, List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Validación: el tipo de comprobante es obligatorio")
    void ventaSinTipoDeComprobanteSeRechaza() {
        assertRechazadaSinConectar(
                venta(null, "Efectivo", "10"), List.of(detalle(5, 1)));
        assertRechazadaSinConectar(
                venta("   ", "Efectivo", "10"), List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Validación: el método de pago es obligatorio")
    void ventaSinMetodoDePagoSeRechaza() {
        assertRechazadaSinConectar(
                venta("Boleta", null, "10"), List.of(detalle(5, 1)));
        assertRechazadaSinConectar(
                venta("Boleta", "  ", "10"), List.of(detalle(5, 1)));
    }

    // =========================================================
    // 2. Caja y comprobante
    // =========================================================

    @Test
    @DisplayName("Caja: sin caja abierta no se puede vender")
    void sinCajaAbiertaNoSePuedeVender() throws Exception {
        when(cajaDAO.buscarCajaAbierta(con)).thenReturn(null);

        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> registrar(
                        venta("Boleta", "Efectivo", "100"),
                        List.of(detalle(5, 1))));

        assertTrue(error.getMessage()
                .contains("No existe una Caja abierta"));

        verify(con).rollback();
        verify(con, never()).commit();
        verify(ventaDAO, never())
                .insertar(eq(con), any(Venta.class));
    }

    @Test
    @DisplayName("Comprobante: un tipo desconocido se rechaza")
    void tipoDeComprobanteDesconocidoSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Ticket", "Efectivo", "100"),
                List.of(detalle(5, 1)));

        verify(ventaDAO, never())
                .insertar(eq(con), any(Venta.class));
    }

    // =========================================================
    // 3. Reglas sobre el lote, el stock y el precio
    // =========================================================

    @Test
    @DisplayName("Lote: un lote inexistente se rechaza")
    void loteInexistenteSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(inventarioDAO.buscarPorId(con, 5)).thenReturn(null);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Lote: un precio de venta nulo se rechaza")
    void lotePrecioNuloSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, null));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Lote: un precio de venta igual a cero se rechaza")
    void lotePrecioCeroSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "0.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Stock: no se puede vender más unidades que el stock del lote")
    void stockInsuficienteSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 2, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 3)));

        verify(inventarioDAO, never()).reducirStock(
                any(Connection.class), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Stock: se puede vender exactamente todo el stock del lote")
    void ventaDeTodoElStockEsValida() {
        prepararFlujoCorrecto("B001", 1, lote(5, 3, "10.00"));

        assertTrue(registrar(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3))));
    }

    @Test
    @DisplayName("Detalle: una cantidad igual a cero se rechaza")
    void detalleConCantidadCeroSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 0)));
    }

    @Test
    @DisplayName("Detalle: un lote con id inválido se rechaza")
    void detalleConIdDeLoteInvalidoSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(0, 1)));
    }

    @Test
    @DisplayName("Detalle: un detalle nulo dentro de la lista se rechaza")
    void detalleNuloDentroDeLaListaSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                Collections.<DetalleVenta>singletonList(null));
    }

    // =========================================================
    // 4. Descuento del lote
    // =========================================================

    @Test
    @DisplayName("Descuento: un porcentaje mayor a 100 se rechaza")
    void descuentoMayorACienSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1,
                loteConDescuento(5, 10, "10.00", "150"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Descuento: un porcentaje negativo se rechaza")
    void descuentoNegativoSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1,
                loteConDescuento(5, 10, "10.00", "-5"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "100"),
                List.of(detalle(5, 1)));
    }

    @Test
    @DisplayName("Descuento: activo pero sin porcentaje no descuenta nada")
    void descuentoActivoSinPorcentajeNoDescuenta() {
        prepararFlujoCorrecto("B001", 1,
                loteConDescuento(5, 10, "10.00", null));

        Venta v = venta("Boleta", "Efectivo", "30.00");
        DetalleVenta d = detalle(5, 3);

        assertTrue(registrar(v, List.of(d)));

        assertMonto("0.00", d.getDescuento());
        assertMonto("30.00", v.getTotal());
    }

    // =========================================================
    // 5. Pago en efectivo y vuelto
    // =========================================================

    @Test
    @DisplayName("Efectivo: un monto recibido menor al total se rechaza")
    void efectivoInsuficienteSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "20.00"),
                List.of(detalle(5, 3)));

        verify(ventaDAO, never())
                .insertar(eq(con), any(Venta.class));
    }

    @Test
    @DisplayName("Efectivo: sin monto recibido se rechaza")
    void efectivoSinMontoRecibidoSeRechaza() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", null),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Efectivo: pagar el monto exacto no genera vuelto")
    void efectivoExactoNoGeneraVuelto() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        Venta v = venta("Boleta", "Efectivo", "30.00");

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        assertMonto("30.00", v.getMontoRecibido());
        assertMonto("0.00", v.getVuelto());
    }

    @Test
    @DisplayName("Pago digital: el monto recibido y el vuelto quedan en cero")
    void pagoDigitalIgnoraMontoRecibidoYVuelto() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        Venta v = venta("Boleta", "Yape", "999.00");

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        assertMonto("0.00", v.getMontoRecibido());
        assertMonto("0.00", v.getVuelto());
    }

    // =========================================================
    // 6. Venta correcta: importes, serie y estado
    // =========================================================

    @Test
    @DisplayName("Boleta en efectivo: calcula total, IGV incluido y vuelto")
    void boletaEnEfectivoCalculaTotalesYVuelto() throws Exception {
        prepararFlujoCorrecto("B001", 7, lote(5, 10, "10.00"));

        Venta v = venta("Boleta", "Efectivo", "50.00");
        DetalleVenta d = detalle(5, 3);

        assertTrue(registrar(v, List.of(d)));

        // Serie y número de comprobante
        assertEquals("B001", v.getSerie());
        assertEquals(7, v.getNumero());
        assertEquals("B001-00000007", v.getNroComprobante());
        assertEquals(ID_VENTA, v.getIdVenta());

        // Importes de la cabecera (10.00 x 3 = 30.00 con IGV incluido)
        assertMonto("30.00", v.getTotal());
        assertMonto("25.42", v.getSubtotal());
        assertMonto("4.58", v.getIgv());
        assertMonto("50.00", v.getMontoRecibido());
        assertMonto("20.00", v.getVuelto());

        // Estado y fecha por defecto
        assertEquals("Pendiente", v.getEstadoComprobante());
        assertNotNull(v.getFechaVenta());

        // Importes del detalle
        assertEquals(ID_VENTA, d.getIdVenta());
        assertMonto("10.00", d.getPrecioUnitario());
        assertMonto("30.00", d.getSubtotal());
        assertMonto("0.00", d.getDescuento());
        assertMonto("30.00", d.getImporteTotal());

        // Transacción confirmada
        verify(con).setAutoCommit(false);
        verify(con).commit();
        verify(con, never()).rollback();
        verify(con).close();
    }

    @Test
    @DisplayName("Factura con tarjeta y descuento del 10 %: usa serie F001")
    void facturaConTarjetaYDescuentoDelLote() {
        prepararFlujoCorrecto("F001", 1,
                loteConDescuento(5, 10, "20.00", "10"));

        Venta v = venta("Factura", "Tarjeta", null);
        DetalleVenta d = detalle(5, 2);

        assertTrue(registrar(v, List.of(d)));

        assertEquals("F001", v.getSerie());
        assertEquals("F001-00000001", v.getNroComprobante());

        // 20.00 x 2 = 40.00; descuento 10 % = 4.00; total = 36.00
        assertMonto("20.00", d.getPrecioUnitario());
        assertMonto("40.00", d.getSubtotal());
        assertMonto("4.00", d.getDescuento());
        assertMonto("36.00", d.getImporteTotal());

        assertMonto("36.00", v.getTotal());
        assertMonto("30.51", v.getSubtotal());
        assertMonto("5.49", v.getIgv());
        assertMonto("0.00", v.getMontoRecibido());
        assertMonto("0.00", v.getVuelto());
    }

    @Test
    @DisplayName("El tipo de comprobante no distingue mayúsculas de minúsculas")
    void tipoDeComprobanteIgnoraMayusculas() {
        prepararFlujoCorrecto("B001", 3, lote(5, 10, "10.00"));

        Venta v = venta("BOLETA", "efectivo", "30.00");

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        assertEquals("B001", v.getSerie());
    }

    @Test
    @DisplayName("Varios detalles: el total es la suma de los importes")
    void ventaConVariosDetallesSumaLosImportes() {
        prepararFlujoCorrecto("B001", 2,
                lote(5, 10, "10.00"),
                lote(6, 10, "4.50"));

        Venta v = venta("Boleta", "Efectivo", "40.00");
        DetalleVenta d1 = detalle(5, 3);
        DetalleVenta d2 = detalle(6, 2);

        assertTrue(registrar(v, List.of(d1, d2)));

        // 10.00 x 3 = 30.00  +  4.50 x 2 = 9.00  ->  39.00
        assertMonto("30.00", d1.getImporteTotal());
        assertMonto("9.00", d2.getImporteTotal());
        assertMonto("39.00", v.getTotal());
        assertMonto("33.05", v.getSubtotal());
        assertMonto("5.95", v.getIgv());
        assertMonto("1.00", v.getVuelto());

        verify(detalleVentaDAO, times(2))
                .insertar(eq(con), any(DetalleVenta.class));
        verify(kardexDAO, times(2))
                .insertar(eq(con), any(Kardex.class));
    }

    @Test
    @DisplayName("Fecha: se respeta la fecha de venta si ya viene informada")
    void seRespetaLaFechaDeVentaInformada() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        LocalDateTime fecha = LocalDateTime.of(2026, 11, 10, 9, 30);
        Venta v = venta("Boleta", "Efectivo", "30.00");
        v.setFechaVenta(fecha);

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        assertEquals(fecha, v.getFechaVenta());
    }

    @Test
    @DisplayName("Estado: se respeta el estado de comprobante ya informado")
    void seRespetaElEstadoDeComprobanteInformado() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        Venta v = venta("Boleta", "Efectivo", "30.00");
        v.setEstadoComprobante("Aceptado");

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        assertEquals("Aceptado", v.getEstadoComprobante());
    }

    // =========================================================
    // 7. Efectos sobre stock, Kardex y Caja
    // =========================================================

    @Test
    @DisplayName("Stock: la venta descuenta del lote la cantidad vendida")
    void ventaDescuentaStockDelLote() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertTrue(registrar(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3))));

        verify(inventarioDAO).reducirStock(con, 5, 3);
    }

    @Test
    @DisplayName("Kardex: la venta registra una salida con el stock antes y después")
    void ventaRegistraSalidaEnKardex() {
        prepararFlujoCorrecto("B001", 7, lote(5, 10, "10.00"));

        Venta v = venta("Boleta", "Efectivo", "30.00");

        assertTrue(registrar(v, List.of(detalle(5, 3))));

        ArgumentCaptor<Kardex> captor =
                ArgumentCaptor.forClass(Kardex.class);
        verify(kardexDAO).insertar(eq(con), captor.capture());

        Kardex k = captor.getValue();

        assertEquals("Salida", k.getMovimiento());
        assertEquals("Venta", k.getMotivoMovimiento());
        assertEquals(3, k.getCantidad());
        assertEquals(10, k.getStockAntes());
        assertEquals(7, k.getStockDespues());
        assertEquals(9, k.getIdProducto());
        assertEquals(5, k.getIdInventario());
        assertEquals(2, k.getIdUsuario());
        assertEquals("Venta", k.getTipoReferencia());
        assertEquals(ID_VENTA, k.getIdReferencia().intValue());
        assertEquals("B001-00000007", k.getNroComprobante());
        assertMonto("10.00", k.getPrecioUnitario());
    }

    @Test
    @DisplayName("Caja: la venta registra un ingreso por el total cobrado")
    void ventaRegistraIngresoEnCaja() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));

        assertTrue(registrar(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3))));

        ArgumentCaptor<MovimientoCaja> captor =
                ArgumentCaptor.forClass(MovimientoCaja.class);
        verify(movimientoCajaDAO)
                .insertar(eq(con), captor.capture());

        MovimientoCaja m = captor.getValue();

        assertEquals("Ingreso", m.getTipoMovimiento());
        assertEquals("Venta", m.getConcepto());
        assertMonto("30.00", m.getMonto());
        assertEquals("Efectivo", m.getMetodoPago());
        assertEquals(ID_CAJA, m.getIdCaja());
        assertEquals(2, m.getIdUsuario());
        assertEquals(ID_VENTA, m.getIdVenta().intValue());
        assertNull(m.getIdCompra());
        assertTrue(m.isActivo());
    }

    @Test
    @DisplayName("Caja: el monto esperado = apertura + ingresos - salidas")
    void ventaActualizaElMontoEsperadoDeLaCaja() {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(movimientoCajaDAO.obtenerTotalIngresos(con, ID_CAJA))
                .thenReturn(new BigDecimal("30.00"));
        when(movimientoCajaDAO.obtenerTotalSalidas(con, ID_CAJA))
                .thenReturn(new BigDecimal("5.00"));

        assertTrue(registrar(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3))));

        ArgumentCaptor<BigDecimal> esperado =
                ArgumentCaptor.forClass(BigDecimal.class);

        verify(cajaDAO).actualizarTotales(
                eq(con),
                eq(ID_CAJA),
                any(BigDecimal.class),
                any(BigDecimal.class),
                esperado.capture());

        // 100.00 (apertura) + 30.00 (ingresos) - 5.00 (salidas)
        assertMonto("125.00", esperado.getValue());
    }

    // =========================================================
    // 8. Reversión de la transacción ante errores
    // =========================================================

    @Test
    @DisplayName("Reversión: si no se guarda la cabecera de la venta")
    void falloAlGuardarLaCabeceraRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(ventaDAO.insertar(eq(con), any(Venta.class)))
                .thenReturn(0);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));

        verify(inventarioDAO, never()).reducirStock(
                any(Connection.class), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Reversión: si no se guarda un detalle de la venta")
    void falloAlGuardarUnDetalleRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(detalleVentaDAO.insertar(eq(con), any(DetalleVenta.class)))
                .thenReturn(0);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Reversión: si el stock cambió y no se puede reducir")
    void falloAlReducirStockRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(inventarioDAO.reducirStock(con, 5, 3)).thenReturn(false);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));

        verify(kardexDAO, never())
                .insertar(any(Connection.class), any(Kardex.class));
    }

    @Test
    @DisplayName("Reversión: si no se registra el movimiento de Kardex")
    void falloAlRegistrarKardexRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(kardexDAO.insertar(eq(con), any(Kardex.class)))
                .thenReturn(false);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Reversión: si no se registra el ingreso en Caja")
    void falloAlRegistrarMovimientoDeCajaRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(movimientoCajaDAO.insertar(
                eq(con), any(MovimientoCaja.class))).thenReturn(false);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Reversión: si no se actualizan los totales de la Caja")
    void falloAlActualizarTotalesDeCajaRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        when(cajaDAO.actualizarTotales(
                eq(con), eq(ID_CAJA),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class))).thenReturn(false);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Reversión: una excepción SQL inesperada devuelve false")
    void excepcionSqlInesperadaRevierte() throws Exception {
        prepararFlujoCorrecto("B001", 1, lote(5, 10, "10.00"));
        doThrow(new SQLException("Error simulado"))
                .when(con).setAutoCommit(false);

        assertFallaYRevierte(
                venta("Boleta", "Efectivo", "30.00"),
                List.of(detalle(5, 3)));
    }

    @Test
    @DisplayName("Conexión: si no hay conexión a la base de datos devuelve false")
    void sinConexionALaBaseDeDatosDevuelveFalse() throws Exception {
        try (MockedStatic<ConexionBD> bd =
                mockStatic(ConexionBD.class)) {

            bd.when(ConexionBD::conectar)
                    .thenThrow(new SQLException("Sin conexión"));

            assertFalse(servicio.registrarVentaCompleta(
                    venta("Boleta", "Efectivo", "30.00"),
                    List.of(detalle(5, 3))));
        }

        verifyNoInteractions(ventaDAO, inventarioDAO, kardexDAO);
    }
}
