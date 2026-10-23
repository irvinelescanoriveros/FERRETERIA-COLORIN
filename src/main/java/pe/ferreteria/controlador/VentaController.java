package pe.ferreteria.controlador;

import java.math.BigDecimal;
import java.util.List;
import pe.ferreteria.modelo.Venta;
import pe.ferreteria.modelo.DetalleVenta;
import pe.ferreteria.reporte.DatosReporteGerencial;
import pe.ferreteria.reporte.DetalleVentaReporte;
import pe.ferreteria.reporte.EvolucionReporte;
import pe.ferreteria.reporte.MetodoPagoReporte;
import pe.ferreteria.reporte.ProductoMasVendidoReporte;
import pe.ferreteria.reporte.RendimientoVendedorReporte;
import pe.ferreteria.servicio.VentaService;

public class VentaController {

    private final VentaService ventaService;

    public VentaController() {
        ventaService = new VentaService();
    }

    public int registrarVenta(Venta venta) {

        return ventaService.registrarVenta(venta);
    }

    public int registrarDetalleVenta(
            DetalleVenta detalleVenta) {

        return ventaService.registrarDetalleVenta(
                detalleVenta
        );
    }
    public boolean registrarVentaCompleta(
        Venta venta,
        java.util.List<DetalleVenta> detalles) {

    return ventaService.registrarVentaCompleta(
            venta,
            detalles
    );
}
    public DetalleVenta buscarDetallePorVentaEInventario(
        int idVenta,
        int idInventario) {

    return ventaService.buscarDetallePorVentaEInventario(
            idVenta,
            idInventario
    );
}
    
public java.util.List<DetalleVenta> listarDetallesPorVenta(
        int idVenta) {

    return ventaService.listarDetallesPorVenta(
            idVenta
    );
}


public Venta buscarPorId(int idVenta) {
    return ventaService.buscarPorId(idVenta);
}

public java.util.List<Venta> listarParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public java.util.List<DetalleVenta> listarDetallesParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarDetallesParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public DatosReporteGerencial obtenerResumenParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.obtenerResumenParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}


public List<ProductoMasVendidoReporte> listarProductosMasVendidosParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarProductosMasVendidosParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<RendimientoVendedorReporte> listarRendimientoVendedoresParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarRendimientoVendedoresParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<MetodoPagoReporte> listarMetodosPagoParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarMetodosPagoParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<EvolucionReporte> listarEvolucionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarEvolucionParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<DetalleVentaReporte> listarDetalleVentasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return ventaService.listarDetalleVentasParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
}