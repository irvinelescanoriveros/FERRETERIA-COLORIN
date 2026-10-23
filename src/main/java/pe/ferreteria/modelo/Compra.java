package pe.ferreteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compra {

    private int idCompra;
    private int idProveedor;
    private int idUsuario;
    private LocalDateTime fechaCompra;
    private String nroComprobante;
    private BigDecimal total;
    private boolean activo;

    public Compra() {
    }

    public Compra(int idCompra, int idProveedor, int idUsuario,
                  LocalDateTime fechaCompra, String nroComprobante,
                  BigDecimal total, boolean activo) {

        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.idUsuario = idUsuario;
        this.fechaCompra = fechaCompra;
        this.nroComprobante = nroComprobante;
        this.total = total;
        this.activo = activo;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNroComprobante() {
        return nroComprobante;
    }

    public void setNroComprobante(String nroComprobante) {
        this.nroComprobante = nroComprobante;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}