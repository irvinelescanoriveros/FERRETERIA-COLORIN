package pe.ferreteria.reporte;

import java.math.BigDecimal;

public class EvolucionReporte {

    private String periodo;
    private int ventasRealizadas;
    private BigDecimal totalVendido;
    private BigDecimal gananciaBruta;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getVentasRealizadas() {
        return ventasRealizadas;
    }

    public void setVentasRealizadas(int ventasRealizadas) {
        this.ventasRealizadas = ventasRealizadas;
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