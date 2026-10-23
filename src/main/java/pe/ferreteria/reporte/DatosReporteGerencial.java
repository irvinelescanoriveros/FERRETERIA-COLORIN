package pe.ferreteria.reporte;

import java.math.BigDecimal;
import java.util.List;

public class DatosReporteGerencial {

    private BigDecimal totalVendido;
    private BigDecimal gananciaBruta;
    private int ventasRealizadas;
    private BigDecimal ticketPromedio;
    private BigDecimal igvGenerado;
    private List<EvolucionReporte> evolucion;
private List<ProductoMasVendidoReporte> productosMasVendidos;
private List<RendimientoVendedorReporte> rendimientoVendedores;
private List<MetodoPagoReporte> metodosPago;
private List<MermaReporte> mermas;
private List<RotacionProductoReporte> rotacionProductos;
private List<DetalleVentaReporte> detalleVentas;
private int stockDefectuosoActual;
    
public BigDecimal getTotalVendido() {
    return totalVendido;
}

public void setTotalVendido(BigDecimal totalVendido) {
    this.totalVendido = totalVendido;
}

public BigDecimal getGananciaBruta() {
    return gananciaBruta;
}

public void setGananciaBruta(BigDecimal gananciaBruta) {
    this.gananciaBruta = gananciaBruta;
}

public int getVentasRealizadas() {
    return ventasRealizadas;
}

public void setVentasRealizadas(int ventasRealizadas) {
    this.ventasRealizadas = ventasRealizadas;
}

public BigDecimal getTicketPromedio() {
    return ticketPromedio;
}

public void setTicketPromedio(BigDecimal ticketPromedio) {
    this.ticketPromedio = ticketPromedio;
}

public BigDecimal getIgvGenerado() {
    return igvGenerado;
}

public void setIgvGenerado(BigDecimal igvGenerado) {
    this.igvGenerado = igvGenerado;
}
public List<EvolucionReporte> getEvolucion() {
    return evolucion;
}

public void setEvolucion(List<EvolucionReporte> evolucion) {
    this.evolucion = evolucion;
}

public List<ProductoMasVendidoReporte> getProductosMasVendidos() {
    return productosMasVendidos;
}

public void setProductosMasVendidos(
        List<ProductoMasVendidoReporte> productosMasVendidos) {
    this.productosMasVendidos = productosMasVendidos;
}

public List<RendimientoVendedorReporte> getRendimientoVendedores() {
    return rendimientoVendedores;
}

public void setRendimientoVendedores(
        List<RendimientoVendedorReporte> rendimientoVendedores) {
    this.rendimientoVendedores = rendimientoVendedores;
}

public List<MetodoPagoReporte> getMetodosPago() {
    return metodosPago;
}

public void setMetodosPago(
        List<MetodoPagoReporte> metodosPago) {
    this.metodosPago = metodosPago;
}

public List<MermaReporte> getMermas() {
    return mermas;
}

public void setMermas(
        List<MermaReporte> mermas) {
    this.mermas = mermas;
}

public List<RotacionProductoReporte> getRotacionProductos() {
    return rotacionProductos;
}

public void setRotacionProductos(
        List<RotacionProductoReporte> rotacionProductos) {
    this.rotacionProductos = rotacionProductos;
}

public List<DetalleVentaReporte> getDetalleVentas() {
    return detalleVentas;
}

public void setDetalleVentas(
        List<DetalleVentaReporte> detalleVentas) {
    this.detalleVentas = detalleVentas;
}
public int getStockDefectuosoActual() {
    return stockDefectuosoActual;
}

public void setStockDefectuosoActual(int stockDefectuosoActual) {
    this.stockDefectuosoActual = stockDefectuosoActual;
}
}
