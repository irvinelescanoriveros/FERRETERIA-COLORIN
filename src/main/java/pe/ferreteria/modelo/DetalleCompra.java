package pe.ferreteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DetalleCompra {

    private int idDetalle;
    private int idCompra;
    private int idProducto;
    private String lote;
    private int cantidad;
    private BigDecimal precioCompra;
    private LocalDate fechaVencimiento;

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalle, int idCompra, int idProducto,
                         String lote, int cantidad,
                         BigDecimal precioCompra,
                         LocalDate fechaVencimiento) {

        this.idDetalle = idDetalle;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.lote = lote;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}