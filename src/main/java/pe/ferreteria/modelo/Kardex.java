package pe.ferreteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Kardex {

    private int idKardex;
    private int idProducto;
    private int idInventario;
    private int idUsuario;
    private LocalDateTime fechaMovimiento;

    private String movimiento;
    private String motivoMovimiento;
    private int cantidad;

    private int stockAntes;
    private int stockDespues;

    private int stockDefectuosoAntes;
    private int stockDefectuosoDespues;

    private BigDecimal precioUnitario;
    private BigDecimal montoTotal;

    private String tipoReferencia;
    private Integer idReferencia;

    private String nroComprobante;
    private String motivo;

    public Kardex() {
    }

    public int getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(int idKardex) {
        this.idKardex = idKardex;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(
            LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getMotivoMovimiento() {
        return motivoMovimiento;
    }

    public void setMotivoMovimiento(String motivoMovimiento) {
        this.motivoMovimiento = motivoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockAntes() {
        return stockAntes;
    }

    public void setStockAntes(int stockAntes) {
        this.stockAntes = stockAntes;
    }

    public int getStockDespues() {
        return stockDespues;
    }

    public void setStockDespues(int stockDespues) {
        this.stockDespues = stockDespues;
    }

    public int getStockDefectuosoAntes() {
        return stockDefectuosoAntes;
    }

    public void setStockDefectuosoAntes(
            int stockDefectuosoAntes) {
        this.stockDefectuosoAntes
                = stockDefectuosoAntes;
    }

    public int getStockDefectuosoDespues() {
        return stockDefectuosoDespues;
    }

    public void setStockDefectuosoDespues(
            int stockDefectuosoDespues) {
        this.stockDefectuosoDespues
                = stockDefectuosoDespues;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(
            BigDecimal precioUnitario) {
        this.precioUnitario
                = precioUnitario;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(
            BigDecimal montoTotal) {
        this.montoTotal
                = montoTotal;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(
            String tipoReferencia) {
        this.tipoReferencia
                = tipoReferencia;
    }

   public Integer getIdReferencia() {
    return idReferencia;
}

public void setIdReferencia(Integer idReferencia) {
    this.idReferencia = idReferencia;
}

    public String getNroComprobante() {
        return nroComprobante;
    }

    public void setNroComprobante(
            String nroComprobante) {
        this.nroComprobante
                = nroComprobante;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
