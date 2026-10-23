package pe.ferreteria.reporte;

import java.math.BigDecimal;

public class MermaReporte {

    private int idKardex;
    private int idProducto;
    private String producto;
    private String marca;
    private String motivo;
    private int cantidad;
    private BigDecimal costoMerma;

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

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoMerma() {
        return costoMerma;
    }

    public void setCostoMerma(BigDecimal costoMerma) {
        this.costoMerma = costoMerma;
    }
}