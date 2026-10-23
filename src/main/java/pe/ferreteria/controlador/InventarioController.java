package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.servicio.InventarioService;

import java.math.BigDecimal;
import java.util.List;
import pe.ferreteria.reporte.RotacionProductoReporte;

public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController() {
        inventarioService = new InventarioService();
    }

    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    public int registrarInventario(
            pe.ferreteria.modelo.DetalleCompra detalleCompra,
            pe.ferreteria.modelo.Compra compra,
            pe.ferreteria.modelo.Producto producto,
            int cantidadRecibida) {

        return inventarioService.registrarInventario(
                detalleCompra,
                compra,
                producto,
                cantidadRecibida
        );
    }

    public boolean actualizarUbicacion(
            int idInventario,
            String estante,
            String nivel) {

        return inventarioService.actualizarUbicacion(
                idInventario,
                estante,
                nivel
        );
    }

    public boolean actualizarStock(
            int idInventario,
            int nuevoStock) {

        return inventarioService.actualizarStock(
                idInventario,
                nuevoStock
        );
    }

    public boolean activarDescuento(
            int idInventario,
            BigDecimal porcentaje) {

        return inventarioService.activarDescuento(
                idInventario,
                porcentaje
        );
    }

    public boolean desactivarDescuento(
            int idInventario) {

        return inventarioService.desactivarDescuento(
                idInventario
        );
    }
    public boolean retirarStockDefectuoso(
        int idInventario,
        int cantidad) {

    return inventarioService.retirarStockDefectuoso(
            idInventario,
            cantidad
    );
}
    public Inventario buscarParaDashboard(int idInventario) {

    return inventarioService.buscarParaDashboard(
            idInventario
    );
}
    public List<RotacionProductoReporte> listarRotacionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return inventarioService.listarRotacionParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
}