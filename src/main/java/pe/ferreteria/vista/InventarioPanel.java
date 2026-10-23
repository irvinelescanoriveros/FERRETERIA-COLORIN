package pe.ferreteria.vista;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.controlador.InventarioController;
import pe.ferreteria.controlador.MarcaController;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.controlador.ProveedorController;
import pe.ferreteria.controlador.KardexController;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Kardex;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Proveedor;

public class InventarioPanel extends javax.swing.JPanel {

    private static final int STOCK_POR_AGOTARSE = 2;
    private static final int DIAS_POR_VENCER = 90;
    private final InventarioController inventarioController
            = new InventarioController();

    private final ProductoController productoController
            = new ProductoController();

    private final CategoriaController categoriaController
            = new CategoriaController();

    private final MarcaController marcaController
            = new MarcaController();

    private final ProveedorController proveedorController
            = new ProveedorController();

    private List<Inventario> listaInventario
            = new ArrayList<>();

    private List<Producto> listaProductos
            = new ArrayList<>();

    private List<Categoria> listaCategorias
            = new ArrayList<>();

    private List<Marca> listaMarcas
            = new ArrayList<>();

    private List<Proveedor> listaProveedores
            = new ArrayList<>();
    private final KardexController kardexController
        = new KardexController();

    public InventarioPanel() {
        initComponents();

        cargarDatosInventario();

        configurarTablaInventario();
        configurarTablaAgotados();
        configurarTablaProductosPorVencer();

        cargarFiltrosInventario();

        actualizarTablasInventario();
    }

    private void cargarDatosInventario() {

        listaInventario = inventarioController.listar();
        listaProductos = productoController.listar();
        listaCategorias = categoriaController.listar();
        listaMarcas = marcaController.listar();
        listaProveedores = proveedorController.listar();
    }

    private void configurarTablaInventario() {

        javax.swing.table.DefaultTableModel modelo
                = new javax.swing.table.DefaultTableModel(
                        new Object[]{
                            "Lote",
                            "Producto",
                            "Categoría",
                            "Unidad",
                            "Marca",
                            "Proveedor",
                            "StockLote",
                            "StockProduct",
                            "P.Compra",
                            "P.Venta",
                            "F. Venc.",
                            "Ubicación",
                            "StockMalo",
                            "Descuento"
                        },
                        0
                ) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna) {

                return false;
            }
        };

        tableInventario.setModel(modelo);
    }

    private void cargarTablaInventario() {

        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tableInventario.getModel();

        modelo.setRowCount(0);

        for (Inventario inventario
                : listaInventario) {

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null) {
                continue;
            }

            String nombreCategoria = "";

            for (Categoria categoria : listaCategorias) {

                if (categoria.getIdCategoria()
                        == productoEncontrado.getIdCategoria()) {

                    nombreCategoria
                            = categoria.getNombre();

                    break;
                }
            }

            String nombreMarca = "";

            for (Marca marca : listaMarcas) {

                if (marca.getIdMarca()
                        == productoEncontrado.getIdMarca()) {

                    nombreMarca
                            = marca.getNombre();

                    break;
                }
            }

            String nombreProveedor = "";

            for (Proveedor proveedor : listaProveedores) {

                if (proveedor.getIdProveedor()
                        == inventario.getIdProveedor()) {

                    nombreProveedor
                            = proveedor.getEmpresa();

                    break;
                }
            }

            String fechaVencimiento
                    = inventario.getFechaVencimiento() == null
                    ? "Sin vencimiento"
                    : inventario.getFechaVencimiento()
                            .toString();

            String ubicacion = "";

            if (inventario.getEstante() != null
                    && !inventario.getEstante()
                            .trim()
                            .isEmpty()) {

                ubicacion
                        = inventario.getEstante();
            }

            if (inventario.getNivel() != null
                    && !inventario.getNivel()
                            .trim()
                            .isEmpty()) {

                if (!ubicacion.isEmpty()) {
                    ubicacion += "-";
                }

                ubicacion
                        += inventario.getNivel();
            }

            if (ubicacion.isEmpty()) {
                ubicacion = "Sin asignar";
            }

            int stockProducto
                    = obtenerStockTotalProducto(
                            inventario.getIdProducto()
                    );

            String textoDescuento
                    = obtenerTextoDescuento(
                            inventario
                    );

            modelo.addRow(new Object[]{
                inventario.getLote(),
                productoEncontrado.getNombre(),
                nombreCategoria,
                productoEncontrado.getUnidad(),
                nombreMarca,
                nombreProveedor,
                inventario.getStock(),
                stockProducto,
                inventario.getPrecioCompra(),
                inventario.getPrecioVenta(),
                fechaVencimiento,
                ubicacion,
                inventario.getStockDefectuoso(),
                textoDescuento
            });
        }
    }

    private void cargarFiltrosInventario() {

        combocategoria.removeAllItems();
        comboMarca.removeAllItems();

        combocategoria.addItem("Todas");
        comboMarca.addItem("Todas");

        for (Categoria categoria : listaCategorias) {
            combocategoria.addItem(
                    categoria.getNombre()
            );
        }

        for (Marca marca : listaMarcas) {
            comboMarca.addItem(
                    marca.getNombre()
            );
        }

        combocategoria.setSelectedIndex(0);
        comboMarca.setSelectedIndex(0);
    }

    private void filtrarTablaInventario() {

        String textoBusqueda
                = txtBuscarporNombreProducto.getText()
                        .trim()
                        .toLowerCase();

        String categoriaSeleccionada
                = combocategoria.getSelectedItem() == null
                ? "Todas"
                : combocategoria.getSelectedItem().toString();

        String marcaSeleccionada
                = comboMarca.getSelectedItem() == null
                ? "Todas"
                : comboMarca.getSelectedItem().toString();

        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tableInventario.getModel();

        modelo.setRowCount(0);

        for (Inventario inventario : listaInventario) {

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null) {
                continue;
            }

            String nombreProducto
                    = productoEncontrado.getNombre();

            String nombreCategoria = "";

            for (Categoria categoria : listaCategorias) {

                if (categoria.getIdCategoria()
                        == productoEncontrado.getIdCategoria()) {

                    nombreCategoria = categoria.getNombre();
                    break;
                }
            }

            String nombreMarca = "";

            for (Marca marca : listaMarcas) {

                if (marca.getIdMarca()
                        == productoEncontrado.getIdMarca()) {

                    nombreMarca = marca.getNombre();
                    break;
                }
            }

            boolean coincideNombre
                    = textoBusqueda.isEmpty()
                    || nombreProducto.toLowerCase()
                            .contains(textoBusqueda);

            boolean coincideCategoria
                    = categoriaSeleccionada.equals("Todas")
                    || nombreCategoria.equals(
                            categoriaSeleccionada
                    );

            boolean coincideMarca
                    = marcaSeleccionada.equals("Todas")
                    || nombreMarca.equals(
                            marcaSeleccionada
                    );

            if (!coincideNombre
                    || !coincideCategoria
                    || !coincideMarca) {

                continue;
            }

            String nombreProveedor = "";

            for (Proveedor proveedor : listaProveedores) {

                if (proveedor.getIdProveedor()
                        == inventario.getIdProveedor()) {

                    nombreProveedor
                            = proveedor.getEmpresa();
                    break;
                }
            }

            String fechaVencimiento
                    = inventario.getFechaVencimiento() == null
                    ? "Sin vencimiento"
                    : inventario.getFechaVencimiento()
                            .toString();

            int stockProducto
                    = obtenerStockTotalProducto(
                            inventario.getIdProducto()
                    );

            String ubicacion = "";

            if (inventario.getEstante() != null
                    && !inventario.getEstante()
                            .trim()
                            .isEmpty()) {

                ubicacion = inventario.getEstante();
            }

            if (inventario.getNivel() != null
                    && !inventario.getNivel()
                            .trim()
                            .isEmpty()) {

                if (!ubicacion.isEmpty()) {
                    ubicacion += "-";
                }

                ubicacion += inventario.getNivel();
            }

            if (ubicacion.isEmpty()) {
                ubicacion = "Sin asignar";
            }

            String textoDescuento
                    = obtenerTextoDescuento(inventario);

            modelo.addRow(new Object[]{
                inventario.getLote(),
                nombreProducto,
                nombreCategoria,
                productoEncontrado.getUnidad(),
                nombreMarca,
                nombreProveedor,
                inventario.getStock(),
                stockProducto,
                inventario.getPrecioCompra(),
                inventario.getPrecioVenta(),
                fechaVencimiento,
                ubicacion,
                inventario.getStockDefectuoso(),
                textoDescuento
            });
        }
    }

    private void configurarTablaAgotados() {

        javax.swing.table.DefaultTableModel modelo
                = new javax.swing.table.DefaultTableModel(
                        new Object[]{
                            "Lote",
                            "Producto",
                            "Proveedor",
                            "Stock Lote",
                            "Stock Producto",
                            "Estante",
                            "Nivel"
                        },
                        0
                ) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tableProductosAgotadosoPorAgotarse.setModel(modelo);
    }

    private void cargarTablaAgotados() {

        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tableProductosAgotadosoPorAgotarse.getModel();

        modelo.setRowCount(0);

        for (Inventario inventario : listaInventario) {

            int stockTotal
                    = obtenerStockTotalProducto(
                            inventario.getIdProducto()
                    );

            if (stockTotal > STOCK_POR_AGOTARSE) {
                continue;
            }

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null) {
                continue;
            }

            String nombreProveedor = "";

            for (Proveedor proveedor : listaProveedores) {

                if (proveedor.getIdProveedor()
                        == inventario.getIdProveedor()) {

                    nombreProveedor
                            = proveedor.getEmpresa();
                    break;
                }
            }

            modelo.addRow(new Object[]{
                inventario.getLote(),
                productoEncontrado.getNombre(),
                nombreProveedor,
                inventario.getStock(),
                stockTotal,
                inventario.getEstante(),
                inventario.getNivel()
            });
        }
    }

    private void configurarTablaProductosPorVencer() {

        javax.swing.table.DefaultTableModel modelo
                = new javax.swing.table.DefaultTableModel(
                        new Object[]{
                            "Lote",
                            "Producto",
                            "Proveedor",
                            "Stock",
                            "Fecha vencimiento",
                            "Estante",
                            "Nivel",
                            "Vence En"
                        },
                        0
                ) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tableProductosPorVencer.setModel(modelo);
    }

    private void cargarTablaProductosPorVencer() {

        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tableProductosPorVencer.getModel();

        modelo.setRowCount(0);

        java.time.LocalDate hoy
                = java.time.LocalDate.now();

        for (Inventario inventario : listaInventario) {

            if (inventario.getFechaVencimiento() == null) {
                continue;
            }

            java.time.LocalDate fechaVencimiento
                    = inventario.getFechaVencimiento();

            long diasRestantes
                    = java.time.temporal.ChronoUnit.DAYS.between(
                            hoy,
                            fechaVencimiento
                    );

            if (diasRestantes > DIAS_POR_VENCER) {
                continue;
            }

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null) {
                continue;
            }

            String nombreProveedor = "";

            for (Proveedor proveedor : listaProveedores) {

                if (proveedor.getIdProveedor()
                        == inventario.getIdProveedor()) {

                    nombreProveedor
                            = proveedor.getEmpresa();
                    break;
                }
            }

            String venceEn;

            if (diasRestantes < 0) {
                venceEn = "Vencido";
            } else if (diasRestantes == 0) {
                venceEn = "Hoy";
            } else if (diasRestantes == 1) {
                venceEn = "1 día";
            } else {
                venceEn = diasRestantes + " días";
            }

            modelo.addRow(new Object[]{
                inventario.getLote(),
                productoEncontrado.getNombre(),
                nombreProveedor,
                inventario.getStock(),
                fechaVencimiento.toString(),
                inventario.getEstante(),
                inventario.getNivel(),
                venceEn
            });
        }
    }

    private int obtenerStockTotalProducto(int idProducto) {

        int stockTotal = 0;

        for (Inventario inventario : listaInventario) {

            if (inventario.getIdProducto() == idProducto) {
                stockTotal += inventario.getStock();
            }
        }

        return stockTotal;
    }

    private void actualizarTablasInventario() {

        cargarTablaInventario();
        cargarTablaAgotados();
        cargarTablaProductosPorVencer();
    }

 private void cambiarUbicacion() {

    int fila = tableInventario.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un lote de la tabla."
        );

        return;
    }

    String lote =
            tableInventario
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableInventario
                    .getValueAt(fila, 1)
                    .toString();

    /*
     * Buscar el registro real de Inventario.
     */
    Inventario inventarioSeleccionado = null;

    for (Inventario inventario
            : listaInventario) {

        Producto productoEncontrado = null;

        for (Producto producto
                : listaProductos) {

            if (producto.getIdProducto()
                    == inventario.getIdProducto()) {

                productoEncontrado = producto;
                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        if (inventario.getLote().equals(lote)
                && productoEncontrado.getNombre()
                        .equals(nombreProducto)) {

            inventarioSeleccionado =
                    inventario;

            break;
        }
    }

    if (inventarioSeleccionado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el lote seleccionado."
        );

        return;
    }

    /*
     * Obtener ubicación directamente del objeto Inventario.
     */
    String estanteActual =
            inventarioSeleccionado.getEstante() == null
                    ? ""
                    : inventarioSeleccionado
                            .getEstante()
                            .trim();

    String nivelActual =
            inventarioSeleccionado.getNivel() == null
                    ? ""
                    : inventarioSeleccionado
                            .getNivel()
                            .trim();

    javax.swing.JTextField txtNuevoEstante =
            new javax.swing.JTextField(
                    estanteActual,
                    10
            );

    javax.swing.JTextField txtNuevoNivel =
            new javax.swing.JTextField(
                    nivelActual,
                    10
            );

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.GridLayout(
                            0,
                            2,
                            5,
                            5
                    )
            );

    panel.add(
            new javax.swing.JLabel(
                    "Producto:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    nombreProducto
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Lote:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    lote
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Estante actual:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    estanteActual.isEmpty()
                            ? "Sin asignar"
                            : estanteActual
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Nivel actual:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    nivelActual.isEmpty()
                            ? "Sin asignar"
                            : nivelActual
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Nuevo estante:"
            )
    );

    panel.add(
            txtNuevoEstante
    );

    panel.add(
            new javax.swing.JLabel(
                    "Nuevo nivel:"
            )
    );

    panel.add(
            txtNuevoNivel
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Cambiar Ubicación",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    String nuevoEstante =
            txtNuevoEstante
                    .getText()
                    .trim();

    String nuevoNivel =
            txtNuevoNivel
                    .getText()
                    .trim();

    if (nuevoEstante.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese el nuevo estante."
        );

        txtNuevoEstante.requestFocus();

        return;
    }

    if (nuevoNivel.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese el nuevo nivel."
        );

        txtNuevoNivel.requestFocus();

        return;
    }

    if (!nuevoEstante.matches(
            "[A-Za-z]+")) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El estante debe contener solo letras."
        );

        txtNuevoEstante.requestFocus();

        return;
    }

    if (!nuevoNivel.matches(
            "\\d+")) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El nivel debe contener solo números."
        );

        txtNuevoNivel.requestFocus();

        return;
    }

    boolean actualizado =
            inventarioController.actualizarUbicacion(
                    inventarioSeleccionado
                            .getIdInventario(),
                    nuevoEstante,
                    nuevoNivel
            );

    if (!actualizado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar la ubicación."
        );

        return;
    }

    /*
     * Actualizar también la información en memoria.
     */
    inventarioSeleccionado.setEstante(
            nuevoEstante
    );

    inventarioSeleccionado.setNivel(
            nuevoNivel
    );

    cargarTablaInventario();
    cargarTablaAgotados();
    cargarTablaProductosPorVencer();

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Ubicación actualizada correctamente."
    );
}

private void ajustarStock() {

    if (pe.ferreteria.infraestructura.SesionUsuario
            .getUsuarioActual() == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Debe iniciar sesión para ajustar el stock."
        );

        return;
    }

    int fila =
            tableInventario.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un lote de la tabla."
        );

        return;
    }

    String lote =
            tableInventario
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableInventario
                    .getValueAt(fila, 1)
                    .toString();

    int stockActual;

    try {

        stockActual =
                Integer.parseInt(
                        tableInventario
                                .getValueAt(fila, 6)
                                .toString()
                );

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo obtener el stock actual."
        );

        return;
    }

    if (stockActual <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este lote ya tiene stock 0."
        );

        return;
    }

    /*
     * Motivos permitidos para ajuste general.
     */
    javax.swing.JComboBox<String> cboMotivo =
        new javax.swing.JComboBox<>(
                new String[]{
                    
                    "Correccion",
                    "Dar de baja"
                }
        );

    /*
     * Cantidad que se desea retirar del stock.
     */
    javax.swing.JTextField txtCantidad =
            new javax.swing.JTextField(10);

    /*
     * Observación del ajuste.
     */
    javax.swing.JTextField txtObservacion =
            new javax.swing.JTextField(22);

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.GridBagLayout()
            );

    java.awt.GridBagConstraints gbc =
            new java.awt.GridBagConstraints();

    gbc.insets =
            new java.awt.Insets(
                    3, 5, 3, 5
            );

    gbc.anchor =
            java.awt.GridBagConstraints.WEST;

    gbc.fill =
            java.awt.GridBagConstraints.HORIZONTAL;

    /*
     * Producto
     */
    gbc.gridx = 0;
    gbc.gridy = 0;

    panel.add(
            new javax.swing.JLabel(
                    "Producto:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            new javax.swing.JLabel(
                    nombreProducto
            ),
            gbc
    );

    /*
     * Lote
     */
    gbc.gridx = 0;
    gbc.gridy = 1;

    panel.add(
            new javax.swing.JLabel(
                    "Lote:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            new javax.swing.JLabel(
                    lote
            ),
            gbc
    );

    /*
     * Stock actual
     */
    gbc.gridx = 0;
    gbc.gridy = 2;

    panel.add(
            new javax.swing.JLabel(
                    "Stock actual:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            new javax.swing.JLabel(
                    String.valueOf(
                            stockActual
                    )
            ),
            gbc
    );

    /*
     * Cantidad a retirar
     */
    gbc.gridx = 0;
    gbc.gridy = 3;

    panel.add(
            new javax.swing.JLabel(
                    "Cantidad a retirar:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            txtCantidad,
            gbc
    );

    /*
     * Motivo
     */
    gbc.gridx = 0;
    gbc.gridy = 4;

    panel.add(
            new javax.swing.JLabel(
                    "Motivo:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            cboMotivo,
            gbc
    );

    /*
     * Observación
     */
    gbc.gridx = 0;
    gbc.gridy = 5;

    panel.add(
            new javax.swing.JLabel(
                    "Observación:"
            ),
            gbc
    );

    gbc.gridx = 1;

    panel.add(
            txtObservacion,
            gbc
    );

    /*
     * Información
     */
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 2;

    panel.add(
            new javax.swing.JLabel(
                    "La cantidad indicada será retirada del stock."
            ),
            gbc
    );

    Object[] opciones = {
        "Continuar",
        "Cancelar"
    };

    int respuesta =
            javax.swing.JOptionPane.showOptionDialog(
                    this,
                    panel,
                    "Ajustar Stock",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE,
                    null,
                    opciones,
                    opciones[1]
            );

    if (respuesta != 0) {
        return;
    }

    /*
     * Obtener cantidad.
     */
    int cantidadRetirar;

    try {

        cantidadRetirar =
                Integer.parseInt(
                        txtCantidad
                                .getText()
                                .trim()
                );

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una cantidad válida."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidadRetirar <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad a retirar debe ser mayor que 0."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidadRetirar > stockActual) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad a retirar no puede superar "
                + "el stock actual."
        );

        txtCantidad.requestFocus();

        return;
    }

    String motivo =
            cboMotivo
                    .getSelectedItem()
                    .toString()
                    .trim();

    String observacion =
            txtObservacion
                    .getText()
                    .trim();

    if (motivo.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un motivo."
        );

        cboMotivo.requestFocus();

        return;
    }

    if (observacion.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una observación."
        );

        txtObservacion.requestFocus();

        return;
    }

    /*
     * Calcular el nuevo stock.
     */
    int nuevoStock =
            stockActual - cantidadRetirar;

    /*
     * Identificar el objeto Inventario seleccionado.
     */
    Inventario inventarioSeleccionado =
            null;

    for (Inventario inventario
            : listaInventario) {

        Producto productoEncontrado =
                null;

        for (Producto producto
                : listaProductos) {

            if (producto.getIdProducto()
                    == inventario.getIdProducto()) {

                productoEncontrado =
                        producto;

                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        if (inventario.getLote()
                .equals(lote)
                && productoEncontrado
                        .getNombre()
                        .equals(nombreProducto)) {

            inventarioSeleccionado =
                    inventario;

            break;
        }
    }

    if (inventarioSeleccionado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el lote seleccionado."
        );

        return;
    }

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario
                    .getIdUsuario();

    if (idUsuario <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );

        return;
    }

    /*
     * Confirmación final.
     */
    int respuestaConfirmacion =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de realizar el ajuste?\n\n"
                    + "Producto: "
                    + nombreProducto
                    + "\n"
                    + "Lote: "
                    + lote
                    + "\n"
                    + "Stock actual: "
                    + stockActual
                    + "\n"
                    + "Cantidad a retirar: "
                    + cantidadRetirar
                    + "\n"
                    + "Nuevo stock: "
                    + nuevoStock
                    + "\n"
                    + "Motivo: "
                    + motivo
                    + "\n"
                    + "Observación: "
                    + observacion,
                    "Confirmar Ajuste",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

    if (respuestaConfirmacion
            != javax.swing.JOptionPane.YES_OPTION) {

        return;
    }

    /*
     * Registrar el ajuste.
     */
    boolean actualizado =
            kardexController.ajustarStock(
                    inventarioSeleccionado
                            .getIdInventario(),
                    idUsuario,
                    nuevoStock,
                    motivo
            );

    if (!actualizado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo ajustar el stock "
                + "ni registrar el movimiento en Kardex."
        );

        return;
    }

    inventarioSeleccionado.setStock(
            nuevoStock
    );

    cargarTablaInventario();
    cargarTablaAgotados();
    cargarTablaProductosPorVencer();

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Stock ajustado correctamente.\n"
            + "Stock anterior: "
            + stockActual
            + "\n"
            + "Cantidad retirada: "
            + cantidadRetirar
            + "\n"
            + "Nuevo stock: "
            + nuevoStock
            + "\n"
            + "El movimiento también fue registrado en Kardex."
    );
}

    private String obtenerTextoDescuento(
        Inventario inventario) {

    if (!inventario.isDescuentoActivo()) {
        return "Desactivado";
    }

    if (inventario.getDescuentoPorcentaje() == null) {
        return "Activado";
    }

    return "Activado _"
            + inventario.getDescuentoPorcentaje()
                    .stripTrailingZeros()
                    .toPlainString()
            + "%";
}
    private void cambiarDescuento() {

    int fila = tableInventario.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un lote de la tabla."
        );

        return;
    }

    String lote =
            tableInventario
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableInventario
                    .getValueAt(fila, 1)
                    .toString();

    Inventario inventarioSeleccionado = null;

    for (Inventario inventario : listaInventario) {

        Producto productoEncontrado = null;

        for (Producto producto : listaProductos) {

            if (producto.getIdProducto()
                    == inventario.getIdProducto()) {

                productoEncontrado = producto;
                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        if (inventario.getLote().equals(lote)
                && productoEncontrado.getNombre()
                        .equals(nombreProducto)) {

            inventarioSeleccionado = inventario;
            break;
        }
    }

    if (inventarioSeleccionado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el lote seleccionado."
        );

        return;
    }

    /*
     * Si ya tiene descuento activo,
     * el botón lo desactiva.
     */
    if (inventarioSeleccionado.isDescuentoActivo()) {

        int respuesta =
                javax.swing.JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea desactivar el descuento?\n\n"
                        + "Producto: "
                        + nombreProducto
                        + "\n"
                        + "Lote: "
                        + lote
                        + "\n"
                        + "Descuento actual: "
                        + inventarioSeleccionado
                                .getDescuentoPorcentaje()
                        + "%",
                        "Desactivar Descuento",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta
                != javax.swing.JOptionPane.YES_OPTION) {

            return;
        }

        boolean actualizado =
                inventarioController.desactivarDescuento(
                        inventarioSeleccionado
                                .getIdInventario()
                );

        if (!actualizado) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "No se pudo desactivar el descuento."
            );

            return;
        }

        inventarioSeleccionado.setDescuentoActivo(
                false
        );

        inventarioSeleccionado.setDescuentoPorcentaje(
                java.math.BigDecimal.ZERO
        );

        cargarTablaInventario();

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Descuento desactivado correctamente."
        );

        return;
    }

    /*
     * Si no tiene descuento activo,
     * el botón lo activa.
     */
    javax.swing.JTextField txtPorcentaje =
            new javax.swing.JTextField(10);

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.GridLayout(
                            0,
                            2,
                            5,
                            5
                    )
            );

    panel.add(
            new javax.swing.JLabel("Producto:")
    );

    panel.add(
            new javax.swing.JLabel(nombreProducto)
    );

    panel.add(
            new javax.swing.JLabel("Lote:")
    );

    panel.add(
            new javax.swing.JLabel(lote)
    );

    panel.add(
            new javax.swing.JLabel("Precio de venta:")
    );

    panel.add(
            new javax.swing.JLabel(
                    "S/ "
                    + inventarioSeleccionado
                            .getPrecioVenta()
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Descuento (%):"
            )
    );

    panel.add(
            txtPorcentaje
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Activar Descuento",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    String texto =
            txtPorcentaje
                    .getText()
                    .trim();

    if (texto.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese el porcentaje de descuento."
        );

        txtPorcentaje.requestFocus();

        return;
    }

    java.math.BigDecimal porcentaje;

    try {

        porcentaje =
                new java.math.BigDecimal(texto);

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese un porcentaje válido."
        );

        txtPorcentaje.requestFocus();

        return;
    }

    if (porcentaje.compareTo(
            java.math.BigDecimal.ZERO) <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El descuento debe ser mayor que 0%."
        );

        txtPorcentaje.requestFocus();

        return;
    }

    if (porcentaje.compareTo(
            new java.math.BigDecimal("100")) > 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El descuento no puede superar el 100%."
        );

        txtPorcentaje.requestFocus();

        return;
    }

    porcentaje =
            porcentaje.setScale(
                    2,
                    java.math.RoundingMode.HALF_UP
            );

    boolean actualizado =
            inventarioController.activarDescuento(
                    inventarioSeleccionado
                            .getIdInventario(),
                    porcentaje
            );

    if (!actualizado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo activar el descuento."
        );

        return;
    }

    inventarioSeleccionado.setDescuentoActivo(
            true
    );

    inventarioSeleccionado.setDescuentoPorcentaje(
            porcentaje
    );

    cargarTablaInventario();

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Descuento activado correctamente.\n"
            + "Porcentaje: "
            + porcentaje
            + "%"
    );
}
   private void retirarStockDefectuoso() {

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario
                    .getIdUsuario();

    if (idUsuario <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );

        return;
    }

    int fila =
            tableInventario.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un lote de la tabla."
        );

        return;
    }

    String lote =
            tableInventario
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableInventario
                    .getValueAt(fila, 1)
                    .toString();

    /*
     * Buscar el registro real de Inventario.
     */
    Inventario inventarioSeleccionado =
            null;

    for (Inventario inventario
            : listaInventario) {

        Producto productoEncontrado =
                null;

        for (Producto producto
                : listaProductos) {

            if (producto.getIdProducto()
                    == inventario.getIdProducto()) {

                productoEncontrado =
                        producto;

                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        if (inventario.getLote()
                .equals(lote)
                && productoEncontrado
                        .getNombre()
                        .equals(nombreProducto)) {

            inventarioSeleccionado =
                    inventario;

            break;
        }
    }

    if (inventarioSeleccionado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el lote seleccionado."
        );

        return;
    }

    int stockDefectuosoActual =
            inventarioSeleccionado
                    .getStockDefectuoso();

    if (stockDefectuosoActual <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este lote no tiene stock defectuoso para retirar."
        );

        return;
    }

    /*
     * Obtener los movimientos originales que enviaron
     * unidades a stock defectuoso.
     */
    java.util.List<Kardex> ingresosDefectuosos =
            kardexController
                    .listarIngresosDefectuososPorInventario(
                            inventarioSeleccionado
                                    .getIdInventario()
                    );

    /*
     * Obtener todos los movimientos del lote para
     * determinar cuánto ya fue retirado de cada
     * ingreso defectuoso.
     */
    java.util.List<Kardex> movimientosLote =
            kardexController.listarPorInventario(
                    inventarioSeleccionado
                            .getIdInventario()
            );

    java.util.List<Kardex> defectuososDisponibles =
            new java.util.ArrayList<>();

    java.util.List<Integer> cantidadesDisponibles =
            new java.util.ArrayList<>();

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "ID Kardex",
                        "Fecha",
                        "Cantidad",
                        "Disponible",
                        "Comprobante",
                        "Motivo"
                    },
                    0
            ) {

                @Override
                public boolean isCellEditable(
                        int fila,
                        int columna) {

                    return false;
                }
            };

    java.time.format.DateTimeFormatter formatoFecha =
            java.time.format.DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    for (Kardex ingreso
            : ingresosDefectuosos) {

        int cantidadOriginal =
                ingreso.getStockDefectuosoDespues()
                - ingreso.getStockDefectuosoAntes();

        if (cantidadOriginal <= 0) {
            continue;
        }

        int cantidadRetirada = 0;

        for (Kardex movimiento
                : movimientosLote) {

            if ("Ajuste".equalsIgnoreCase(
                    movimiento.getMovimiento())
                    && "Ajuste".equalsIgnoreCase(
                            movimiento.getTipoReferencia())
                    && movimiento.getIdReferencia()
                            == ingreso.getIdKardex()
                    && movimiento.getStockDefectuosoDespues()
                            < movimiento.getStockDefectuosoAntes()) {

                cantidadRetirada +=
                        movimiento.getStockDefectuosoAntes()
                        - movimiento.getStockDefectuosoDespues();
            }
        }

        int disponible =
                cantidadOriginal
                - cantidadRetirada;

        if (disponible <= 0) {
            continue;
        }

        defectuososDisponibles.add(
                ingreso
        );

        cantidadesDisponibles.add(
                disponible
        );

        String fecha =
                ingreso.getFechaMovimiento() == null
                ? ""
                : ingreso.getFechaMovimiento()
                        .format(formatoFecha);

        String comprobante =
                ingreso.getNroComprobante() == null
                ? ""
                : ingreso.getNroComprobante();

        String motivo =
                ingreso.getMotivo() == null
                ? ""
                : ingreso.getMotivo();

        modelo.addRow(
                new Object[]{
                    ingreso.getIdKardex(),
                    fecha,
                    cantidadOriginal,
                    disponible,
                    comprobante,
                    motivo
                }
        );
    }

    if (defectuososDisponibles.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No existen productos defectuosos disponibles "
                + "para retirar en este lote."
        );

        return;
    }

    javax.swing.JTable tablaDefectuosos =
            new javax.swing.JTable(
                    modelo
            );

    tablaDefectuosos.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
    );

    javax.swing.JTextField txtCantidad =
            new javax.swing.JTextField(
                    8
            );

    txtCantidad.setText(
            "1"
    );

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout(
                            5,
                            5
                    )
            );

    panel.add(
            new javax.swing.JScrollPane(
                    tablaDefectuosos
            ),
            java.awt.BorderLayout.CENTER
    );

    javax.swing.JPanel panelCantidad =
            new javax.swing.JPanel(
                    new java.awt.FlowLayout(
                            java.awt.FlowLayout.LEFT
                    )
            );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Cantidad a retirar:"
            )
    );

    panelCantidad.add(
            txtCantidad
    );

    panel.add(
            panelCantidad,
            java.awt.BorderLayout.SOUTH
    );

    javax.swing.JLabel informacion =
            new javax.swing.JLabel(
                    "<html>"
                    + "<b>Producto:</b> "
                    + nombreProducto
                    + " &nbsp;&nbsp; "
                    + "<b>Lote:</b> "
                    + lote
                    + "<br>"
                    + "Seleccione el ID Kardex de la bolsa "
                    + "defectuosa que desea retirar."
                    + "</html>"
            );

    panel.add(
            informacion,
            java.awt.BorderLayout.NORTH
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Retirar S. Defectuoso",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    int filaDefectuoso =
            tablaDefectuosos.getSelectedRow();

    if (filaDefectuoso == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione la bolsa defectuosa que desea retirar."
        );

        return;
    }

    int filaModelo =
            tablaDefectuosos.convertRowIndexToModel(
                    filaDefectuoso
            );

    Kardex ingresoSeleccionado =
            defectuososDisponibles.get(
                    filaModelo
            );

    int cantidadDisponible =
            cantidadesDisponibles.get(
                    filaModelo
            );

    String textoCantidad =
            txtCantidad.getText()
                    .trim();

    if (textoCantidad.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese la cantidad a retirar."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (!textoCantidad.matches(
            "\\d+")) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe contener solo números."
        );

        txtCantidad.requestFocus();

        return;
    }

    int cantidad;

    try {

        cantidad =
                Integer.parseInt(
                        textoCantidad
                );

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad ingresada no es válida."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidad <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser mayor que cero."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidad > cantidadDisponible) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad no puede superar "
                + "la cantidad disponible de esta bolsa: "
                + cantidadDisponible
        );

        txtCantidad.requestFocus();

        return;
    }

    int nuevoStockDefectuoso =
            stockDefectuosoActual
            - cantidad;

    int confirmacion =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de retirar?\n\n"
                    + "Producto: "
                    + nombreProducto
                    + "\n"
                    + "Lote: "
                    + lote
                    + "\n"
                    + "ID Kardex: "
                    + ingresoSeleccionado
                            .getIdKardex()
                    + "\n"
                    + "Cantidad: "
                    + cantidad
                    + "\n"
                    + "Disponible en esta bolsa: "
                    + cantidadDisponible
                    + "\n"
                    + "Stock defectuoso total: "
                    + stockDefectuosoActual
                    + " → "
                    + nuevoStockDefectuoso,
                    "Confirmar retiro",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

    if (confirmacion
            != javax.swing.JOptionPane.YES_OPTION) {

        return;
    }

    /*
     * Inventario + Kardex.
     *
     * El ID Kardex seleccionado identifica
     * exactamente la bolsa que se retira.
     */
    boolean actualizado =
            kardexController.retirarStockDefectuoso(
                    inventarioSeleccionado
                            .getIdInventario(),
                    idUsuario,
                    ingresoSeleccionado
                            .getIdKardex(),
                    cantidad
            );

    if (!actualizado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo retirar el stock defectuoso "
                + "ni registrar el movimiento en Kardex."
        );

        return;
    }

    /*
     * Actualizar memoria.
     */
    inventarioSeleccionado.setStockDefectuoso(
            nuevoStockDefectuoso
    );

    cargarTablaInventario();
    cargarTablaAgotados();
    cargarTablaProductosPorVencer();

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Stock defectuoso retirado correctamente."
            + "\nID Kardex retirado: "
            + ingresoSeleccionado.getIdKardex()
            + "\nCantidad retirada: "
            + cantidad
            + "\nStock defectuoso restante: "
            + nuevoStockDefectuoso
    );
}
 private void reingresarProductoDefectuoso() {

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario
                    .getIdUsuario();

    if (idUsuario <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );

        return;
    }

    int fila =
            tableInventario.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un lote de la tabla."
        );

        return;
    }

    String lote =
            tableInventario
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableInventario
                    .getValueAt(fila, 1)
                    .toString();

    /*
     * Buscar el registro real de Inventario.
     */
    Inventario inventarioSeleccionado =
            null;

    for (Inventario inventario
            : listaInventario) {

        Producto productoEncontrado =
                null;

        for (Producto producto
                : listaProductos) {

            if (producto.getIdProducto()
                    == inventario.getIdProducto()) {

                productoEncontrado =
                        producto;

                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        if (inventario.getLote()
                .equals(lote)
                && productoEncontrado
                        .getNombre()
                        .equals(nombreProducto)) {

            inventarioSeleccionado =
                    inventario;

            break;
        }
    }

    if (inventarioSeleccionado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el lote seleccionado."
        );

        return;
    }

    /*
     * Validar que el lote tenga stock defectuoso.
     */
    int stockDefectuosoActual =
            inventarioSeleccionado
                    .getStockDefectuoso();

    if (stockDefectuosoActual <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este lote no tiene stock defectuoso "
                + "para reingresar."
        );

        return;
    }

    /*
     * Obtener los movimientos originales
     * que enviaron unidades a defectuoso.
     */
    java.util.List<Kardex> ingresosDefectuosos =
            kardexController
                    .listarIngresosDefectuososPorInventario(
                            inventarioSeleccionado
                                    .getIdInventario()
                    );

    /*
     * Obtener todos los movimientos del lote.
     */
    java.util.List<Kardex> movimientosLote =
            kardexController.listarPorInventario(
                    inventarioSeleccionado
                            .getIdInventario()
            );

    java.util.List<Kardex> defectuososDisponibles =
            new java.util.ArrayList<>();

    java.util.List<Integer> cantidadesDisponibles =
            new java.util.ArrayList<>();

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "ID Kardex",
                        "Fecha",
                        "Cantidad",
                        "Disponible",
                        "Comprobante",
                        "Motivo"
                    },
                    0
            ) {

                @Override
                public boolean isCellEditable(
                        int fila,
                        int columna) {

                    return false;
                }
            };

    java.time.format.DateTimeFormatter formatoFecha =
            java.time.format.DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    for (Kardex ingreso
            : ingresosDefectuosos) {

        int cantidadOriginal =
                ingreso.getStockDefectuosoDespues()
                - ingreso.getStockDefectuosoAntes();

        if (cantidadOriginal <= 0) {
            continue;
        }

        int cantidadRetirada = 0;
        int cantidadReingresada = 0;

        /*
         * Calcular retiros de esta bolsa.
         */
        for (Kardex movimiento
                : movimientosLote) {

            if ("Ajuste".equalsIgnoreCase(
                    movimiento.getMovimiento())
                    && "Ajuste".equalsIgnoreCase(
                            movimiento.getTipoReferencia())
                    && movimiento.getIdReferencia()
                            != null
                    && movimiento.getIdReferencia()
                            == ingreso.getIdKardex()
                    && movimiento
                            .getStockDefectuosoDespues()
                            < movimiento
                                    .getStockDefectuosoAntes()) {

                cantidadRetirada +=
                        movimiento
                                .getStockDefectuosoAntes()
                        - movimiento
                                .getStockDefectuosoDespues();
            }
        }

        /*
         * Calcular reingresos de esta bolsa.
         */
        for (Kardex movimiento
                : movimientosLote) {

            if ("Entrada".equalsIgnoreCase(
                    movimiento.getMovimiento())
                    && "Ajuste".equalsIgnoreCase(
                            movimiento.getTipoReferencia())
                    && movimiento.getIdReferencia()
                            != null
                    && movimiento.getIdReferencia()
                            == ingreso.getIdKardex()
                    && movimiento
                            .getStockDefectuosoDespues()
                            < movimiento
                                    .getStockDefectuosoAntes()) {

                cantidadReingresada +=
                        movimiento.getCantidad();
            }
        }

        int disponible =
                cantidadOriginal
                - cantidadRetirada
                - cantidadReingresada;

        if (disponible <= 0) {
            continue;
        }

        defectuososDisponibles.add(
                ingreso
        );

        cantidadesDisponibles.add(
                disponible
        );

        String fecha =
                ingreso.getFechaMovimiento() == null
                ? ""
                : ingreso.getFechaMovimiento()
                        .format(formatoFecha);

        String comprobante =
                ingreso.getNroComprobante() == null
                ? ""
                : ingreso.getNroComprobante();

        String motivo =
                ingreso.getMotivo() == null
                ? ""
                : ingreso.getMotivo();

        modelo.addRow(
                new Object[]{
                    ingreso.getIdKardex(),
                    fecha,
                    cantidadOriginal,
                    disponible,
                    comprobante,
                    motivo
                }
        );
    }

    if (defectuososDisponibles.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No existen productos defectuosos "
                + "disponibles para reingresar "
                + "en este lote."
        );

        return;
    }

    /*
     * Tabla de bolsas defectuosas.
     */
    javax.swing.JTable tablaDefectuosos =
            new javax.swing.JTable(
                    modelo
            );

    tablaDefectuosos.setSelectionMode(
            javax.swing.ListSelectionModel
                    .SINGLE_SELECTION
    );

    /*
     * Cantidad.
     */
    javax.swing.JTextField txtCantidad =
            new javax.swing.JTextField(
                    8
            );

    txtCantidad.setText(
            "1"
    );

    /*
     * Motivo del movimiento.
     */
    javax.swing.JComboBox<String> cmbMotivo =
            new javax.swing.JComboBox<>(
                    new String[]{
                        "Reingreso",
                        "Garantia",
                        "Otros"
                    }
            );

    /*
     * Observación.
     */
    javax.swing.JTextField txtObservacion =
            new javax.swing.JTextField(
                    20
            );

    javax.swing.JPanel panelDatos =
            new javax.swing.JPanel(
                    new java.awt.GridLayout(
                            3,
                            2,
                            5,
                            5
                    )
            );

    panelDatos.add(
            new javax.swing.JLabel(
                    "Cantidad a reingresar:"
            )
    );

    panelDatos.add(
            txtCantidad
    );

    panelDatos.add(
            new javax.swing.JLabel(
                    "Motivo del movimiento:"
            )
    );

    panelDatos.add(
            cmbMotivo
    );

    panelDatos.add(
            new javax.swing.JLabel(
                    "Observación:"
            )
    );

    panelDatos.add(
            txtObservacion
    );

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout(
                            5,
                            5
                    )
            );

    javax.swing.JLabel informacion =
            new javax.swing.JLabel(
                    "<html>"
                    + "<b>Producto:</b> "
                    + nombreProducto
                    + " &nbsp;&nbsp; "
                    + "<b>Lote:</b> "
                    + lote
                    + "<br>"
                    + "Seleccione la bolsa defectuosa "
                    + "que desea reingresar."
                    + "</html>"
            );

    panel.add(
            informacion,
            java.awt.BorderLayout.NORTH
    );

    panel.add(
            new javax.swing.JScrollPane(
                    tablaDefectuosos
            ),
            java.awt.BorderLayout.CENTER
    );

    panel.add(
            panelDatos,
            java.awt.BorderLayout.SOUTH
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Reingresar Mercadería",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    /*
     * Validar bolsa seleccionada.
     */
    int filaDefectuoso =
            tablaDefectuosos.getSelectedRow();

    if (filaDefectuoso == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione la bolsa defectuosa "
                + "que desea reingresar."
        );

        return;
    }

    int filaModelo =
            tablaDefectuosos
                    .convertRowIndexToModel(
                            filaDefectuoso
                    );

    Kardex ingresoSeleccionado =
            defectuososDisponibles.get(
                    filaModelo
            );

    int cantidadDisponible =
            cantidadesDisponibles.get(
                    filaModelo
            );

    /*
     * Validar cantidad.
     */
    String textoCantidad =
            txtCantidad.getText()
                    .trim();

    if (textoCantidad.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese la cantidad a reingresar."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (!textoCantidad.matches(
            "\\d+")) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe contener solo números."
        );

        txtCantidad.requestFocus();

        return;
    }

    int cantidad;

    try {

        cantidad =
                Integer.parseInt(
                        textoCantidad
                );

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad ingresada no es válida."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidad <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser mayor que cero."
        );

        txtCantidad.requestFocus();

        return;
    }

    if (cantidad > cantidadDisponible) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad no puede superar "
                + "la cantidad disponible de esta bolsa: "
                + cantidadDisponible
        );

        txtCantidad.requestFocus();

        return;
    }

    /*
     * Validar observación.
     */
    String observacion =
            txtObservacion.getText()
                    .trim();

    if (observacion.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una observación."
        );

        txtObservacion.requestFocus();

        return;
    }

    String motivoMovimiento =
            cmbMotivo
                    .getSelectedItem()
                    .toString();

    int nuevoStock =
            inventarioSeleccionado
                    .getStock()
            + cantidad;

    int nuevoStockDefectuoso =
            stockDefectuosoActual
            - cantidad;

    /*
     * Confirmación.
     */
    int confirmacion =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de realizar "
                    + "el reingreso?\n\n"
                    + "Producto: "
                    + nombreProducto
                    + "\n"
                    + "Lote: "
                    + lote
                    + "\n"
                    + "ID Kardex: "
                    + ingresoSeleccionado
                            .getIdKardex()
                    + "\n"
                    + "Cantidad: "
                    + cantidad
                    + "\n"
                    + "Disponible en esta bolsa: "
                    + cantidadDisponible
                    + "\n"
                    + "Motivo: "
                    + motivoMovimiento
                    + "\n"
                    + "Observación: "
                    + observacion
                    + "\n\n"
                    + "Stock normal: "
                    + inventarioSeleccionado
                            .getStock()
                    + " → "
                    + nuevoStock
                    + "\n"
                    + "Stock defectuoso: "
                    + stockDefectuosoActual
                    + " → "
                    + nuevoStockDefectuoso,
                    "Confirmar reingreso",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE
            );

    if (confirmacion
            != javax.swing.JOptionPane.YES_OPTION) {

        return;
    }

    /*
     * Ejecutar reingreso.
     */
    boolean actualizado =
            kardexController
                    .reingresarProductoDefectuoso(
                            inventarioSeleccionado
                                    .getIdInventario(),
                            idUsuario,
                            ingresoSeleccionado
                                    .getIdKardex(),
                            cantidad,
                            motivoMovimiento,
                            observacion
                    );

    if (!actualizado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo realizar "
                + "el reingreso."
        );

        return;
    }

    /*
     * Actualizar memoria.
     */
    inventarioSeleccionado.setStock(
            nuevoStock
    );

    inventarioSeleccionado.setStockDefectuoso(
            nuevoStockDefectuoso
    );

    cargarTablaInventario();
    cargarTablaAgotados();
    cargarTablaProductosPorVencer();

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Mercadería reingresada correctamente."
            + "\nID Kardex original: "
            + ingresoSeleccionado.getIdKardex()
            + "\nCantidad reingresada: "
            + cantidad
            + "\nStock normal: "
            + nuevoStock
            + "\nStock defectuoso restante: "
            + nuevoStockDefectuoso
    );
}  
   
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        combocategoria = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableInventario = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProductosAgotadosoPorAgotarse = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableProductosPorVencer = new javax.swing.JTable();
        txtBuscarporNombreProducto = new javax.swing.JTextField();
        btnCambiarUbcacion = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnAjustarStock = new javax.swing.JButton();
        comboMarca = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        btnAplicarDescuento = new javax.swing.JButton();
        btnRetirarStockDefectuoso = new javax.swing.JButton();
        btnReingresoStockDefectuoso = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(239, 248, 254));

        jLabel34.setText("Buscar Producto: ");

        combocategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combocategoriaActionPerformed(evt);
            }
        });

        jLabel35.setText("Categoria");

        tableInventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "lote", "Producto", "Categoria", "Unidad", "Marca", "Proveedor", "StockLote", "StockProducto", "P. Compra", "P. Venta", "F. de venc.", "Ubicacion", "S. Defectuoso", "Descuento"
            }
        ));
        jScrollPane4.setViewportView(tableInventario);

        jPanel3.setBackground(new java.awt.Color(255, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("PRODCUTOS AGOTADOS O POR AGOTARSE"));

        tableProductosAgotadosoPorAgotarse.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Lote", "Producto", "Proveedor", "Stock Lote", "Stock Producto", "Estante", "Nivel"
            }
        ));
        jScrollPane1.setViewportView(tableProductosAgotadosoPorAgotarse);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1163, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("PRODUCTOS POR VENCER"));

        tableProductosPorVencer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Lote", "Producto", "Proveedor", "Stock", "F. Venc", "Estante", "Nivel", "Vence en"
            }
        ));
        jScrollPane2.setViewportView(tableProductosPorVencer);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        txtBuscarporNombreProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarporNombreProductoActionPerformed(evt);
            }
        });
        txtBuscarporNombreProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarporNombreProductoKeyReleased(evt);
            }
        });

        btnCambiarUbcacion.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnCambiarUbcacion.setText("CAMBIAR UBICACION");
        btnCambiarUbcacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarUbcacionActionPerformed(evt);
            }
        });

        btnActualizar.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnActualizar.setText("ACTUALIZAR");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnAjustarStock.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnAjustarStock.setText("AJUSTAR STOCK");
        btnAjustarStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjustarStockActionPerformed(evt);
            }
        });

        comboMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMarcaActionPerformed(evt);
            }
        });

        jLabel36.setText("Marca");

        btnAplicarDescuento.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnAplicarDescuento.setText("GESTIONAR DESCUENTO");
        btnAplicarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAplicarDescuentoActionPerformed(evt);
            }
        });

        btnRetirarStockDefectuoso.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnRetirarStockDefectuoso.setText("RETIRO STOCK DEFECTUOSO");
        btnRetirarStockDefectuoso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetirarStockDefectuosoActionPerformed(evt);
            }
        });

        btnReingresoStockDefectuoso.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnReingresoStockDefectuoso.setText("REINGRESO STOCK DEFECTUOSO");
        btnReingresoStockDefectuoso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReingresoStockDefectuosoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(38, 38, 38)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnAjustarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnCambiarUbcacion, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnAplicarDescuento)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(btnRetirarStockDefectuoso)))
                                .addGap(0, 20, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReingresoStockDefectuoso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarporNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(158, 158, 158)
                        .addComponent(jLabel36)
                        .addGap(27, 27, 27)
                        .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combocategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(combocategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(txtBuscarporNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnCambiarUbcacion, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAplicarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(btnRetirarStockDefectuoso, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnReingresoStockDefectuoso, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAjustarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void combocategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combocategoriaActionPerformed
        filtrarTablaInventario();
    }//GEN-LAST:event_combocategoriaActionPerformed

    private void txtBuscarporNombreProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarporNombreProductoActionPerformed

    }//GEN-LAST:event_txtBuscarporNombreProductoActionPerformed

    private void txtBuscarporNombreProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarporNombreProductoKeyReleased
        filtrarTablaInventario();
    }//GEN-LAST:event_txtBuscarporNombreProductoKeyReleased

    private void btnCambiarUbcacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarUbcacionActionPerformed
        cambiarUbicacion();
    }//GEN-LAST:event_btnCambiarUbcacionActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarDatosInventario();
        cargarFiltrosInventario();
        actualizarTablasInventario();   // TODO add your handling code here:
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnAjustarStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjustarStockActionPerformed
        ajustarStock();        // TODO add your handling code here:
    }//GEN-LAST:event_btnAjustarStockActionPerformed

    private void comboMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMarcaActionPerformed
        filtrarTablaInventario();        // TODO add your handling code here:
    }//GEN-LAST:event_comboMarcaActionPerformed

    private void btnAplicarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarDescuentoActionPerformed
cambiarDescuento();        // TODO add your handling code here:
    }//GEN-LAST:event_btnAplicarDescuentoActionPerformed

    private void btnRetirarStockDefectuosoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetirarStockDefectuosoActionPerformed
     retirarStockDefectuoso();  // TODO add your handling code here:
    }//GEN-LAST:event_btnRetirarStockDefectuosoActionPerformed

    private void btnReingresoStockDefectuosoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReingresoStockDefectuosoActionPerformed
reingresarProductoDefectuoso();        // TODO add your handling code here:
    }//GEN-LAST:event_btnReingresoStockDefectuosoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAjustarStock;
    private javax.swing.JButton btnAplicarDescuento;
    private javax.swing.JButton btnCambiarUbcacion;
    private javax.swing.JButton btnReingresoStockDefectuoso;
    private javax.swing.JButton btnRetirarStockDefectuoso;
    private javax.swing.JComboBox<String> comboMarca;
    private javax.swing.JComboBox<String> combocategoria;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tableInventario;
    private javax.swing.JTable tableProductosAgotadosoPorAgotarse;
    private javax.swing.JTable tableProductosPorVencer;
    private javax.swing.JTextField txtBuscarporNombreProducto;
    // End of variables declaration//GEN-END:variables
}
