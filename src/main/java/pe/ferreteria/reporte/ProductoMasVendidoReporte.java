package pe.ferreteria.reporte;

import java.math.BigDecimal;

public class ProductoMasVendidoReporte {

    private int idProducto;
    private String producto;
    private String marca;
    private int cantidadVendida;
    private BigDecimal totalVendido;
    private BigDecimal gananciaBruta;

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

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

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
}