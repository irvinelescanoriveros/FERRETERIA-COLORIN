package pe.ferreteria.vista;

import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.controlador.VentaController;
import pe.ferreteria.controlador.KardexController;
import pe.ferreteria.controlador.InventarioController;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.modelo.DetalleVenta;
import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.controlador.MarcaController;
import pe.ferreteria.controlador.ProveedorController;

import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.modelo.Proveedor;
import pe.ferreteria.modelo.Kardex;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Usuario;

import pe.ferreteria.servicio.UsuarioService;
import pe.ferreteria.util.GeneradorEtiquetaDefectuoso;
import pe.ferreteria.util.GeneradorPDF;

public class KardexPanel extends javax.swing.JPanel {

    private final KardexController kardexController
            = new KardexController();

    private final InventarioController inventarioController
            = new InventarioController();

    private final ProductoController productoController
            = new ProductoController();

    private final UsuarioService usuarioService
            = new UsuarioService();
    private final CategoriaController categoriaController
        = new CategoriaController();

private final MarcaController marcaController
        = new MarcaController();

private final ProveedorController proveedorController
        = new ProveedorController();

    private List<Kardex> listaKardex
            = new ArrayList<>();

    private List<Inventario> listaInventario
            = new ArrayList<>();

    private List<Producto> listaProductos
            = new ArrayList<>();

    private List<Usuario> listaUsuarios
            = new ArrayList<>();
    private final VentaController ventaController
        = new VentaController();
    private List<Kardex> listaKardexMostrada
        = new ArrayList<>();
    private List<Categoria> listaCategorias
        = new ArrayList<>();

private List<Marca> listaMarcas
        = new ArrayList<>();

private List<Proveedor> listaProveedores
        = new ArrayList<>();
    

    public KardexPanel() {

    initComponents();

    cargarDatosKardex();

    configurarTablaKardex();
    cargarFiltrosKardex();
    cargarTablaKardex(listaKardex);
}

    private void cargarDatosKardex() {

        listaKardex
                = kardexController.listar();

        listaInventario
                = inventarioController.listar();

        listaProductos
                = productoController.listar();

        listaUsuarios = usuarioService.listarKardex();
        listaCategorias
        = categoriaController.listar();

listaMarcas
        = marcaController.listar();

listaProveedores
        = proveedorController.listar();
        
    }
    private void configurarTablaKardex() {

    javax.swing.table.DefaultTableModel modelo
            = new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "Fecha/Hora",
                        "Producto",
                        "Lote",
                        "Movimiento",
                        "Motivo",
                        "Cant.",
                        "Stock Antes",
                        "Stock Después",
                        "Stock defectuoso",
                        "Origen",
                        "Comprobante",
                        "Usuario"
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

    tableKardex.setModel(modelo);
}
    private void cargarTablaKardex(List<Kardex> lista) {

    javax.swing.table.DefaultTableModel modelo
            = (javax.swing.table.DefaultTableModel)
                    tableKardex.getModel();

    modelo.setRowCount(0);

    listaKardexMostrada =
            new ArrayList<>(lista);

    for (Kardex kardex : lista) {

        String nombreProducto = "";
        String lote = "";
        String username = "";

        for (Producto producto : listaProductos) {

            if (producto.getIdProducto()
                    == kardex.getIdProducto()) {

                nombreProducto =
                        producto.getNombre();

                break;
            }
        }

        for (Inventario inventario
                : listaInventario) {

            if (inventario.getIdInventario()
                    == kardex.getIdInventario()) {

                lote =
                        inventario.getLote();

                break;
            }
        }

        for (Usuario usuario
                : listaUsuarios) {

            if (usuario.getIdUsuario()
                    == kardex.getIdUsuario()) {

                username =
                        usuario.getUsername();

                break;
            }
        }

        modelo.addRow(new Object[]{

            kardex.getFechaMovimiento(),

            nombreProducto,

            lote,

            kardex.getMovimiento(),

            kardex.getMotivoMovimiento(),

            kardex.getCantidad(),

            kardex.getStockAntes(),

            kardex.getStockDespues(),

            kardex.getStockDefectuosoDespues(),

            kardex.getTipoReferencia(),

            kardex.getNroComprobante(),

            username
        });
    }
}
private void cargarFiltrosKardex() {

    comboMovimiento.removeAllItems();
comboMotivo.removeAllItems();
comboUsuario.removeAllItems();

    comboMovimiento.addItem("Todos");
    comboUsuario.addItem("Todos");

    comboMovimiento.addItem("Entrada");
    comboMovimiento.addItem("Salida");
    comboMovimiento.addItem("Ajuste");
    comboMovimiento.addItem("Reversion");
    
    comboMotivo.addItem("Todos");
comboMotivo.addItem("Compra");
comboMotivo.addItem("Venta");
comboMotivo.addItem("Producto Defectuoso");
comboMotivo.addItem("Reposicion p. client.");
comboMotivo.addItem("Reingreso");
comboMotivo.addItem("Garantia");
comboMotivo.addItem("Reversion");
comboMotivo.addItem("Retiro Defectuoso");
comboMotivo.addItem("Correccion");
comboMotivo.addItem("Dar de baja");
comboMotivo.addItem("Otros");

    for (Usuario usuario : listaUsuarios) {

        comboUsuario.addItem(
                usuario.getUsername()
        );
    }

    comboMovimiento.setSelectedIndex(0);
    comboUsuario.setSelectedIndex(0);
    comboMotivo.setSelectedIndex(0);
}   
private void filtrarTablaKardex() {

    String textoBusqueda =
            txtBuscarProducto
                    .getText()
                    .trim()
                    .toLowerCase();

    String movimientoSeleccionado =
            comboMovimiento.getSelectedItem() == null
                    ? "Todos"
                    : comboMovimiento
                            .getSelectedItem()
                            .toString();

    String motivoSeleccionado =
            comboMotivo.getSelectedItem() == null
                    ? "Todos"
                    : comboMotivo
                            .getSelectedItem()
                            .toString();

    String usuarioSeleccionado =
            comboUsuario.getSelectedItem() == null
                    ? "Todos"
                    : comboUsuario
                            .getSelectedItem()
                            .toString();

    List<Kardex> listaFiltrada =
            new ArrayList<>();

    for (Kardex kardex : listaKardex) {

        String nombreProducto = "";
        String lote = "";
        String username = "";
        String comprobante = "";

        /*
         * Buscar producto.
         */
        for (Producto producto
                : listaProductos) {

            if (producto.getIdProducto()
                    == kardex.getIdProducto()) {

                nombreProducto =
                        producto.getNombre();

                break;
            }
        }

        /*
         * Buscar lote.
         */
        for (Inventario inventario
                : listaInventario) {

            if (inventario.getIdInventario()
                    == kardex.getIdInventario()) {

                lote =
                        inventario.getLote();

                break;
            }
        }

        /*
         * Buscar usuario.
         */
        for (Usuario usuario
                : listaUsuarios) {

            if (usuario.getIdUsuario()
                    == kardex.getIdUsuario()) {

                username =
                        usuario.getUsername();

                break;
            }
        }

        /*
         * Número de comprobante.
         */
        if (kardex.getNroComprobante() != null) {

            comprobante =
                    kardex.getNroComprobante();
        }

        /*
         * Búsqueda por:
         * - Producto
         * - Lote
         * - Comprobante
         */
        boolean coincideBusqueda =
                textoBusqueda.isEmpty()
                || nombreProducto
                        .toLowerCase()
                        .contains(textoBusqueda)
                || lote
                        .toLowerCase()
                        .contains(textoBusqueda)
                || comprobante
                        .toLowerCase()
                        .contains(textoBusqueda);

        /*
         * Filtro por movimiento.
         */
        boolean coincideMovimiento =
                movimientoSeleccionado.equals("Todos")
                || kardex.getMovimiento()
                        .equalsIgnoreCase(
                                movimientoSeleccionado
                        );

        /*
         * Filtro por motivo.
         */
        boolean coincideMotivo =
                motivoSeleccionado.equals("Todos")
                || kardex.getMotivoMovimiento()
                        .equalsIgnoreCase(
                                motivoSeleccionado
                        );

        /*
         * Filtro por usuario.
         */
        boolean coincideUsuario =
                usuarioSeleccionado.equals("Todos")
                || username.equals(
                        usuarioSeleccionado
                );

        /*
         * Agregar si cumple todos los filtros.
         */
        if (coincideBusqueda
                && coincideMovimiento
                && coincideMotivo
                && coincideUsuario) {

            listaFiltrada.add(
                    kardex
            );
        }
    }

    cargarTablaKardex(
            listaFiltrada
    );
}
    private void actualizarKardex() {

    cargarDatosKardex();

    cargarFiltrosKardex();

    cargarTablaKardex(
            listaKardex
    );
}
    
  private void verDetalleKardex() {

    int fila = tableKardex.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un movimiento de la tabla."
        );

        return;
    }

    int filaModelo =
            tableKardex.convertRowIndexToModel(fila);

    if (filaModelo < 0
            || filaModelo >= listaKardexMostrada.size()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el movimiento seleccionado."
        );

        return;
    }

    int idKardex =
            listaKardexMostrada
                    .get(filaModelo)
                    .getIdKardex();

    Kardex kardex =
            kardexController.buscarPorId(idKardex);

    if (kardex == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo obtener el movimiento seleccionado."
        );

        return;
    }

    String nombreProducto = "";

    for (Producto producto : listaProductos) {

        if (producto.getIdProducto()
                == kardex.getIdProducto()) {

            nombreProducto =
                    producto.getNombre();

            break;
        }
    }

    String lote = "";

    for (Inventario inventario : listaInventario) {

        if (inventario.getIdInventario()
                == kardex.getIdInventario()) {

            lote =
                    inventario.getLote();

            break;
        }
    }

    String username = "";

    for (Usuario usuario : listaUsuarios) {

        if (usuario.getIdUsuario()
                == kardex.getIdUsuario()) {

            username =
                    usuario.getUsername();

            break;
        }
    }

    String textoDescuento = "Desactivado";

    if ("Venta".equalsIgnoreCase(
            kardex.getTipoReferencia())
            && kardex.getIdReferencia() > 0) {

        DetalleVenta detalleVenta =
                ventaController
                        .buscarDetallePorVentaEInventario(
                                kardex.getIdReferencia(),
                                kardex.getIdInventario()
                        );

        if (detalleVenta != null
                && detalleVenta.getDescuento() != null
                && detalleVenta.getDescuento()
                        .compareTo(
                                java.math.BigDecimal.ZERO
                        ) > 0) {

            java.math.BigDecimal subtotal =
                    detalleVenta.getPrecioUnitario()
                            .multiply(
                                    java.math.BigDecimal.valueOf(
                                            detalleVenta.getCantidad()
                                    )
                            );

            if (subtotal.compareTo(
                    java.math.BigDecimal.ZERO
            ) > 0) {

                java.math.BigDecimal porcentaje =
                        detalleVenta.getDescuento()
                                .multiply(
                                        new java.math.BigDecimal("100")
                                )
                                .divide(
                                        subtotal,
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                );

                textoDescuento =
                        "Activado _"
                        + porcentaje
                                .stripTrailingZeros()
                                .toPlainString()
                        + "%";
            }
        }
    }

    String fecha =
            kardex.getFechaMovimiento() == null
            ? ""
            : kardex.getFechaMovimiento()
                    .format(
                            java.time.format.DateTimeFormatter
                                    .ofPattern(
                                            "dd/MM/yyyy HH:mm"
                                    )
                    );

    javax.swing.JPanel panel =
            new javax.swing.JPanel(
                    new java.awt.GridBagLayout()
            );

    java.awt.GridBagConstraints gbc =
            new java.awt.GridBagConstraints();

    gbc.insets =
            new java.awt.Insets(
                    2, 4, 2, 4
            );

    gbc.anchor =
            java.awt.GridBagConstraints.WEST;

    gbc.fill =
            java.awt.GridBagConstraints.HORIZONTAL;

    gbc.weightx = 0;

    /*
     * ID KARDEX
     */
    agregarDetalle(
            panel,
            gbc,
            0,
            "ID Kardex:",
            String.valueOf(
                    kardex.getIdKardex()
            )
    );

    /*
     * FECHA / HORA
     */
    agregarDetalle(
            panel,
            gbc,
            1,
            "Fecha/Hora:",
            fecha
    );

    /*
     * PRODUCTO
     */
    agregarDetalle(
            panel,
            gbc,
            2,
            "Producto:",
            nombreProducto
    );

    /*
     * LOTE
     */
    agregarDetalle(
            panel,
            gbc,
            3,
            "Lote:",
            lote
    );

    /*
     * MOVIMIENTO
     */
    agregarDetalle(
            panel,
            gbc,
            4,
            "Movimiento:",
            safe(
                    kardex.getMovimiento()
            )
    );

    /*
     * CANTIDAD
     */
    agregarDetalle(
            panel,
            gbc,
            5,
            "Cantidad:",
            String.valueOf(
                    kardex.getCantidad()
            )
    );

    /*
     * STOCK
     */
    agregarDetalle(
            panel,
            gbc,
            6,
            "Stock:",
            kardex.getStockAntes()
            + " → "
            + kardex.getStockDespues()
    );

    /*
     * STOCK DEFECTUOSO
     */
    agregarDetalle(
            panel,
            gbc,
            7,
            "Stock Defectuoso:",
            kardex.getStockDefectuosoAntes()
            + " → "
            + kardex.getStockDefectuosoDespues()
    );

    /*
     * PRECIO UNITARIO
     */
    agregarDetalle(
            panel,
            gbc,
            8,
            "Precio unitario:",
            "S/ "
            + formatear(
                    kardex.getPrecioUnitario()
            )
    );

    /*
     * MONTO TOTAL
     */
    agregarDetalle(
            panel,
            gbc,
            9,
            "Monto total:",
            "S/ "
            + formatear(
                    kardex.getMontoTotal()
            )
    );

    /*
     * DESCUENTO
     */
    agregarDetalle(
            panel,
            gbc,
            10,
            "Descuento:",
            textoDescuento
    );

    /*
     * ORIGEN
     */
    agregarDetalle(
            panel,
            gbc,
            11,
            "Origen:",
            safe(
                    kardex.getTipoReferencia()
            )
    );

    /*
     * COMPROBANTE
     */
    agregarDetalle(
            panel,
            gbc,
            12,
            "Comprobante:",
            kardex.getNroComprobante() == null
                    ? "—"
                    : kardex.getNroComprobante()
    );

    /*
     * USUARIO
     */
    agregarDetalle(
            panel,
            gbc,
            13,
            "Usuario:",
            username
    );

    /*
     * MOTIVO / OBSERVACIÓN
     */
    gbc.gridx = 0;
    gbc.gridy = 14;
    gbc.weightx = 0;

    panel.add(
            new javax.swing.JLabel(
                    "Motivo / Obs.:"
            ),
            gbc
    );

    javax.swing.JTextArea txtMotivo =
            new javax.swing.JTextArea(
                    kardex.getMotivo() == null
                    ? ""
                    : kardex.getMotivo(),
                    3,
                    25
            );

    txtMotivo.setLineWrap(true);
    txtMotivo.setWrapStyleWord(true);
    txtMotivo.setEditable(false);
    txtMotivo.setOpaque(false);
    txtMotivo.setBorder(null);

    gbc.gridx = 1;
    gbc.weightx = 1;

    panel.add(
            txtMotivo,
            gbc
    );

    /*
     * BOTÓN ABRIR PDF
     */
    javax.swing.JButton btnAbrirPdf =
            new javax.swing.JButton(
                    "Abrir PDF"
            );

    btnAbrirPdf.addActionListener(e -> {

        abrirPdfDesdeDetalle(kardex);

    });

    gbc.gridx = 0;
    gbc.gridy = 15;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.anchor =
            java.awt.GridBagConstraints.CENTER;

    panel.add(
            btnAbrirPdf,
            gbc
    );

    javax.swing.JOptionPane.showMessageDialog(
            this,
            panel,
            "Detalle del movimiento Kardex",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
    );
}
    
    private void agregarDetalle(
        javax.swing.JPanel panel,
        java.awt.GridBagConstraints gbc,
        int fila,
        String etiqueta,
        String valor) {

    gbc.gridy = fila;
    gbc.gridx = 0;
    gbc.weightx = 0;

    javax.swing.JLabel lblEtiqueta =
            new javax.swing.JLabel(
                    etiqueta
            );

    lblEtiqueta.setFont(
            lblEtiqueta.getFont()
                    .deriveFont(
                            java.awt.Font.BOLD
                    )
    );

    panel.add(
            lblEtiqueta,
            gbc
    );

    gbc.gridx = 1;
    gbc.weightx = 1;

    panel.add(
            new javax.swing.JLabel(
                    safe(valor)
            ),
            gbc
    );
}
    private String safe(String texto) {

    return texto == null
            ? ""
            : texto;
}
    private String formatear(
        java.math.BigDecimal numero) {

    if (numero == null) {
        return "0.00";
    }

    return numero
            .setScale(
                    2,
                    java.math.RoundingMode.HALF_UP
            )
            .toPlainString();
}
    private void revertirVenta() {

    int fila = tableKardex.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione una salida de venta."
        );

        return;
    }

    int filaModelo =
            tableKardex.convertRowIndexToModel(
                    fila
            );

    if (filaModelo < 0
            || filaModelo >= listaKardexMostrada.size()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el movimiento seleccionado."
        );

        return;
    }

    Kardex kardex =
            listaKardexMostrada.get(
                    filaModelo
            );

    if (!"Salida".equalsIgnoreCase(
            kardex.getMovimiento())) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Solo se puede revertir una salida."
        );

        return;
    }

    if (!"Venta".equalsIgnoreCase(
            kardex.getTipoReferencia())) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El movimiento seleccionado no corresponde a una venta."
        );

        return;
    }
    if (tieneReversion(kardex)) {

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Esta salida ya fue revertida."
    );

    return;
}
    if (tieneAjusteDefectuoso(kardex)) {

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Esta venta ya tiene productos registrados como defectuosos "
            + "y no puede ser revertida."
    );

    return;
}

    javax.swing.JTextField txtObservacion =
        new javax.swing.JTextField(25);

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
                obtenerNombreProducto(
                        kardex.getIdProducto()
                )
        )
);

panel.add(
        new javax.swing.JLabel(
                "Lote:"
        )
);

panel.add(
        new javax.swing.JLabel(
                obtenerLote(
                        kardex.getIdInventario()
                )
        )
);

panel.add(
        new javax.swing.JLabel(
                "Cantidad:"
        )
);

panel.add(
        new javax.swing.JLabel(
                String.valueOf(
                        kardex.getCantidad()
                )
        )
);

panel.add(
        new javax.swing.JLabel(
                "Comprobante:"
        )
);

panel.add(
        new javax.swing.JLabel(
                kardex.getNroComprobante()
        )
);

panel.add(
        new javax.swing.JLabel(
                "Observación:"
        )
);

panel.add(
        txtObservacion
);

int respuesta =
        javax.swing.JOptionPane.showConfirmDialog(
                this,
                panel,
                "Confirmar reversión",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

if (respuesta
        != javax.swing.JOptionPane.OK_OPTION) {

    return;
}

String motivo =
        txtObservacion.getText()
                .trim();

if (motivo.isEmpty()) {
    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Ingrese una observación."
    );
    txtObservacion.requestFocus();
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

    boolean resultado =
        kardexController.revertirVenta(
                kardex.getIdKardex(),
                idUsuario,
                motivo
        );

    if (!resultado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo revertir la venta."
        );

        return;
    }

    cargarDatosKardex();
    cargarFiltrosKardex();
    cargarTablaKardex(listaKardex);

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Venta revertida correctamente."
    );
}
    private String obtenerNombreProducto(
        int idProducto) {

    for (Producto producto : listaProductos) {

        if (producto.getIdProducto()
                == idProducto) {

            return producto.getNombre();
        }
    }

    return "";
}
    private String obtenerLote(
        int idInventario) {

    for (Inventario inventario
            : listaInventario) {

        if (inventario.getIdInventario()
                == idInventario) {

            return inventario.getLote();
        }
    }

    return "";
}
  private void enviarStockDefectuoso() {

    int fila = tableKardex.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione una salida de venta."
        );

        return;
    }

    int filaModelo =
            tableKardex.convertRowIndexToModel(
                    fila
            );

    if (filaModelo < 0
            || filaModelo >= listaKardexMostrada.size()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el movimiento seleccionado."
        );

        return;
    }

    Kardex salida =
            listaKardexMostrada.get(
                    filaModelo
            );

    if (!"Salida".equalsIgnoreCase(
            salida.getMovimiento())
            || !"Venta".equalsIgnoreCase(
                    salida.getTipoReferencia())) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione una salida de venta."
        );

        return;
    }

    if (tieneReversion(salida)) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Esta venta ya fue revertida."
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

    int yaDefectuosos =
            kardexController
                    .obtenerCantidadDefectuosaPorVenta(
                            salida.getIdReferencia(),
                            salida.getIdInventario()
                    );

    int disponible =
            salida.getCantidad()
            - yaDefectuosos;

    if (disponible <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ya se registró toda la cantidad vendida "
                + "de este lote como defectuosa."
        );

        return;
    }

    javax.swing.JTextField txtCantidad =
            new javax.swing.JTextField(8);

    javax.swing.JTextField txtObservacion =
            new javax.swing.JTextField(20);

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
                    obtenerNombreProducto(
                            salida.getIdProducto()
                    )
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Lote:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    obtenerLote(
                            salida.getIdInventario()
                    )
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Comprobante:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    salida.getNroComprobante()
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Cantidad disponible:"
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    String.valueOf(
                            disponible
                    )
            )
    );

    panel.add(
            new javax.swing.JLabel(
                    "Cantidad defectuosa:"
            )
    );

    panel.add(
            txtCantidad
    );

    panel.add(
            new javax.swing.JLabel(
                    "Observación:"
            )
    );

    panel.add(
            txtObservacion
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Ingresar a S. Defectuoso",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    String textoCantidad =
            txtCantidad.getText()
                    .trim();

    if (!textoCantidad.matches(
            "\\d+")) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una cantidad válida."
        );

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

        return;
    }

    if (cantidad <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser mayor que 0."
        );

        return;
    }

    if (cantidad > disponible) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad no puede superar "
                + "la cantidad disponible: "
                + disponible
        );

        return;
    }

    String motivo =
            txtObservacion.getText()
                    .trim();

    if (motivo.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una observación."
        );

        return;
    }

    int confirmacion =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirmar ingreso a stock defectuoso?\n\n"
                    + "Cantidad: "
                    + cantidad
                    + "\n"
                    + "Observación: "
                    + motivo,
                    "Confirmar",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

    if (confirmacion
            != javax.swing.JOptionPane.YES_OPTION) {

        return;
    }

    boolean resultado =
            kardexController.enviarStockDefectuoso(
                    salida.getIdKardex(),
                    idUsuario,
                    cantidad,
                    motivo
            );

    if (!resultado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar el stock defectuoso."
        );

        return;
    }

    /*
     * ========================================
     * BUSCAR EL KARDEX RECIÉN GENERADO
     * ========================================
     */
    Kardex kardexDefectuoso = null;

    for (Kardex movimiento
            : kardexController.listar()) {

        if (!"Ajuste".equalsIgnoreCase(
                movimiento.getMovimiento())) {

            continue;
        }

        if (!"Venta".equalsIgnoreCase(
                movimiento.getTipoReferencia())) {

            continue;
        }

        if (movimiento.getIdReferencia()
                != salida.getIdReferencia()) {

            continue;
        }

        if (movimiento.getIdInventario()
                != salida.getIdInventario()) {

            continue;
        }

        if (movimiento.getCantidad()
                != cantidad) {

            continue;
        }

        if (!motivo.equals(
                movimiento.getMotivo())) {

            continue;
        }

        if (kardexDefectuoso == null
                || movimiento.getIdKardex()
                        > kardexDefectuoso.getIdKardex()) {

            kardexDefectuoso =
                    movimiento;
        }
    }

    if (kardexDefectuoso == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El stock defectuoso se registró correctamente, "
                + "pero no se pudo identificar el ID Kardex "
                + "para generar la etiqueta."
        );

        return;
    }

    /*
     * ========================================
     * BUSCAR INFORMACIÓN PARA LA ETIQUETA
     * ========================================
     */
    Producto productoEncontrado = null;

    for (Producto producto
            : listaProductos) {

        if (producto.getIdProducto()
                == salida.getIdProducto()) {

            productoEncontrado =
                    producto;

            break;
        }
    }

    Inventario inventarioEncontrado = null;

    for (Inventario inventario
            : listaInventario) {

        if (inventario.getIdInventario()
                == salida.getIdInventario()) {

            inventarioEncontrado =
                    inventario;

            break;
        }
    }

    if (productoEncontrado == null
            || inventarioEncontrado == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El stock defectuoso se registró correctamente, "
                + "pero no se pudo obtener la información "
                + "del producto para la etiqueta."
        );

        return;
    }

    String categoria = "";

    for (Categoria categoriaEncontrada
            : listaCategorias) {

        if (categoriaEncontrada.getIdCategoria()
                == productoEncontrado.getIdCategoria()) {

            categoria =
                    categoriaEncontrada.getNombre();

            break;
        }
    }

    String marca = "";

    for (Marca marcaEncontrada
            : listaMarcas) {

        if (marcaEncontrada.getIdMarca()
                == productoEncontrado.getIdMarca()) {

            marca =
                    marcaEncontrada.getNombre();

            break;
        }
    }

    String proveedor = "";

    for (Proveedor proveedorEncontrado
            : listaProveedores) {

        if (proveedorEncontrado.getIdProveedor()
                == inventarioEncontrado.getIdProveedor()) {

            proveedor =
                    proveedorEncontrado.getEmpresa();

            break;
        }
    }

    String usuario = "";

    for (Usuario usuarioEncontrado
            : listaUsuarios) {

        if (usuarioEncontrado.getIdUsuario()
                == idUsuario) {

            usuario =
                    usuarioEncontrado.getUsername();

            break;
        }
    }

    /*
     * ========================================
     * PREPARAR ETIQUETA
     * ========================================
     */
    GeneradorEtiquetaDefectuoso.DatosEtiqueta datosEtiqueta =
            new GeneradorEtiquetaDefectuoso.DatosEtiqueta(
                    kardexDefectuoso.getIdKardex(),
                    productoEncontrado.getNombre(),
                    productoEncontrado.getCodigo(),
                    categoria,
                    productoEncontrado.getUnidad(),
                    marca,
                    proveedor,
                    inventarioEncontrado.getLote(),
                    cantidad,
                    usuario,
                    motivo,
                    salida.getNroComprobante(),
                    kardexDefectuoso.getFechaMovimiento()
            );

    /*
     * Actualizar Kardex en pantalla.
     */
    cargarDatosKardex();
    cargarTablaKardex(
            listaKardex
    );

    /*
     * Confirmación antes de abrir el PDF.
     */
    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Stock defectuoso registrado correctamente.\n"
            + "ID Kardex: "
            + kardexDefectuoso.getIdKardex()
    );

    /*
     * ========================================
     * GENERAR Y ABRIR PDF
     * ========================================
     */
    try {

        GeneradorEtiquetaDefectuoso.generarYabrir(
                datosEtiqueta
        );

    } catch (java.io.IOException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El stock defectuoso se registró correctamente, "
                + "pero no se pudo generar o abrir la etiqueta.\n\n"
                + e.getMessage(),
                "Etiqueta",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );
    }
}
private void resolverDevolucion() {

    int fila = tableKardex.getSelectedRow();

    if (fila == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione el movimiento de "
                + "producto defectuoso."
        );

        return;
    }

    int filaModelo =
            tableKardex.convertRowIndexToModel(
                    fila
            );

    if (filaModelo < 0
            || filaModelo >= listaKardexMostrada.size()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el movimiento seleccionado."
        );

        return;
    }

    Kardex kardexDefectuoso =
            listaKardexMostrada.get(
                    filaModelo
            );

    /*
     * Debe seleccionarse el movimiento que
     * ingresó el producto a stock defectuoso.
     */
    if (!"Ajuste".equalsIgnoreCase(
            kardexDefectuoso.getMovimiento())
            || !"Producto Defectuoso".equalsIgnoreCase(
                    kardexDefectuoso.getMotivoMovimiento())
            || !"Venta".equalsIgnoreCase(
                    kardexDefectuoso.getTipoReferencia())) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un movimiento de "
                + "producto defectuoso generado "
                + "por una devolución de venta."
        );

        return;
    }

    /*
     * Usuario actual.
     */
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
     * Obtener la cantidad que todavía puede
     * resolverse.
     *
     * Ejemplo:
     * Original = 10
     * Repuesto = 4
     * Devuelto = 2
     * Pendiente = 4
     */
    int cantidadPendiente;

    try {

        cantidadPendiente =
                kardexController
                        .obtenerCantidadPendienteDevolucion(
                                kardexDefectuoso.getIdKardex()
                        );

    } catch (Exception e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo consultar la cantidad "
                + "pendiente de resolución.\n\n"
                + e.getMessage()
        );

        return;
    }

    if (cantidadPendiente <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este producto defectuoso "
                + "ya fue resuelto completamente."
        );

        return;
    }

    /*
     * Seleccionar tipo de resolución.
     */
    String[] opciones = {
        "Reponer producto",
        "Devolver dinero",
        "Cancelar"
    };

    int opcion =
            javax.swing.JOptionPane.showOptionDialog(
                    this,
                    "¿Cómo desea resolver la devolución?",
                    "Resolver devolución",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

    if (opcion == 2
            || opcion == javax.swing.JOptionPane.CLOSED_OPTION) {

        return;
    }

    /*
     * Campo de cantidad.
     *
     * Se coloca automáticamente toda la cantidad
     * pendiente, pero el usuario puede reducirla
     * para una resolución parcial.
     */
    javax.swing.JTextField txtCantidad =
            new javax.swing.JTextField(
                    String.valueOf(
                            cantidadPendiente
                    )
            );

    /*
     * Mostrar información de la devolución.
     */
    javax.swing.JPanel panelCantidad =
            new javax.swing.JPanel(
                    new java.awt.GridLayout(
                            0,
                            2,
                            5,
                            5
                    )
            );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Producto:"
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    obtenerNombreProducto(
                            kardexDefectuoso.getIdProducto()
                    )
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Lote:"
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    obtenerLote(
                            kardexDefectuoso.getIdInventario()
                    )
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Comprobante:"
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    kardexDefectuoso.getNroComprobante()
                            == null
                            ? "—"
                            : kardexDefectuoso
                                    .getNroComprobante()
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Pendiente:"
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    String.valueOf(
                            cantidadPendiente
                    )
            )
    );

    panelCantidad.add(
            new javax.swing.JLabel(
                    "Cantidad a resolver:"
            )
    );

    panelCantidad.add(
            txtCantidad
    );

    int respuesta =
            javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    panelCantidad,
                    opcion == 0
                            ? "Reponer producto"
                            : "Devolver dinero",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

    if (respuesta
            != javax.swing.JOptionPane.OK_OPTION) {

        return;
    }

    /*
     * Validar cantidad ingresada.
     */
    int cantidad;

    try {

        cantidad =
                Integer.parseInt(
                        txtCantidad
                                .getText()
                                .trim()
                );

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una cantidad numérica válida."
        );

        return;
    }

    if (cantidad <= 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser mayor que cero."
        );

        return;
    }

    if (cantidad > cantidadPendiente) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad ingresada supera "
                + "la cantidad pendiente.\n\n"
                + "Pendiente: "
                + cantidadPendiente
        );

        return;
    }

    /*
     * ==================================================
     * REPONER PRODUCTO
     * ==================================================
     */
    if (opcion == 0) {

        /*
         * Observación automática.
         */
        String motivo =
                "Reposición por producto defectuoso";

        boolean resultado =
                kardexController.reponerProductoDefectuoso(
                        kardexDefectuoso.getIdKardex(),
                        idUsuario,
                        cantidad,
                        motivo
                );

        if (!resultado) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar la reposición."
            );

            return;
        }

        cargarDatosKardex();

        cargarFiltrosKardex();

        cargarTablaKardex(
                listaKardex
        );

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La reposición se registró correctamente."
        );

        return;
    }

    /*
     * ==================================================
     * DEVOLVER DINERO
     * ==================================================
     */

    String[] metodosPago = {
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Yape"
    };

    String metodoPago =
            (String) javax.swing.JOptionPane.showInputDialog(
                    this,
                    "Seleccione el método de devolución:",
                    "Método de pago",
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    metodosPago,
                    metodosPago[0]
            );

    if (metodoPago == null) {

        return;
    }

    /*
     * Observación automática.
     */
    String motivo =
            "Devolución de dinero por producto defectuoso";

    boolean resultado =
            kardexController.devolverDineroDefectuoso(
                    kardexDefectuoso.getIdKardex(),
                    idUsuario,
                    cantidad,
                    metodoPago,
                    motivo
            );

    if (!resultado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar la devolución de dinero.\n"
                + "Verifique que exista una Caja abierta "
                + "y que la cantidad pendiente sea suficiente."
        );

        return;
    }

    cargarDatosKardex();

    cargarFiltrosKardex();

    cargarTablaKardex(
            listaKardex
    );

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "La devolución de dinero se registró correctamente."
    );
}
  
  
    private boolean tieneReversion(Kardex salida) {

    for (Kardex movimiento : listaKardex) {

        if ("Reversion".equalsIgnoreCase(
                movimiento.getMovimiento())
                && "Venta".equalsIgnoreCase(
                        movimiento.getTipoReferencia())
                && movimiento.getIdReferencia()
                        == salida.getIdReferencia()
                && movimiento.getIdInventario()
                        == salida.getIdInventario()) {

            return true;
        }
    }

    return false;
}

private void abrirPdfDesdeDetalle(
        Kardex kardex) {

    if (kardex == null) {
        return;
    }

    try {

        /*
         * ========================================
         * VENTA NORMAL
         * ========================================
         */
        if ("Salida".equalsIgnoreCase(
                kardex.getMovimiento())
                && "Venta".equalsIgnoreCase(
                        kardex.getTipoReferencia())) {

            String tipoComprobante =
                    kardex.getNroComprobante() != null
                    && kardex.getNroComprobante()
                            .startsWith("B")
                    ? "Boleta"
                    : "Factura";

            GeneradorPDF.abrirExistente(
                    tipoComprobante,
                    kardex.getNroComprobante(),
                    kardex.getFechaMovimiento()
            );

            return;
        }

        /*
         * ========================================
         * REVERSION DE VENTA
         *
         * Busca la salida original para utilizar
         * el comprobante y la fecha originales.
         * ========================================
         */
        if ("Reversion".equalsIgnoreCase(
                kardex.getMovimiento())
                && "Venta".equalsIgnoreCase(
                        kardex.getTipoReferencia())) {

            Kardex salidaOriginal = null;

            for (Kardex movimiento
                    : listaKardex) {

                if ("Salida".equalsIgnoreCase(
                        movimiento.getMovimiento())
                        && "Venta".equalsIgnoreCase(
                                movimiento.getTipoReferencia())
                        && movimiento.getIdReferencia()
                                == kardex.getIdReferencia()) {

                    salidaOriginal =
                            movimiento;

                    break;
                }
            }

            if (salidaOriginal == null) {

                throw new java.io.IOException(
                        "No se encontró la venta original."
                );
            }

            String tipoComprobante =
                    salidaOriginal.getNroComprobante() != null
                    && salidaOriginal.getNroComprobante()
                            .startsWith("B")
                    ? "Boleta"
                    : "Factura";

            GeneradorPDF.abrirExistente(
                    tipoComprobante,
                    salidaOriginal.getNroComprobante(),
                    salidaOriginal.getFechaMovimiento()
            );

            return;
        }

        /*
         * ========================================
         * PRODUCTO DEFECTUOSO
         *
         * El ajuste asociado a una venta
         * corresponde al ingreso del producto
         * a stock defectuoso.
         * ========================================
         */
        if ("Ajuste".equalsIgnoreCase(
                kardex.getMovimiento())
                && "Venta".equalsIgnoreCase(
                        kardex.getTipoReferencia())) {

            GeneradorEtiquetaDefectuoso.abrirExistente(
                    kardex.getIdKardex()
            );

            return;
        }

        /*
         * ========================================
         * RETIRO DE PRODUCTO DEFECTUOSO
         *
         * tipo_referencia = Ajuste
         * id_referencia = ID Kardex del
         * defectuoso original.
         * ========================================
         */
        if ("Ajuste".equalsIgnoreCase(
                kardex.getMovimiento())
                && "Ajuste".equalsIgnoreCase(
                        kardex.getTipoReferencia())
                && kardex.getIdReferencia() > 0) {

            GeneradorEtiquetaDefectuoso.abrirExistente(
                    kardex.getIdReferencia()
            );

            return;
        }

        /*
         * ========================================
         * OTROS AJUSTES
         *
         * Los ajustes manuales de stock no tienen
         * un PDF asociado.
         * ========================================
         */
        throw new java.io.IOException(
                "Este movimiento no tiene un PDF asociado."
        );

    } catch (Exception e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo abrir el PDF.\n\n"
                + e.getMessage(),
                "Abrir PDF",
                javax.swing.JOptionPane.WARNING_MESSAGE
        );
    }
}
private boolean tieneAjusteDefectuoso(Kardex salida) {

    for (Kardex movimiento : listaKardex) {

        if ("Ajuste".equalsIgnoreCase(
                movimiento.getMovimiento())
                && "Venta".equalsIgnoreCase(
                        movimiento.getTipoReferencia())
                && movimiento.getIdReferencia()
                        == salida.getIdReferencia()
                && movimiento.getIdInventario()
                        == salida.getIdInventario()) {

            return true;
        }
    }

    return false;
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txtBuscarProducto = new javax.swing.JTextField();
        comboMovimiento = new javax.swing.JComboBox<>();
        btnActualizar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableKardex = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        comboUsuario = new javax.swing.JComboBox<>();
        btnRevertirVenta = new javax.swing.JButton();
        btnEnviarStockDefectuosoaInventario = new javax.swing.JButton();
        btnVerDetalle = new javax.swing.JButton();
        btnResolverDevolucion = new javax.swing.JButton();
        comboMotivo = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(239, 248, 254));

        jPanel1.setBackground(new java.awt.Color(239, 248, 254));

        jLabel34.setText("Buscar Producto: ");

        jLabel37.setText("Motivo");

        jLabel36.setText("Movimiento");

        txtBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoActionPerformed(evt);
            }
        });
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyReleased(evt);
            }
        });

        comboMovimiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));
        comboMovimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMovimientoActionPerformed(evt);
            }
        });

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        tableKardex.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Producto", "Lote", "Fecha", "Movimiento", "Cant.", "Stock Antes", "Stock Despues", "Precio", "Monto", "Proveedor/ Cliente", "Comprobante", "usuario", "Estante", "Nivel"
            }
        ));
        jScrollPane4.setViewportView(tableKardex);

        jLabel38.setText("Usuario");

        comboUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUsuarioActionPerformed(evt);
            }
        });

        btnRevertirVenta.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        btnRevertirVenta.setText("Revertir Venta");
        btnRevertirVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevertirVentaActionPerformed(evt);
            }
        });

        btnEnviarStockDefectuosoaInventario.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        btnEnviarStockDefectuosoaInventario.setText("Ingresar Stock Defectuoso");
        btnEnviarStockDefectuosoaInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarStockDefectuosoaInventarioActionPerformed(evt);
            }
        });

        btnVerDetalle.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        btnVerDetalle.setText("Ver Detalle");
        btnVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalleActionPerformed(evt);
            }
        });

        btnResolverDevolucion.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        btnResolverDevolucion.setText("Resolver Devolucion");
        btnResolverDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResolverDevolucionActionPerformed(evt);
            }
        });

        comboMotivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMotivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(btnVerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(177, 177, 177)
                .addComponent(btnRevertirVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(112, 112, 112)
                .addComponent(btnEnviarStockDefectuosoaInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(btnResolverDevolucion)
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(comboMovimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37)
                    .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(btnActualizar)
                    .addComponent(comboMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRevertirVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviarStockDefectuosoaInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResolverDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1501, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 661, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductoActionPerformed

    private void txtBuscarProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyReleased
         filtrarTablaKardex();
    }//GEN-LAST:event_txtBuscarProductoKeyReleased

    private void comboMovimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMovimientoActionPerformed
filtrarTablaKardex();        
    }//GEN-LAST:event_comboMovimientoActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
actualizarKardex();        
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void comboUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUsuarioActionPerformed
filtrarTablaKardex();      
    }//GEN-LAST:event_comboUsuarioActionPerformed

    private void btnRevertirVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevertirVentaActionPerformed
revertirVenta();     
    }//GEN-LAST:event_btnRevertirVentaActionPerformed

    private void btnEnviarStockDefectuosoaInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarStockDefectuosoaInventarioActionPerformed
   enviarStockDefectuoso();     // TODO add your handling code here:
    }//GEN-LAST:event_btnEnviarStockDefectuosoaInventarioActionPerformed

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
verDetalleKardex();        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerDetalleActionPerformed

    private void btnResolverDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResolverDevolucionActionPerformed
   resolverDevolucion();     // TODO add your handling code here:
    }//GEN-LAST:event_btnResolverDevolucionActionPerformed

    private void comboMotivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMotivoActionPerformed
filtrarTablaKardex();        // TODO add your handling code here:
    }//GEN-LAST:event_comboMotivoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEnviarStockDefectuosoaInventario;
    private javax.swing.JButton btnResolverDevolucion;
    private javax.swing.JButton btnRevertirVenta;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JComboBox<String> comboMotivo;
    private javax.swing.JComboBox<String> comboMovimiento;
    private javax.swing.JComboBox<String> comboUsuario;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tableKardex;
    private javax.swing.JTextField txtBuscarProducto;
    // End of variables declaration//GEN-END:variables
}
