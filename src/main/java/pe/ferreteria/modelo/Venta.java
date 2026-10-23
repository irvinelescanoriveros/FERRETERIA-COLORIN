package pe.ferreteria.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venta {

    private int idVenta;
    private int idCliente;
    private int idUsuario;
    private LocalDateTime fechaVenta;

    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    private String tipoComprobante;
    private String serie;
    private int numero;
    private String nroComprobante;

    private String metodoPago;

    private BigDecimal montoRecibido;
    private BigDecimal vuelto;

    private String estadoComprobante;

    private LocalDateTime fechaEnvioSunat;
    private LocalDateTime fechaRespuestaSunat;

    private String codigoRespuestaSunat;
    private String mensajeRespuestaSunat;

    private boolean activo;

    public Venta() {
    }

    public Venta(
            int idVenta,
            int idCliente,
            int idUsuario,
            LocalDateTime fechaVenta,
            BigDecimal subtotal,
            BigDecimal igv,
            BigDecimal total,
            String tipoComprobante,
            String serie,
            int numero,
            String nroComprobante,
            String metodoPago,
            BigDecimal montoRecibido,
            BigDecimal vuelto,
            String estadoComprobante,
            LocalDateTime fechaEnvioSunat,
            LocalDateTime fechaRespuestaSunat,
            String codigoRespuestaSunat,
            String mensajeRespuestaSunat,
            boolean activo) {

        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.fechaVenta = fechaVenta;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.numero = numero;
        this.nroComprobante = nroComprobante;
        this.metodoPago = metodoPago;
        this.montoRecibido = montoRecibido;
        this.vuelto = vuelto;
        this.estadoComprobante = estadoComprobante;
        this.fechaEnvioSunat = fechaEnvioSunat;
        this.fechaRespuestaSunat = fechaRespuestaSunat;
        this.codigoRespuestaSunat = codigoRespuestaSunat;
        this.mensajeRespuestaSunat = mensajeRespuestaSunat;
        this.activo = activo;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNroComprobante() {
        return nroComprobante;
    }

    public void setNroComprobante(String nroComprobante) {
        this.nroComprobante = nroComprobante;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public String getEstadoComprobante() {
        return estadoComprobante;
    }

    public void setEstadoComprobante(String estadoComprobante) {
        this.estadoComprobante = estadoComprobante;
    }

    public LocalDateTime getFechaEnvioSunat() {
        return fechaEnvioSunat;
    }

    public void setFechaEnvioSunat(LocalDateTime fechaEnvioSunat) {
        this.fechaEnvioSunat = fechaEnvioSunat;
    }

    public LocalDateTime getFechaRespuestaSunat() {
        return fechaRespuestaSunat;
    }

    public void setFechaRespuestaSunat(LocalDateTime fechaRespuestaSunat) {
        this.fechaRespuestaSunat = fechaRespuestaSunat;
    }

    public String getCodigoRespuestaSunat() {
        return codigoRespuestaSunat;
    }

    public void setCodigoRespuestaSunat(String codigoRespuestaSunat) {
        this.codigoRespuestaSunat = codigoRespuestaSunat;
    }

    public String getMensajeRespuestaSunat() {
        return mensajeRespuestaSunat;
    }

    public void setMensajeRespuestaSunat(String mensajeRespuestaSunat) {
        this.mensajeRespuestaSunat = mensajeRespuestaSunat;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}