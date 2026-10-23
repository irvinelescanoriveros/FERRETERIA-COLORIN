package pe.ferreteria.modelo;

import java.math.BigDecimal;

public class DetalleVenta {

    private int idDetalle;
    private int idVenta;
    private int idInventario;

    private int cantidad;

    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    private BigDecimal descuento;
    private BigDecimal importeTotal;

    public DetalleVenta() {
    }

    public DetalleVenta(
            int idDetalle,
            int idVenta,
            int idInventario,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal importeTotal) {

        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idInventario = idInventario;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.importeTotal = importeTotal;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }
}