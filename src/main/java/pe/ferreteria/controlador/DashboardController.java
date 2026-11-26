
package pe.ferreteria.controlador;

import java.time.LocalDate;
import java.util.List;

import javax.swing.JPanel;

import pe.ferreteria.modelo.Dashboard;
import pe.ferreteria.servicio.DashboardService;

/**
 * Controlador (MVC) de Dashboard: recibe las acciones de la vista y las delega en el servicio,
 * sin acceder directamente a la base de datos.
 */
public class DashboardController {

    private final DashboardService dashboardService =
            new DashboardService();

    public List<Dashboard> listar() {
        return dashboardService.listar();
    }

    public JPanel crearGraficoVentasSemana(
            List<Dashboard> listaDashboard,
            LocalDate inicioSemana) {

        return dashboardService.crearGraficoVentasSemana(
                listaDashboard,
                inicioSemana
        );
    }
    public JPanel crearGraficoVentasMes(
        List<Dashboard> listaDashboard,
        int mes,
        int anio) {

    return dashboardService.crearGraficoVentasMes(
            listaDashboard,
            mes,
            anio
    );
}
}
