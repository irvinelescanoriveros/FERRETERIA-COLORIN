
package pe.ferreteria.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import pe.ferreteria.controlador.ClienteController;
import pe.ferreteria.controlador.DashboardController;
import pe.ferreteria.controlador.VentaController;
import pe.ferreteria.modelo.Cliente;
import pe.ferreteria.modelo.Dashboard;
import pe.ferreteria.modelo.DetalleVenta;
import pe.ferreteria.modelo.Venta;
import pe.ferreteria.controlador.InventarioController;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.util.GeneradorPDF;
import pe.ferreteria.servicio.ReporteGerencialService;
import pe.ferreteria.reporte.DatosReporteGerencial;
import pe.ferreteria.controlador.UsuarioController;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.util.GeneradorReporteAnualPDF;
import pe.ferreteria.util.GeneradorReporteDiarioPDF;
import pe.ferreteria.util.GeneradorReporteMensualPDF;

public class DashboardPanel extends javax.swing.JPanel {

    private final DashboardController dashboardController =
            new DashboardController();
    private final VentaController ventaController =
        new VentaController();
    private final InventarioController inventarioController =
        new InventarioController();
private final ReporteGerencialService reporteGerencialService =
        new ReporteGerencialService();
private final ProductoController productoController =
        new ProductoController();
private final UsuarioController usuarioController =
        new UsuarioController();


    private List<Dashboard> listaDashboard =
            new ArrayList<>();

    private List<Dashboard> listaDashboardMostrada =
            new ArrayList<>();
private boolean cargandoSemanas = false;
private boolean cargandoVentasMes = false;
private boolean cargandoHistorialVentas = false;


    public DashboardPanel() {
        initComponents();
        cargarDatosDashboard();
        configurarTablaHistorialVentas();
        cargarTablaHistorialVentas(listaDashboard);
        configurarTablaRendimientoVendedores();
        cargarTablaRendimientoVendedores(listaDashboard);
        configurarTablaProductosMasVendidos();
        cargarTablaProductosMasVendidos(listaDashboard);
        cargarResumenGeneral(listaDashboard);
        cargarFiltrosDashboard();
        cargarFiltrosHistorialVentas();
        cargarFiltrosVentasMes();
        cargarSemanasVentaSemana();
        actualizarGraficoSemana();
        actualizarGraficoMes();
    }
    private void cargarDatosDashboard() {

    listaDashboard =
            dashboardController.listar();

    listaDashboardMostrada =
            new ArrayList<>(listaDashboard);
}
    private void configurarTablaHistorialVentas() {

    TableHistorialVentas.setModel(
            new javax.swing.table.DefaultTableModel(
                    new Object[][] {},
                    new String[] {
                        "N°",
                        "Fecha",
                        "Comprobante",
                        "Usuario",
                        "Costo",
                        "Venta",
                        "Ganancia"
                    }
            ) {

                @Override
                public boolean isCellEditable(
                        int row,
                        int column) {

                    return false;
                }
            }
    );
}

private void cargarTablaHistorialVentas(
        List<Dashboard> lista) {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TableHistorialVentas.getModel();

    modelo.setRowCount(0);

    int numero = 1;

    java.util.Map<Integer, Dashboard> ventasAgrupadas =
            new java.util.LinkedHashMap<>();

    for (Dashboard dashboard : lista) {

        Dashboard ventaExistente =
                ventasAgrupadas.get(dashboard.getIdVenta());

        if (ventaExistente == null) {

            Dashboard copia = new Dashboard();

            copia.setIdVenta(dashboard.getIdVenta());
            copia.setFechaVenta(dashboard.getFechaVenta());
            copia.setComprobante(dashboard.getComprobante());
            copia.setUsuario(dashboard.getUsuario());

            copia.setCosto(dashboard.getCosto());
            copia.setVenta(dashboard.getVenta());
            copia.setGanancia(dashboard.getGanancia());

            ventasAgrupadas.put(
                    dashboard.getIdVenta(),
                    copia
            );

        } else {

            ventaExistente.setCosto(
                    ventaExistente.getCosto()
                            .add(dashboard.getCosto())
            );

            ventaExistente.setVenta(
                    ventaExistente.getVenta()
                            .add(dashboard.getVenta())
            );

            ventaExistente.setGanancia(
                    ventaExistente.getGanancia()
                            .add(dashboard.getGanancia())
            );
        }
    }

    for (Dashboard dashboard :
            ventasAgrupadas.values()) {

        modelo.addRow(new Object[] {

            numero++,
            dashboard.getFechaVenta(),
            dashboard.getComprobante(),
            dashboard.getUsuario(),
            dashboard.getCosto(),
            dashboard.getVenta(),
            dashboard.getGanancia()
        });
    }

    listaDashboardMostrada =
            new ArrayList<>(
                    ventasAgrupadas.values()
            );
}


    private void configurarTablaRendimientoVendedores() {

    TableRendimientoVendedores.setModel(
            new javax.swing.table.DefaultTableModel(
                    new Object[][] {},
                    new String[] {
                        "#",
                        "Usuario",
                        "Ventas",
                        "Venta",
                        "Ganancia"
                    }
            ) {

                @Override
                public boolean isCellEditable(
                        int row,
                        int column) {

                    return false;
                }
            }
    );
}
    private void cargarTablaRendimientoVendedores(
        List<Dashboard> lista) {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TableRendimientoVendedores.getModel();

    modelo.setRowCount(0);

    java.util.Map<Integer, Dashboard> agrupado =
            new java.util.LinkedHashMap<>();

    for (Dashboard dashboard : lista) {

        int idUsuario = dashboard.getIdUsuario();

        Dashboard resumen =
                agrupado.get(idUsuario);

        if (resumen == null) {

            resumen = new Dashboard();

            resumen.setIdUsuario(idUsuario);
            resumen.setUsuario(
                    dashboard.getUsuario()
            );

            resumen.setVenta(
                    java.math.BigDecimal.ZERO
            );

            resumen.setGanancia(
                    java.math.BigDecimal.ZERO
            );

            resumen.setCantidad(0);

            agrupado.put(idUsuario, resumen);
        }

        resumen.setCantidad(
                resumen.getCantidad()
                + 1
        );

        resumen.setVenta(
                resumen.getVenta().add(
                        dashboard.getVenta()
                )
        );

        resumen.setGanancia(
                resumen.getGanancia().add(
                        dashboard.getGanancia()
                )
        );
    }

    int numero = 1;

    for (Dashboard resumen : agrupado.values()) {

        modelo.addRow(new Object[] {

            numero++,

            resumen.getUsuario(),

            resumen.getCantidad(),

            resumen.getVenta(),

            resumen.getGanancia()
        });
    }
}
    private void configurarTablaProductosMasVendidos() {

    TableProductosMasVendidos.setModel(
            new javax.swing.table.DefaultTableModel(
                    new Object[][] {},
                    new String[] {
                        "#",
                        "Producto",
                        "Unidades",
                        "Venta"
                    }
            ) {

                @Override
                public boolean isCellEditable(
                        int row,
                        int column) {

                    return false;
                }
            }
    );
}
   private void cargarTablaProductosMasVendidos(
        List<Dashboard> lista) {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TableProductosMasVendidos.getModel();

    modelo.setRowCount(0);

    java.util.Map<Integer, Dashboard> agrupado =
            new java.util.LinkedHashMap<>();

    for (Dashboard dashboard : lista) {

        int idProducto =
                dashboard.getIdProducto();

        Dashboard resumen =
                agrupado.get(idProducto);

        if (resumen == null) {

            resumen = new Dashboard();

            resumen.setIdProducto(idProducto);

            resumen.setProducto(
                    dashboard.getProducto()
            );

            resumen.setCantidad(0);

            resumen.setVenta(
                    java.math.BigDecimal.ZERO
            );

            agrupado.put(
                    idProducto,
                    resumen
            );
        }

        resumen.setCantidad(
                resumen.getCantidad()
                + dashboard.getCantidad()
        );

        resumen.setVenta(
                resumen.getVenta().add(
                        dashboard.getVenta()
                )
        );
    }

    java.util.List<Dashboard> productos =
            new java.util.ArrayList<>(
                    agrupado.values()
            );

    productos.sort(
            (a, b) -> {

                int comparacion =
                        Integer.compare(
                                b.getCantidad(),
                                a.getCantidad()
                        );

                if (comparacion == 0) {

                    comparacion =
                            b.getVenta().compareTo(
                                    a.getVenta()
                            );
                }

                return comparacion;
            }
    );

    int numero = 1;

    for (Dashboard producto : productos) {

        modelo.addRow(new Object[] {

            numero++,

            producto.getProducto(),

            producto.getCantidad(),

            producto.getVenta()
        });
    }
} 
private void cargarResumenGeneral(
        List<Dashboard> lista) {

    java.math.BigDecimal totalVendido =
            java.math.BigDecimal.ZERO;

    java.math.BigDecimal gananciaBruta =
            java.math.BigDecimal.ZERO;

    java.util.Set<Integer> ventas =
            new java.util.HashSet<>();

    for (Dashboard dashboard : lista) {

        totalVendido =
                totalVendido.add(
                        dashboard.getVenta()
                );

        gananciaBruta =
                gananciaBruta.add(
                        dashboard.getGanancia()
                );

        ventas.add(
                dashboard.getIdVenta()
        );
    }

    LabelTotalVendidoGeneral.setText(
            "S/. "
            + totalVendido
                    .setScale(
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
                    .toPlainString()
    );

    LabelVentasRealizadas.setText(
            String.valueOf(
                    ventas.size()
            )
    );

    LabelGananciaBruta.setText(
            "S/. "
            + gananciaBruta
                    .setScale(
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
                    .toPlainString()
    );
} 
    private void cargarFiltrosDashboard() {

    ComboDiaGeneral.removeAllItems();
    ComboMesGeneral.removeAllItems();
    ComboAnioGeneral.removeAllItems();

    // =========================
    // AÑO
    // =========================

    ComboAnioGeneral.addItem("Todos");

    int anioActual = LocalDate.now().getYear();

for (int anio = anioActual; anio >= anioActual - 2; anio--) {
    ComboAnioGeneral.addItem(
            String.valueOf(anio)
    );
}

    // =========================
    // MES
    // =========================

    ComboMesGeneral.addItem("Todos");

    String[] meses = {
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
    };

    for (String mes : meses) {
        ComboMesGeneral.addItem(mes);
    }

    // =========================
    // DÍA
    // =========================

    ComboDiaGeneral.addItem("Todos");

    for (int dia = 1; dia <= 31; dia++) {
        ComboDiaGeneral.addItem(
                String.valueOf(dia)
        );
    }

    // =========================
    // FECHA ACTUAL
    // =========================

    LocalDate hoy = LocalDate.now();

    ComboAnioGeneral.setSelectedItem(
            String.valueOf(hoy.getYear())
    );

    ComboMesGeneral.setSelectedIndex(
            hoy.getMonthValue()
    );

    ComboDiaGeneral.setSelectedItem(
            String.valueOf(hoy.getDayOfMonth())
    );
}
    private void cargarFiltrosVentasMes() {

    cargandoVentasMes = true;

    ComboMesVentaMes.removeAllItems();
    comboVentasMesAnio.removeAllItems();

    String[] meses = {
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
    };

    for (String mes : meses) {
        ComboMesVentaMes.addItem(mes);
    }

    int anioActual = LocalDate.now().getYear();

for (int anio = anioActual; anio >= anioActual - 2; anio--) {
    comboVentasMesAnio.addItem(
            String.valueOf(anio)
    );
}

    LocalDate hoy = LocalDate.now();

    ComboMesVentaMes.setSelectedIndex(
            hoy.getMonthValue() - 1
    );

    comboVentasMesAnio.setSelectedItem(
            String.valueOf(hoy.getYear())
    );

    cargandoVentasMes = false;
}
    
   private void actualizarDiasGeneral() {

    String mesSeleccionado =
            (String) ComboMesGeneral.getSelectedItem();

    String anioSeleccionado =
            (String) ComboAnioGeneral.getSelectedItem();

    if (mesSeleccionado == null
            || anioSeleccionado == null) {
        return;
    }

    ComboDiaGeneral.removeAllItems();
    ComboDiaGeneral.addItem("Todos");

    if ("Todos".equals(mesSeleccionado)
            || "Todos".equals(anioSeleccionado)) {

        for (int dia = 1; dia <= 31; dia++) {
            ComboDiaGeneral.addItem(String.valueOf(dia));
        }
        return;
    }

    int mes = ComboMesGeneral.getSelectedIndex();
    int anio = Integer.parseInt(anioSeleccionado);

    int cantidadDias =
            YearMonth.of(anio, mes).lengthOfMonth();

    for (int dia = 1; dia <= cantidadDias; dia++) {
        ComboDiaGeneral.addItem(String.valueOf(dia));
    }
}
private List<Dashboard> filtrarDashboardGeneral() {

    String diaSeleccionado =
            (String) ComboDiaGeneral.getSelectedItem();

    String mesSeleccionado =
            (String) ComboMesGeneral.getSelectedItem();

    String anioSeleccionado =
            (String) ComboAnioGeneral.getSelectedItem();

    List<Dashboard> listaFiltrada =
            new ArrayList<>();

    for (Dashboard dashboard : listaDashboard) {

        if (dashboard.getFechaVenta() == null) {
            continue;
        }

        boolean cumpleDia = true;
        boolean cumpleMes = true;
        boolean cumpleAnio = true;

        if (!"Todos".equals(diaSeleccionado)) {

            int dia =
                    Integer.parseInt(diaSeleccionado);

            cumpleDia =
                    dashboard.getFechaVenta().getDayOfMonth()
                    == dia;
        }

        if (!"Todos".equals(mesSeleccionado)) {

            int mes =
                    ComboMesGeneral.getSelectedIndex();

            cumpleMes =
                    dashboard.getFechaVenta().getMonthValue()
                    == mes;
        }

        if (!"Todos".equals(anioSeleccionado)) {

            int anio =
                    Integer.parseInt(anioSeleccionado);

            cumpleAnio =
                    dashboard.getFechaVenta().getYear()
                    == anio;
        }

        if (cumpleDia && cumpleMes && cumpleAnio) {
            listaFiltrada.add(dashboard);
        }
    }

    return listaFiltrada;
}


private void actualizarDashboardGeneral() {

    if (ComboDiaGeneral.getSelectedItem() == null
            || ComboMesGeneral.getSelectedItem() == null
            || ComboAnioGeneral.getSelectedItem() == null) {
        return;
    }

    List<Dashboard> listaFiltrada =
            filtrarDashboardGeneral();

    cargarResumenGeneral(listaFiltrada);

    cargarTablaRendimientoVendedores(
            listaFiltrada
    );

    cargarTablaProductosMasVendidos(
            listaFiltrada
    );
}
private void cargarGraficoSemana(LocalDate inicioSemana) {

    panelGraficoSemana.removeAll();

    javax.swing.JPanel grafico =
            dashboardController.crearGraficoVentasSemana(
                    listaDashboard,
                    inicioSemana
            );
    grafico.setPreferredSize(
        new java.awt.Dimension(164, 247)
);
panelGraficoSemana.setLayout(
        new java.awt.BorderLayout()
);

panelGraficoSemana.add(
        grafico,
        java.awt.BorderLayout.CENTER
);

    panelGraficoSemana.revalidate();
    panelGraficoSemana.repaint();
}
private void cargarGraficoMes(int mes, int anio) {

    panelGraficoMes.removeAll();

    javax.swing.JPanel grafico =
            dashboardController.crearGraficoVentasMes(
                    listaDashboard,
                    mes,
                    anio
            );

    grafico.setPreferredSize(
            new java.awt.Dimension(164, 247)
    );

    panelGraficoMes.setLayout(
            new java.awt.BorderLayout()
    );

    panelGraficoMes.add(
            grafico,
            java.awt.BorderLayout.CENTER
    );

    panelGraficoMes.revalidate();
    panelGraficoMes.repaint();
}

private void actualizarGraficoMes() {

    int mes =
            ComboMesVentaMes.getSelectedIndex() + 1;

    String anioSeleccionado =
            (String) comboVentasMesAnio.getSelectedItem();

    if (anioSeleccionado == null) {
        return;
    }

    int anio =
            Integer.parseInt(anioSeleccionado);

    cargarGraficoMes(
            mes,
            anio
    );
}

private void cargarSemanasVentaSemana() {

    cargandoSemanas = true;

    comboSemanaVentaSemana.removeAllItems();

    LocalDate hoy = LocalDate.now();

    LocalDate inicioSemanaActual =
            hoy.with(DayOfWeek.MONDAY);

    LocalDate fechaInicio =
            inicioSemanaActual.minusWeeks(8);

    LocalDate semana =
            inicioSemanaActual;

    while (!semana.isBefore(fechaInicio)) {

        LocalDate finSemana =
                semana.plusDays(5);

        String texto =
                semana.getDayOfMonth()
                + " al "
                + finSemana.getDayOfMonth()
                + " "
                + obtenerNombreMes(
                        semana.getMonthValue()
                )
                + " "
                + semana.getYear();

        comboSemanaVentaSemana.addItem(texto);

        semana =
                semana.minusWeeks(1);
    }

    comboSemanaVentaSemana.setSelectedIndex(0);

    cargandoSemanas = false;
}
private String obtenerNombreMes(int mes) {

    String[] meses = {
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
    };

    return meses[mes - 1];
}
private void actualizarGraficoSemana() {

    LocalDate inicioSemana =
            obtenerInicioSemanaSeleccionada();

    if (inicioSemana == null) {
        return;
    }

    cargarGraficoSemana(inicioSemana);
}
private LocalDate obtenerInicioSemanaSeleccionada() {

    String texto =
            (String) comboSemanaVentaSemana.getSelectedItem();

    if (texto == null || texto.isBlank()) {
        return null;
    }

    String[] partes =
            texto.split(" ");

    int diaInicio =
            Integer.parseInt(partes[0]);

    String nombreMes =
            partes[3];

    int anio =
            Integer.parseInt(partes[4]);

    int mes = 1;

    String[] meses = {
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
    };

    for (int i = 0; i < meses.length; i++) {

        if (meses[i].equals(nombreMes)) {
            mes = i + 1;
            break;
        }
    }

    return LocalDate.of(
            anio,
            mes,
            diaInicio
    );
}

private void cargarFiltrosHistorialVentas() {

    cargandoHistorialVentas = true;

    ComboDiaHistorialVenta.removeAllItems();
    comboMesHistorialVentas.removeAllItems();
    comboAnioHistorialVentas.removeAllItems();
    ComboUsuarioHistorialVentas.removeAllItems();

    // Día
    ComboDiaHistorialVenta.addItem("Todos");

    for (int dia = 1; dia <= 31; dia++) {
        ComboDiaHistorialVenta.addItem(
                String.valueOf(dia)
        );
    }

    // Mes
    comboMesHistorialVentas.addItem("Todos");

    String[] meses = {
        "Enero", "Febrero", "Marzo", "Abril",
        "Mayo", "Junio", "Julio", "Agosto",
        "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    for (String mes : meses) {
        comboMesHistorialVentas.addItem(mes);
    }

    // Año
    comboAnioHistorialVentas.addItem("Todos");

    int anioActual = LocalDate.now().getYear();

    for (int anio = anioActual;
         anio >= anioActual - 2;
         anio--) {

        comboAnioHistorialVentas.addItem(
                String.valueOf(anio)
        );
    }

    // Usuario
    ComboUsuarioHistorialVentas.addItem("Todos");

List<Usuario> usuarios = usuarioController.listar();

for (Usuario usuario : usuarios) {

    if (usuario.isActivo()
            && ("Administrador".equals(usuario.getRol())
            || "Vendedor".equals(usuario.getRol()))) {

        ComboUsuarioHistorialVentas.addItem(
                usuario.getNombre()
                + " "
                + usuario.getApellidos()
        );
    }
}

    // Valores iniciales
    LocalDate hoy = LocalDate.now();

ComboDiaHistorialVenta.setSelectedItem(
        String.valueOf(hoy.getDayOfMonth())
);

comboMesHistorialVentas.setSelectedIndex(
        hoy.getMonthValue()
);

comboAnioHistorialVentas.setSelectedItem(
        String.valueOf(hoy.getYear())
);

    ComboUsuarioHistorialVentas.setSelectedItem("Todos");

    cargandoHistorialVentas = false;
}

private void actualizarHistorialVentas() {

    if (cargandoHistorialVentas) {
        return;
    }

    String diaSeleccionado =
            (String) ComboDiaHistorialVenta.getSelectedItem();

    String mesSeleccionado =
            (String) comboMesHistorialVentas.getSelectedItem();

    String anioSeleccionado =
            (String) comboAnioHistorialVentas.getSelectedItem();

    String usuarioSeleccionado =
            (String) ComboUsuarioHistorialVentas.getSelectedItem();

    List<Dashboard> listaFiltrada =
            new ArrayList<>();

    for (Dashboard dashboard : listaDashboard) {

        LocalDate fecha =
                dashboard.getFechaVenta().toLocalDate();

        // Día
        if (diaSeleccionado != null
                && !diaSeleccionado.equals("Todos")) {

            if (fecha.getDayOfMonth()
                    != Integer.parseInt(diaSeleccionado)) {
                continue;
            }
        }

        // Mes
        if (mesSeleccionado != null
                && !mesSeleccionado.equals("Todos")) {

            int mes =
                    comboMesHistorialVentas.getSelectedIndex();

            if (fecha.getMonthValue() != mes) {
                continue;
            }
        }

        // Año
        if (anioSeleccionado != null
                && !anioSeleccionado.equals("Todos")) {

            if (fecha.getYear()
                    != Integer.parseInt(anioSeleccionado)) {
                continue;
            }
        }

        // Usuario
        if (usuarioSeleccionado != null
                && !usuarioSeleccionado.equals("Todos")) {

            if (!usuarioSeleccionado.equals(
                    dashboard.getUsuario())) {
                continue;
            }
        }

        listaFiltrada.add(dashboard);
    }

    cargarTablaHistorialVentas(listaFiltrada);
}
private void verDetalleVentaSeleccionada() {

    int fila = TableHistorialVentas.getSelectedRow();

    // 1. Validar que seleccionó una fila
    if (fila == -1) {

        JOptionPane.showMessageDialog(
                this,
                "Seleccione una venta.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    Dashboard dashboard =
            listaDashboardMostrada.get(fila);

    int idVenta = dashboard.getIdVenta();

    // 2. Buscar la venta
    Venta venta =
            ventaController.buscarPorId(idVenta);

    if (venta == null) {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo encontrar la venta seleccionada.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    // 3. Buscar sus detalles
    List<DetalleVenta> detalles =
            ventaController.listarDetallesPorVenta(idVenta);

    if (detalles == null || detalles.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "La venta no tiene detalles registrados.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }
    Cliente cliente =
        new ClienteController()
                .buscarPorId(venta.getIdCliente());

if (cliente == null) {

    JOptionPane.showMessageDialog(
            this,
            "No se pudo encontrar el cliente de la venta.",
            "Error",
            JOptionPane.ERROR_MESSAGE
    );

    return;
}
for (DetalleVenta detalle : detalles) {

    Inventario inventario =
            inventarioController.buscarParaDashboard(
                    detalle.getIdInventario()
            );

    if (inventario == null) {
        continue;
    }

    Producto producto =
            productoController.buscarParaDashboard(
                    inventario.getIdProducto()
            );

    if (producto == null) {
        continue;
    }

    // Aquí ya tenemos:
    // inventario → lote
    // producto → nombre y unidad
    // detalle → cantidad, precio, descuento e importe
}
JPanel panelDetalle =
        new JPanel(new BorderLayout(10, 10));

JPanel panelInformacion =
        new JPanel(new GridLayout(0, 2, 8, 5));

panelInformacion.add(
        new JLabel("Fecha y hora:")
);

panelInformacion.add(
        new JLabel(
                venta.getFechaVenta().toString()
        )
);

panelInformacion.add(
        new JLabel("Moneda:")
);

panelInformacion.add(
        new JLabel("Soles (S/)")
);

panelInformacion.add(
        new JLabel("Forma de pago:")
);

panelInformacion.add(
        new JLabel(
                venta.getMetodoPago()
        )
);

panelInformacion.add(
        new JLabel("Usuario:")
);

panelInformacion.add(
        new JLabel(
                dashboard.getUsuario()
        )
);

panelInformacion.add(
        new JLabel("Cliente:")
);

String nombreCliente;

if ("Natural".equals(
        cliente.getTipoCliente())) {

    nombreCliente =
            (cliente.getNombre() + " "
                    + cliente.getApellidos()).trim();

} else {

    nombreCliente =
            cliente.getRazonSocial();
}

panelInformacion.add(
        new JLabel(nombreCliente)
);

panelInformacion.add(
        new JLabel("Documento:")
);

String documento;

if ("Natural".equals(
        cliente.getTipoCliente())) {

    documento = cliente.getDni();

} else {

    documento = cliente.getRuc();
}

panelInformacion.add(
        new JLabel(
                documento != null
                        ? documento
                        : ""
        )
);

panelDetalle.add(
        panelInformacion,
        BorderLayout.NORTH
);
javax.swing.table.DefaultTableModel modeloDetalle =
        new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "N°",
                    "Lote",
                    "Producto",
                    "Unidad",
                    "Cantidad",
                    "Precio unitario",
                    "Descuento",
                    "Importe"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {
                return false;
            }
        };

javax.swing.JTable tablaDetalle =
        new javax.swing.JTable(modeloDetalle);

int numeroDetalle = 1;

for (DetalleVenta detalle : detalles) {

    Inventario inventario =
            inventarioController.buscarParaDashboard(
                    detalle.getIdInventario()
            );

    if (inventario == null) {
        continue;
    }

    Producto producto =
            productoController.buscarParaDashboard(
                    inventario.getIdProducto()
            );

    if (producto == null) {
        continue;
    }

    modeloDetalle.addRow(new Object[]{

        numeroDetalle++,
        inventario.getLote(),
        producto.getNombre(),
        producto.getUnidad(),
        detalle.getCantidad(),
        detalle.getPrecioUnitario(),
        detalle.getDescuento(),
        detalle.getImporteTotal()
    });
}

panelDetalle.add(
        new javax.swing.JScrollPane(tablaDetalle),
        BorderLayout.CENTER
);
JPanel panelResumen =
        new JPanel(new GridLayout(0, 2, 8, 5));

panelResumen.add(
        new JLabel("Monto recibido:")
);

panelResumen.add(
        new JLabel(
                formatear(venta.getMontoRecibido())
        )
);

panelResumen.add(
        new JLabel("Vuelto:")
);

panelResumen.add(
        new JLabel(
                formatear(venta.getVuelto())
        )
);

panelResumen.add(
        new JLabel("Operaciones gravadas:")
);

panelResumen.add(
        new JLabel(
                formatear(venta.getSubtotal())
        )
);

panelResumen.add(
        new JLabel("IGV:")
);

panelResumen.add(
        new JLabel(
                formatear(venta.getIgv())
        )
);

panelResumen.add(
        new JLabel("Importe total:")
);

panelResumen.add(
        new JLabel(
                formatear(venta.getTotal())
        )
);

panelDetalle.add(
        panelResumen,
        BorderLayout.SOUTH
);
JPanel panelBotones =
        new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

JButton btnAbrirPdf =
        new JButton("Abrir PDF");

JButton btnCerrar =
        new JButton("Cerrar");

panelBotones.add(btnAbrirPdf);
panelBotones.add(btnCerrar);

btnAbrirPdf.addActionListener(e -> {

    try {

        GeneradorPDF.abrirExistente(
                venta.getTipoComprobante(),
                venta.getNroComprobante(),
                venta.getFechaVenta()
        );

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo abrir el PDF.\n"
                + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
});

panelDetalle.add(
        panelBotones,
        BorderLayout.PAGE_END
);
btnCerrar.addActionListener(e -> {

    javax.swing.SwingUtilities
            .getWindowAncestor(panelDetalle)
            .dispose();
});

javax.swing.JDialog dialogo =
        new javax.swing.JDialog(
                javax.swing.SwingUtilities
                        .getWindowAncestor(this),
                "Detalle de la venta",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL
        );

dialogo.setContentPane(panelDetalle);

dialogo.pack();

dialogo.setSize(500, 400);

dialogo.setLocationRelativeTo(this);

dialogo.setVisible(true);
}


private String formatear(BigDecimal valor) {

    if (valor == null) {
        return "S/. 0.00";
    }

    return String.format(
            "S/. %.2f",
            valor
    );
}
private void generarReporteGerencial() {

    String diaSeleccionado =
            (String) ComboDiaHistorialVenta.getSelectedItem();

    String mesSeleccionado =
            (String) comboMesHistorialVentas.getSelectedItem();

    String anioSeleccionado =
            (String) comboAnioHistorialVentas.getSelectedItem();

    String usuarioSeleccionado =
            (String) ComboUsuarioHistorialVentas.getSelectedItem();

    // =========================
    // VALIDAR AÑO
    // =========================

    if (anioSeleccionado == null
        || "Todos".equals(anioSeleccionado)) {

    JOptionPane.showMessageDialog(
            this,
            "Seleccione un año para generar el reporte.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
    );

    return;
}

// =========================
// VALIDAR PERIODO
// =========================

boolean diaTodos =
        diaSeleccionado == null
        || "Todos".equals(diaSeleccionado);

boolean mesTodos =
        mesSeleccionado == null
        || "Todos".equals(mesSeleccionado);

// Diario: Día + Mes + Año
if (!diaTodos && mesTodos) {

    JOptionPane.showMessageDialog(
            this,
            "Para un reporte diario debe seleccionar un mes.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
    );

    return;
}

// Anual: Día Todos + Mes Todos + Año
// Mensual: Día Todos + Mes específico + Año
// Diario: Día específico + Mes específico + Año

// No permitir los tres filtros en Todos
if (diaTodos && mesTodos) {
    // Este caso es anual y es válido porque el año ya fue seleccionado.
}

// No permitir Día específico + Mes Todos
// (ya controlado arriba)
    // =========================
    // OBTENER DÍA
    // =========================

    Integer dia = null;

    if (diaSeleccionado != null
            && !"Todos".equals(diaSeleccionado)) {

        dia = Integer.parseInt(
                diaSeleccionado
        );
    }

    // =========================
    // OBTENER MES
    // =========================

    Integer mes = null;

    if (mesSeleccionado != null
            && !"Todos".equals(mesSeleccionado)) {

        mes =
                comboMesHistorialVentas
                        .getSelectedIndex();
    }

    // =========================
    // OBTENER AÑO
    // =========================

    int anio =
            Integer.parseInt(
                    anioSeleccionado
            );
    
    // =========================
// VALIDAR FECHA FUTURA
// =========================

LocalDate hoy = LocalDate.now();

if (anio > hoy.getYear()) {

    JOptionPane.showMessageDialog(
            this,
            "No se puede generar un reporte de un año futuro.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
    );

    return;
}

if (anio == hoy.getYear() && mes != null) {

    if (mes > hoy.getMonthValue()) {

        JOptionPane.showMessageDialog(
                this,
                "No se puede generar un reporte de un mes futuro.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    if (mes == hoy.getMonthValue()
            && dia != null
            && dia > hoy.getDayOfMonth()) {

        JOptionPane.showMessageDialog(
                this,
                "No se puede generar un reporte de una fecha futura.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }
}

    // =========================
    // OBTENER USUARIO
    // =========================

    Integer idUsuario = null;

    if (usuarioSeleccionado != null
            && !"Todos".equals(usuarioSeleccionado)) {

        List<Usuario> usuarios =
                usuarioController.listar();

        for (Usuario usuario : usuarios) {

            String nombreCompleto =
        usuario.getNombre()
        + " "
        + usuario.getApellidos();

if (nombreCompleto.equals(usuarioSeleccionado)) {

    idUsuario =
            usuario.getIdUsuario();

    break;
}
        }
    }

    // =========================
    // GENERAR DATOS
    // =========================

    DatosReporteGerencial datos =
            reporteGerencialService.generarDatos(
                    dia,
                    mes,
                    anio,
                    idUsuario
            );
   try {

    String usuarioReporte =
            usuarioSeleccionado == null
            || "Todos".equals(usuarioSeleccionado)
            ? null
            : usuarioSeleccionado;

    java.nio.file.Path ruta;

    if (dia != null) {

    GeneradorReporteDiarioPDF generador =
            new GeneradorReporteDiarioPDF();

    ruta = generador.generar(
            datos,
            dia,
            mes,
            anio,
            usuarioReporte
    );

} else if (mes != null) {

    GeneradorReporteMensualPDF generador =
            new GeneradorReporteMensualPDF();

    ruta = generador.generar(
            datos,
            mes,
            anio,
            usuarioReporte
    );

} else {

    GeneradorReporteAnualPDF generador =
            new GeneradorReporteAnualPDF();

    ruta = generador.generar(
            datos,
            anio,
            usuarioReporte
    );
}

    java.awt.Desktop.getDesktop().open(
            ruta.toFile()
    );

    JOptionPane.showMessageDialog(
            this,
            "Reporte generado correctamente.\n\n"
            + "Archivo:\n"
            + ruta.toString(),
            "Reporte gerencial",
            JOptionPane.INFORMATION_MESSAGE
    );

} catch (Exception ex) {

    JOptionPane.showMessageDialog(
            this,
            "No se pudo generar el reporte.\n"
            + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
    );
}}
private void actualizarDashboard() {

    cargarDatosDashboard();

    cargarFiltrosDashboard();
    cargarFiltrosHistorialVentas();
    cargarFiltrosVentasMes();
    cargarSemanasVentaSemana();

    cargarTablaHistorialVentas(
            listaDashboard
    );

    cargarTablaRendimientoVendedores(
            listaDashboard
    );

    cargarTablaProductosMasVendidos(
            listaDashboard
    );

    cargarResumenGeneral(
            listaDashboard
    );

    actualizarDashboardGeneral();

    actualizarGraficoSemana();

    actualizarGraficoMes();

    actualizarHistorialVentas();
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        ComboDiaGeneral = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        LabelGananciaBruta = new javax.swing.JLabel();
        LabelVentasRealizadas = new javax.swing.JLabel();
        LabelTotalVendidoGeneral = new javax.swing.JLabel();
        ComboMesGeneral = new javax.swing.JComboBox<>();
        ComboAnioGeneral = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ComboDiaHistorialVenta = new javax.swing.JComboBox<>();
        comboAnioHistorialVentas = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        comboMesHistorialVentas = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableHistorialVentas = new javax.swing.JTable();
        ComboUsuarioHistorialVentas = new javax.swing.JComboBox<>();
        btnVerDetalles = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableRendimientoVendedores = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        TableProductosMasVendidos = new javax.swing.JTable();
        panelGraficoSemana = new javax.swing.JPanel();
        comboSemanaVentaSemana = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        panelGraficoMes = new javax.swing.JPanel();
        ComboMesVentaMes = new javax.swing.JComboBox<>();
        comboVentasMesAnio = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(238, 238, 246));

        jPanel5.setBackground(new java.awt.Color(238, 238, 246));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DASHBOARD - REPORTE DE VENTAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N
        jPanel5.setToolTipText("");

        ComboDiaGeneral.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoy", "Ultima semana", "Ultimos 30 dias", "Ultimos 3 meses", "Ultimos 6 meses" }));
        ComboDiaGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboDiaGeneralActionPerformed(evt);
            }
        });

        jLabel21.setText("TOTAL VENDIDO");

        btnActualizar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        jLabel22.setText("Dia");

        jLabel11.setText("VENTAS REALIZADAS");

        jLabel12.setText("GANANCIA BRUTA");

        LabelGananciaBruta.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        LabelGananciaBruta.setText(":");

        LabelVentasRealizadas.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        LabelVentasRealizadas.setText(":");

        LabelTotalVendidoGeneral.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        LabelTotalVendidoGeneral.setText(":");

        ComboMesGeneral.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "septiembre" }));
        ComboMesGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboMesGeneralActionPerformed(evt);
            }
        });

        ComboAnioGeneral.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026" }));
        ComboAnioGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboAnioGeneralActionPerformed(evt);
            }
        });

        jLabel30.setText("Año");

        jLabel34.setText("Mes ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboDiaGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ComboMesGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ComboAnioGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelTotalVendidoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(38, 38, 38)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelVentasRealizadas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelGananciaBruta, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ComboDiaGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(ComboMesGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboAnioGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel34))
                .addGap(44, 44, 44))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(LabelGananciaBruta)
                    .addComponent(LabelVentasRealizadas)
                    .addComponent(jLabel11)
                    .addComponent(LabelTotalVendidoGeneral)
                    .addComponent(jLabel21)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(238, 238, 246));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HISTORIAL DE VENTAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N
        jPanel3.setToolTipText("");

        ComboDiaHistorialVenta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Hoy", "Ultima semana", "Ultimos 30 dias", "Ultimos 3 meses", "Ultimos 6 meses" }));
        ComboDiaHistorialVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboDiaHistorialVentaActionPerformed(evt);
            }
        });

        comboAnioHistorialVentas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", "2025", "2024", "2023", " " }));
        comboAnioHistorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAnioHistorialVentasActionPerformed(evt);
            }
        });

        jLabel19.setText("Año");

        jLabel20.setText("Dia");

        comboMesHistorialVentas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        comboMesHistorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMesHistorialVentasActionPerformed(evt);
            }
        });

        jLabel23.setText("Mes");

        jLabel24.setText("Usuario");

        TableHistorialVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "N°", "Fecha", "Comprobante", "Usuario", "Costo", "Venta", "ganacia"
            }
        ));
        jScrollPane1.setViewportView(TableHistorialVentas);

        ComboUsuarioHistorialVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboUsuarioHistorialVentasActionPerformed(evt);
            }
        });

        btnVerDetalles.setText("Ver Detalles");
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });

        jButton1.setText("GENERAR REPEPORTE PDF");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboDiaHistorialVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboMesHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboAnioHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboUsuarioHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVerDetalles)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ComboDiaHistorialVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboAnioHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(comboMesHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(ComboUsuarioHistorialVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerDetalles)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(233, 233, 233))
        );

        jPanel4.setBackground(new java.awt.Color(238, 238, 246));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RENDIMIENTO DE VENDEDORES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N
        jPanel4.setToolTipText("");

        TableRendimientoVendedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "#", "Usuario", "Ventas ", "Venta", "ganancia"
            }
        ));
        jScrollPane2.setViewportView(TableRendimientoVendedores);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel15.setBackground(new java.awt.Color(238, 238, 246));
        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PRODUCTOS MAS VENDIDOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N
        jPanel15.setToolTipText("");

        TableProductosMasVendidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "#", "Producto", "Unidades", "Venta"
            }
        ));
        jScrollPane9.setViewportView(TableProductosMasVendidos);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelGraficoSemana.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelGraficoSemanaLayout = new javax.swing.GroupLayout(panelGraficoSemana);
        panelGraficoSemana.setLayout(panelGraficoSemanaLayout);
        panelGraficoSemanaLayout.setHorizontalGroup(
            panelGraficoSemanaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelGraficoSemanaLayout.setVerticalGroup(
            panelGraficoSemanaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
        );

        comboSemanaVentaSemana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSemanaVentaSemanaActionPerformed(evt);
            }
        });

        jLabel25.setText("Semana");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setText("VENTAS DE LA SEMANA");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setText("VENTAS DEL MES");

        panelGraficoMes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelGraficoMesLayout = new javax.swing.GroupLayout(panelGraficoMes);
        panelGraficoMes.setLayout(panelGraficoMesLayout);
        panelGraficoMesLayout.setHorizontalGroup(
            panelGraficoMesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelGraficoMesLayout.setVerticalGroup(
            panelGraficoMesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 235, Short.MAX_VALUE)
        );

        ComboMesVentaMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Septiembre" }));
        ComboMesVentaMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboMesVentaMesActionPerformed(evt);
            }
        });

        comboVentasMesAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", " " }));
        comboVentasMesAnio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboVentasMesAnioActionPerformed(evt);
            }
        });

        jLabel28.setText("Mes");

        jLabel29.setText("Año");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboMesVentaMes, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboVentasMesAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboSemanaVentaSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelGraficoMes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelGraficoSemana, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 334, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(comboSemanaVentaSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelGraficoSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboVentasMesAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ComboMesVentaMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel29)
                                .addComponent(jLabel28)
                                .addComponent(jLabel27)))
                        .addGap(18, 18, 18)
                        .addComponent(panelGraficoMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ComboDiaGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboDiaGeneralActionPerformed
if (ComboDiaGeneral.getSelectedItem() == null) {
    return;
}
actualizarDashboardGeneral(); // TODO add your handling code here:
    }//GEN-LAST:event_ComboDiaGeneralActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
actualizarDashboard();  
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void ComboDiaHistorialVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboDiaHistorialVentaActionPerformed
 if (cargandoHistorialVentas) {
        return;
    }

    actualizarHistorialVentas();   // TODO add your handling code here:
    }//GEN-LAST:event_ComboDiaHistorialVentaActionPerformed

    private void comboAnioHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAnioHistorialVentasActionPerformed
if (cargandoHistorialVentas) {
        return;
    }

    actualizarHistorialVentas();       // TODO add your handling code here:
    }//GEN-LAST:event_comboAnioHistorialVentasActionPerformed

    private void comboMesHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMesHistorialVentasActionPerformed
if (cargandoHistorialVentas) {
        return;
    }

    actualizarHistorialVentas();               // TODO add your handling code here:
    }//GEN-LAST:event_comboMesHistorialVentasActionPerformed

    private void ComboMesGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboMesGeneralActionPerformed
actualizarDiasGeneral();
actualizarDashboardGeneral();        // TODO add your handling code here:
    }//GEN-LAST:event_ComboMesGeneralActionPerformed

    private void ComboAnioGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboAnioGeneralActionPerformed
actualizarDiasGeneral();
actualizarDashboardGeneral();        // TODO add your handling code here:
    }//GEN-LAST:event_ComboAnioGeneralActionPerformed

    private void ComboUsuarioHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboUsuarioHistorialVentasActionPerformed
if (cargandoHistorialVentas) {
        return;
    }

    actualizarHistorialVentas();        // TODO add your handling code here:
    }//GEN-LAST:event_ComboUsuarioHistorialVentasActionPerformed

    private void comboSemanaVentaSemanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSemanaVentaSemanaActionPerformed
if (cargandoSemanas) {
        return;
    }

    actualizarGraficoSemana();
    }//GEN-LAST:event_comboSemanaVentaSemanaActionPerformed

    private void ComboMesVentaMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboMesVentaMesActionPerformed
    if (cargandoVentasMes) {
    return;
}

actualizarGraficoMes();    // TODO add your handling code here:
    }//GEN-LAST:event_ComboMesVentaMesActionPerformed

    private void comboVentasMesAnioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboVentasMesAnioActionPerformed
if (cargandoVentasMes) {
    return;
}

actualizarGraficoMes();        // TODO add your handling code here:
    }//GEN-LAST:event_comboVentasMesAnioActionPerformed

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed
       verDetalleVentaSeleccionada(); // TODO add your handling code here:
    }//GEN-LAST:event_btnVerDetallesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
generarReporteGerencial();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboAnioGeneral;
    private javax.swing.JComboBox<String> ComboDiaGeneral;
    private javax.swing.JComboBox<String> ComboDiaHistorialVenta;
    private javax.swing.JComboBox<String> ComboMesGeneral;
    private javax.swing.JComboBox<String> ComboMesVentaMes;
    private javax.swing.JComboBox<String> ComboUsuarioHistorialVentas;
    private javax.swing.JLabel LabelGananciaBruta;
    private javax.swing.JLabel LabelTotalVendidoGeneral;
    private javax.swing.JLabel LabelVentasRealizadas;
    private javax.swing.JTable TableHistorialVentas;
    private javax.swing.JTable TableProductosMasVendidos;
    private javax.swing.JTable TableRendimientoVendedores;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JComboBox<String> comboAnioHistorialVentas;
    private javax.swing.JComboBox<String> comboMesHistorialVentas;
    private javax.swing.JComboBox<String> comboSemanaVentaSemana;
    private javax.swing.JComboBox<String> comboVentasMesAnio;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel panelGraficoMes;
    private javax.swing.JPanel panelGraficoSemana;
    // End of variables declaration//GEN-END:variables
}
