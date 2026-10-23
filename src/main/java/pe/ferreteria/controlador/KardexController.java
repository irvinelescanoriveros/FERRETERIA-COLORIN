
package pe.ferreteria.controlador;

import java.util.List;

import pe.ferreteria.modelo.Kardex;
import pe.ferreteria.reporte.MermaReporte;
import pe.ferreteria.servicio.KardexService;

public class KardexController {

    private final KardexService kardexService;

    public KardexController() {
        kardexService =
                new KardexService();
    }

    public boolean registrar(
            Kardex kardex) {

        return kardexService.registrar(
                kardex
        );
    }

    public List<Kardex> listar() {

        return kardexService.listar();
    }

    public Kardex buscarPorId(
            int idKardex) {

        return kardexService.buscarPorId(
                idKardex
        );
    }

    public List<Kardex> listarPorInventario(
            int idInventario) {

        return kardexService.listarPorInventario(
                idInventario
        );
    }

    public List<Kardex> listarPorProducto(
            int idProducto) {

        return kardexService.listarPorProducto(
                idProducto
        );
    }

    public int obtenerCantidadDefectuosaPorVenta(
            int idVenta,
            int idInventario) {

        return kardexService
                .obtenerCantidadDefectuosaPorVenta(
                        idVenta,
                        idInventario
                );
    }
    public boolean revertirVenta(
        int idKardex,
        int idUsuario,
        String motivo) {

    return kardexService.revertirVenta(
            idKardex,
            idUsuario,
            motivo
    );
}
    public boolean enviarStockDefectuoso(
        int idKardex,
        int idUsuario,
        int cantidad,
        String motivo) {

    return kardexService.enviarStockDefectuoso(
            idKardex,
            idUsuario,
            cantidad,
            motivo
    );
}
    public boolean ajustarStock(
        int idInventario,
        int idUsuario,
        int nuevoStock,
        String motivo) {

    return kardexService.ajustarStock(
            idInventario,
            idUsuario,
            nuevoStock,
            motivo
    );
}

public boolean retirarStockDefectuoso(
        int idInventario,
        int idUsuario,
        int idKardexDefectuoso,
        int cantidad) {

    return kardexService.retirarStockDefectuoso(
            idInventario,
            idUsuario,
            idKardexDefectuoso,
            cantidad
    );
}
public List<Kardex> listarIngresosDefectuososPorInventario(
        int idInventario) {

    return kardexService
            .listarIngresosDefectuososPorInventario(
                    idInventario
            );
}
public boolean reponerProductoDefectuoso(
        int idKardexDefectuoso,
        int idUsuario,
        int cantidad,
        String motivo) {

    return kardexService.reponerProductoDefectuoso(
            idKardexDefectuoso,
            idUsuario,
            cantidad,
            motivo
    );
}
public boolean devolverDineroDefectuoso(
        int idKardexDefectuoso,
        int idUsuario,
        int cantidad,
        String metodoPago,
        String motivo) {

    return kardexService.devolverDineroDefectuoso(
            idKardexDefectuoso,
            idUsuario,
            cantidad,
            metodoPago,
            motivo
    );
}
public int obtenerCantidadPendienteDevolucion(
        int idKardexDefectuoso) {

    return kardexService.obtenerCantidadPendienteDevolucion(
            idKardexDefectuoso
    );
}
public boolean reingresarProductoDefectuoso(
        int idInventario,
        int idUsuario,
        int idKardexDefectuoso,
        int cantidad,
        String motivoMovimiento,
        String observacion) {

    return kardexService.reingresarProductoDefectuoso(
            idInventario,
            idUsuario,
            idKardexDefectuoso,
            cantidad,
            motivoMovimiento,
            observacion
    );
}
public List<Kardex> listarMermasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return kardexService.listarMermasParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
public List<MermaReporte> listarMermaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return kardexService.listarMermaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
}