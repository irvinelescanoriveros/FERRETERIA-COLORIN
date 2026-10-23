package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;
import pe.ferreteria.servicio.CompraService;

import java.util.List;

public class CompraController {

    private final CompraService compraService =
            new CompraService();

    public boolean registrarCompra(
            Compra compra,
            List<DetalleCompra> detalles) {

        return compraService.registrarCompra(
                compra,
                detalles
        );
    }
    public boolean existeNroComprobante(String nroComprobante) {

    return compraService.existeNroComprobante(
            nroComprobante
    );
}
}