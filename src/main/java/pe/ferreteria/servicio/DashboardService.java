
package pe.ferreteria.servicio;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import pe.ferreteria.dao.DashboardDAO;
import pe.ferreteria.modelo.Dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardService {

    private final DashboardDAO dashboardDAO =
            new DashboardDAO();

    // ==============================
    // LISTAR DATOS DEL DASHBOARD
    // ==============================

    public List<Dashboard> listar() {
        return dashboardDAO.listar();
    }

    // ==============================
    // GRÁFICO DE VENTAS DE LA SEMANA
    // ==============================

    public ChartPanel crearGraficoVentasSemana(
            List<Dashboard> listaDashboard,
            LocalDate inicioSemana) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();
        List<String> rangosSemana =
        new java.util.ArrayList<>();

        String[] nombresDias = {
            "Lunes",
            "Martes",
            "Miércoles",
            "Jueves",
            "Viernes",
            "Sábado"
        };

        DayOfWeek[] diasSemana = {
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        };

        // ==========================================
        // CALCULAR LAS VENTAS DE CADA DÍA
        // ==========================================

        for (int i = 0; i < diasSemana.length; i++) {

            LocalDate fecha =
                    inicioSemana.with(
                            diasSemana[i]
                    );

            BigDecimal totalDia =
                    BigDecimal.ZERO;

            for (Dashboard dashboard : listaDashboard) {

                LocalDateTime fechaVenta =
                        dashboard.getFechaVenta();

                if (fechaVenta == null) {
                    continue;
                }

                LocalDate fechaVentaDia =
                        fechaVenta.toLocalDate();

                if (fechaVentaDia.equals(fecha)) {

                    if (dashboard.getVenta() != null) {

                        totalDia =
                                totalDia.add(
                                        dashboard.getVenta()
                                );
                    }
                }
            }

            dataset.addValue(
                    totalDia,
                    "Ventas",
                    nombresDias[i]
            );
        }

        // ==========================================
        // CREAR GRÁFICO
        // ==========================================

        JFreeChart chart =
                ChartFactory.createBarChart(
                        null,
                        null,
                        null,
                        dataset,
                        PlotOrientation.VERTICAL,
                        false,
                        true,
                        false
                );

        CategoryPlot plot =
                chart.getCategoryPlot();

        // ==========================================
        // FONDO Y CUADRÍCULA
        // ==========================================

        plot.setBackgroundPaint(
                java.awt.Color.WHITE
        );

        plot.setOutlineVisible(false);

        plot.setRangeGridlinesVisible(false);

        // ==========================================
        // EJE HORIZONTAL
        // ==========================================

        CategoryAxis ejeDias =
                plot.getDomainAxis();

        ejeDias.setCategoryMargin(0.20);

        ejeDias.setTickLabelFont(
                new java.awt.Font(
                        "SansSerif",
                        java.awt.Font.PLAIN,
                        12
                )
        );

        // ==========================================
        // EJE VERTICAL
        // ==========================================

        NumberAxis ejeVentas =
                (NumberAxis) plot.getRangeAxis();

        ejeVentas.setTickLabelsVisible(false);
        ejeVentas.setTickMarksVisible(false);
        ejeVentas.setAxisLineVisible(false);

        // ==========================================
        // BARRAS
        // ==========================================

        BarRenderer renderer =
                (BarRenderer) plot.getRenderer();

        renderer.setDrawBarOutline(false);

        renderer.setMaximumBarWidth(0.12);

        renderer.setDefaultToolTipGenerator(
        new CategoryToolTipGenerator() {

            @Override
            public String generateToolTip(
                    org.jfree.data.category.CategoryDataset dataset,
                    int row,
                    int column) {

                Number valor =
                        dataset.getValue(
                                row,
                                column
                        );

                BigDecimal monto =
                        BigDecimal.valueOf(
                                valor.doubleValue()
                        );

                LocalDate fecha =
                        inicioSemana.plusDays(column);

                String nombreDia =
                        fecha.getDayOfWeek()
                                .getDisplayName(
                                        java.time.format.TextStyle.FULL,
                                        new java.util.Locale(
                                                "es",
                                                "ES"
                                        )
                                );

                String nombreMes =
                        fecha.getMonth()
                                .getDisplayName(
                                        java.time.format.TextStyle.FULL,
                                        new java.util.Locale(
                                                "es",
                                                "ES"
                                        )
                                );

                return "<html>"
                        + nombreDia
                        + "<br>"
                        + fecha.getDayOfMonth()
                        + " "
                        + nombreMes
                        + "<br>"
                        + "Ventas: S/. "
                        + monto
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                )
                                .toPlainString()
                        + "</html>";
            }
        }
);

        // ==========================================
        // PANEL DEL GRÁFICO
        // ==========================================

        ChartPanel chartPanel =
                new ChartPanel(chart);

        chartPanel.setMouseWheelEnabled(false);

        chartPanel.setDomainZoomable(false);

        chartPanel.setRangeZoomable(false);

        chartPanel.setPopupMenu(null);

        return chartPanel;
    }
public ChartPanel crearGraficoVentasMes(
        List<Dashboard> listaDashboard,
        int mes,
        int anio) {

    DefaultCategoryDataset dataset =
            new DefaultCategoryDataset();

    List<String> rangosSemana =
            new java.util.ArrayList<>();

    YearMonth periodo =
            YearMonth.of(anio, mes);

    LocalDate primerDia =
            periodo.atDay(1);

    LocalDate ultimoDia =
            periodo.atEndOfMonth();

    LocalDate inicioSemana =
            primerDia.with(
                    DayOfWeek.MONDAY
            );

    int numeroSemana = 1;

    while (!inicioSemana.isAfter(ultimoDia)) {

        LocalDate finSemana =
                inicioSemana.plusDays(5);

        LocalDate inicioVisible =
                inicioSemana.isBefore(primerDia)
                        ? primerDia
                        : inicioSemana;

        LocalDate finVisible =
                finSemana.isAfter(ultimoDia)
                        ? ultimoDia
                        : finSemana;

        BigDecimal totalSemana =
                BigDecimal.ZERO;

        for (Dashboard dashboard : listaDashboard) {

            LocalDateTime fechaVenta =
                    dashboard.getFechaVenta();

            if (fechaVenta == null) {
                continue;
            }

            LocalDate fecha =
                    fechaVenta.toLocalDate();

            if (!fecha.isBefore(inicioVisible)
                    && !fecha.isAfter(finVisible)
                    && !fecha.isBefore(primerDia)
                    && !fecha.isAfter(ultimoDia)) {

                if (dashboard.getVenta() != null) {
                    totalSemana =
                            totalSemana.add(
                                    dashboard.getVenta()
                            );
                }
            }
        }

        String rangoSemana =
                inicioVisible.getDayOfMonth()
                + "–"
                + finVisible.getDayOfMonth()
                + " "
                + finVisible.getMonth()
                        .getDisplayName(
                                java.time.format.TextStyle.FULL,
                                new java.util.Locale(
                                        "es",
                                        "ES"
                                )
                        );

        rangosSemana.add(rangoSemana);

        dataset.addValue(
                totalSemana,
                "Ventas",
                "Semana " + numeroSemana
        );

        numeroSemana++;

        inicioSemana =
                inicioSemana.plusWeeks(1);
    }

    JFreeChart chart =
            ChartFactory.createBarChart(
                    null,
                    null,
                    null,
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

    CategoryPlot plot =
            chart.getCategoryPlot();

    plot.setBackgroundPaint(
            java.awt.Color.WHITE
    );

    plot.setOutlineVisible(false);

    plot.setRangeGridlinesVisible(false);

    CategoryAxis ejeSemanas =
            plot.getDomainAxis();

    ejeSemanas.setCategoryMargin(0.20);

    ejeSemanas.setTickLabelFont(
            new java.awt.Font(
                    "SansSerif",
                    java.awt.Font.PLAIN,
                    12
            )
    );

    NumberAxis ejeVentas =
            (NumberAxis) plot.getRangeAxis();

    ejeVentas.setTickLabelsVisible(false);
    ejeVentas.setTickMarksVisible(false);
    ejeVentas.setAxisLineVisible(false);

    ejeVentas.setAutoRange(true);
    ejeVentas.setAutoRangeMinimumSize(500.00);

    BarRenderer renderer =
            (BarRenderer) plot.getRenderer();

    renderer.setDrawBarOutline(false);

    renderer.setMaximumBarWidth(0.12);

    renderer.setDefaultToolTipGenerator(
            new CategoryToolTipGenerator() {

                @Override
                public String generateToolTip(
                        org.jfree.data.category.CategoryDataset dataset,
                        int row,
                        int column) {

                    Number valor =
                            dataset.getValue(
                                    row,
                                    column
                            );

                    BigDecimal monto =
                            BigDecimal.valueOf(
                                    valor.doubleValue()
                            );

                    String rango =
                            rangosSemana.get(column);

                    return "<html>"
                            + dataset.getColumnKey(column)
                            + "<br>"
                            + rango
                            + "<br>"
                            + "Ventas: S/. "
                            + monto
                                    .setScale(
                                            2,
                                            java.math.RoundingMode.HALF_UP
                                    )
                                    .toPlainString()
                            + "</html>";
                }
            }
    );

    ChartPanel chartPanel =
            new ChartPanel(chart);

    chartPanel.setMouseWheelEnabled(false);
    chartPanel.setDomainZoomable(false);
    chartPanel.setRangeZoomable(false);
    chartPanel.setPopupMenu(null);

    return chartPanel;
}
}

