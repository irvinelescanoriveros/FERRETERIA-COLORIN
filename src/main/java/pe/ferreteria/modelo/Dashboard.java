package pe.ferreteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Dashboard {

    // ==============================
    // VENTA
    // ==============================

    private int idVenta;
    private int idDetalle;
    private LocalDateTime fechaVenta;
    private String comprobante;

    // ==============================
    // USUARIO / VENDEDOR
    // ==============================

    private int idUsuario;
    private String usuario;

    // ==============================
    // PRODUCTO
    // ==============================

    private int idProducto;
    private String producto;
    private int cantidad;

    // ==============================
    // DATOS ECONÓMICOS DE LA LÍNEA
    // ==============================

    private BigDecimal costo;
    private BigDecimal venta;
    private BigDecimal ganancia;

    // ==============================
    // CONSTRUCTOR
    // ==============================

    public Dashboard() {
    }

    // ==============================
    // GETTERS Y SETTERS
    // ==============================

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getVenta() {
        return venta;
    }

    public void setVenta(BigDecimal venta) {
        this.venta = venta;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }
}