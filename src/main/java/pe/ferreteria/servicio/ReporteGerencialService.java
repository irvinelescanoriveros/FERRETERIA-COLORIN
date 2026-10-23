package pe.ferreteria.servicio;

import pe.ferreteria.reporte.DatosReporteGerencial;

public class ReporteGerencialService {

    private final VentaService ventaService;
    private final InventarioService inventarioService;
    private final KardexService kardexService;

    public ReporteGerencialService() {
        this.ventaService = new VentaService();
        this.inventarioService = new InventarioService();
        this.kardexService = new KardexService();
    }

    public DatosReporteGerencial generarDatos(
            Integer dia,
            Integer mes,
            int anio,
            Integer idUsuario) {

        DatosReporteGerencial datos
                = ventaService.obtenerResumenParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                );
        datos.setStockDefectuosoActual(
        inventarioService.obtenerStockDefectuosoActual()
);

        if (dia == null) {

            datos.setEvolucion(
                    ventaService.listarEvolucionParaReporte(
                            dia,
                            mes,
                            anio,
                            idUsuario
                    )
            );
        }

        datos.setProductosMasVendidos(
                ventaService.listarProductosMasVendidosParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        datos.setRendimientoVendedores(
                ventaService.listarRendimientoVendedoresParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        datos.setMetodosPago(
                ventaService.listarMetodosPagoParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        datos.setDetalleVentas(
                ventaService.listarDetalleVentasParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        datos.setMermas(
                kardexService.listarMermaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        datos.setRotacionProductos(
                inventarioService.listarRotacionParaReporte(
                        dia,
                        mes,
                        anio,
                        idUsuario
                )
        );

        return datos;
    }
}
