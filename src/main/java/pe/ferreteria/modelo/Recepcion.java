package pe.ferreteria.modelo;

import java.time.LocalDateTime;

public class Recepcion {

    private int idRecepcion;
    private int idCompra;
    private int idUsuario;
    private LocalDateTime fechaRecepcion;
    private String estado;
    private String observacion;

    public Recepcion() {
    }

    public Recepcion(int idRecepcion, int idCompra, int idUsuario,
                     LocalDateTime fechaRecepcion, String estado,
                     String observacion) {

        this.idRecepcion = idRecepcion;
        this.idCompra = idCompra;
        this.idUsuario = idUsuario;
        this.fechaRecepcion = fechaRecepcion;
        this.estado = estado;
        this.observacion = observacion;
    }

    public int getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(int idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}