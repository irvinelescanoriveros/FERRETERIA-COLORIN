package pe.ferreteria.modelo;

import java.time.LocalDateTime;

public class DetalleRecepcion {

    private int idDetalleRecepcion;
    private int idRecepcion;
    private int idDetalleCompra;
    private String estado;
    private LocalDateTime fechaConfirmacion;
    private Integer cantidadRecibida;
    private String observacion;

    public DetalleRecepcion() {
    }

    public DetalleRecepcion(int idDetalleRecepcion, int idRecepcion,
                            int idDetalleCompra, String estado,
                            LocalDateTime fechaConfirmacion,
                            Integer cantidadRecibida,
                            String observacion) {

        this.idDetalleRecepcion = idDetalleRecepcion;
        this.idRecepcion = idRecepcion;
        this.idDetalleCompra = idDetalleCompra;
        this.estado = estado;
        this.fechaConfirmacion = fechaConfirmacion;
        this.cantidadRecibida = cantidadRecibida;
        this.observacion = observacion;
    }

    public int getIdDetalleRecepcion() {
        return idDetalleRecepcion;
    }

    public void setIdDetalleRecepcion(int idDetalleRecepcion) {
        this.idDetalleRecepcion = idDetalleRecepcion;
    }

    public int getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(int idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public Integer getCantidadRecibida() {
        return cantidadRecibida;
    }

    public void setCantidadRecibida(Integer cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}