package pe.ferreteria.reporte;

import java.math.BigDecimal;

/**
 * Total de ventas por método de pago.
 * Objeto de transferencia de datos (DTO) de solo lectura para los reportes.
 */
public class MetodoPagoReporte {

    private String metodoPago;
    private int operaciones;
    private BigDecimal total;

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getOperaciones() {
        return operaciones;
    }

    public void setOperaciones(int operaciones) {
        this.operaciones = operaciones;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}