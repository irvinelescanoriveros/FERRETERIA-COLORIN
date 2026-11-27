package pe.ferreteria.vista;

/**
 * Pantalla de recepción de compras (Java Swing). Capa de vista del patrón MVC: solo presenta datos y
 * delega la lógica en los controladores.
 */
public class RecepcionPanel extends javax.swing.JPanel {
private javax.swing.Timer temporizadorEscaner;
private boolean procesandoEscaner;
private final pe.ferreteria.controlador.RecepcionController recepcionController =
        new pe.ferreteria.controlador.RecepcionController();

private final pe.ferreteria.controlador.ProveedorController proveedorController =
        new pe.ferreteria.controlador.ProveedorController();
    public RecepcionPanel() {
        initComponents();
        pe.ferreteria.modelo.Usuario usuario =
                pe.ferreteria.infraestructura.SesionUsuario.getUsuarioActual();
        configurarAccesoAdministrador(usuario == null ? null : usuario.getRol());
        configurarEscanerAutomatico();
        inicializarRecepcion();
    }

    private void inicializarRecepcion() {

    configurarTablaRecepcion();
    cargarComprasPendientes();

    txtNombreProveedor.setText("");
    txtFechaDelComprobante.setText("");

    limpiarProductoSeleccionado();
    txtEscaner.requestFocusInWindow();
}

    private void configurarEscanerAutomatico() {
        temporizadorEscaner = new javax.swing.Timer(450, evt -> procesarEscaneoAutomatico());
        temporizadorEscaner.setRepeats(false);
        txtEscaner.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void cambio() {
                if (txtEscaner.getText().trim().length() >= 3) temporizadorEscaner.restart();
                else temporizadorEscaner.stop();
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { cambio(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { cambio(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { cambio(); }
        });
    }

    private void procesarEscaneoAutomatico() {
        String codigo = txtEscaner.getText().trim();
        if (codigo.length() >= 3
                && new pe.ferreteria.controlador.ProductoController().buscarPorCodigo(codigo) != null) {
            procesarCodigoEscaneadoSeguro();
        }
    }

    private void procesarCodigoEscaneadoSeguro() {
        if (procesandoEscaner || txtEscaner.getText().trim().isEmpty()) return;
        procesandoEscaner = true;
        try {
            procesarCodigoEscaneadoAhora();
        } finally {
            procesandoEscaner = false;
        }
    }

    private void procesarCodigoEscaneadoAhora() {
        String codigo = txtEscaner.getText().trim();
        if (codigo.isEmpty()) return;
        pe.ferreteria.modelo.Compra compra = obtenerCompraSeleccionada();
        if (compra == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecciona primero el comprobante de compra.");
            txtEscaner.selectAll();
            return;
        }
        pe.ferreteria.modelo.Producto producto =
                new pe.ferreteria.controlador.ProductoController().buscarPorCodigo(codigo);
        if (producto == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "No se encontró un producto activo con ese código.");
            txtEscaner.selectAll();
            return;
        }
        pe.ferreteria.modelo.DetalleCompra detalle = null;
        for (pe.ferreteria.modelo.DetalleCompra linea :
                recepcionController.listarDetallesCompra(compra.getIdCompra())) {
            if (linea.getIdProducto() == producto.getIdProducto()) { detalle = linea; break; }
        }
        if (detalle == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "El producto no forma parte del comprobante seleccionado.");
            txtEscaner.selectAll();
            return;
        }
        int idUsuario = pe.ferreteria.infraestructura.SesionUsuario.getIdUsuario();
        if (idUsuario <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay un usuario con sesión iniciada.");
            return;
        }
        int idRecepcion = obtenerOCrearRecepcion(idUsuario);
        if (idRecepcion <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo obtener la recepción.");
            return;
        }
        pe.ferreteria.modelo.DetalleRecepcion detalleActual =
                recepcionController.buscarDetalleRecepcion(
                        idRecepcion, detalle.getIdDetalle());
        if (detalleActual != null) {
            int recibidas = detalleActual.getCantidadRecibida() == null
                    ? 0 : detalleActual.getCantidadRecibida();
            int maximo = detalle.getCantidad() - detalleActual.getCantidadDefectuosa();
            if ("Completada".equalsIgnoreCase(detalleActual.getEstado())
                    || recibidas >= maximo) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Ya se recibieron todas las unidades permitidas de este lote.",
                        "Recepción completa", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                txtEscaner.setText("");
                txtEscaner.requestFocusInWindow();
                return;
            }
        }
        int cantidad = recepcionController.procesarEscaneo(
                compra, detalle, producto, idRecepcion, idUsuario);
        if (cantidad <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se registró el escaneo. Revisa el estado y la cantidad recibida.");
            txtEscaner.selectAll();
            return;
        }
        pe.ferreteria.modelo.DetalleRecepcion detalleActualizado =
                recepcionController.buscarDetalleRecepcion(
                        idRecepcion, detalle.getIdDetalle());
        int cantidadDefectuosa = detalleActualizado == null
                ? 0 : detalleActualizado.getCantidadDefectuosa();
        int cantidadMaxima = detalle.getCantidad() - cantidadDefectuosa;

        lblProductoEscaneado.setText(
                "Producto seleccionado: " + detalle.getLote() + " - " + producto.getNombre());
        lblPresentacionEscaneada.setText(
                "Presentación: " + producto.getUnidad());
        lblCantidadRegistrada.setText(
                "Cantidad registrada total: " + cantidad + " / " + cantidadMaxima
                + (cantidadDefectuosa > 0
                        ? " (" + cantidadDefectuosa + " observadas)" : ""));

        txtProductoSeleccionado.setText(detalle.getLote() + " - " + producto.getNombre());
        txtCantidaddelProducto.setText(String.valueOf(cantidad));
        txtObservacion.setText("");
        txtCantidadObservada.setText("");
        txtBuscarProducto.setText("");
        comboEstado.setSelectedItem("Todos");
        cargarTablaRecepcion();
        for (int fila = 0; fila < tableRecepcionDelComprobante.getRowCount(); fila++) {
            if (tableRecepcionDelComprobante.getValueAt(fila, 0).toString().equals(detalle.getLote())) {
                tableRecepcionDelComprobante.setRowSelectionInterval(fila, fila);
                break;
            }
        }
        txtEscaner.setText("");
        txtEscaner.requestFocusInWindow();
    }
    private void cargarComprasPendientes() {

        pe.ferreteria.controlador.RecepcionController controller =
                new pe.ferreteria.controlador.RecepcionController();

        java.util.List<pe.ferreteria.modelo.Compra> compras =
                controller.listarComprasPendientesRecepcion();

        comboComprobante.removeAllItems();

        for (pe.ferreteria.modelo.Compra compra : compras) {

            comboComprobante.addItem(
                    compra.getNroComprobante()
            );
        }
    }
    private void cargarDatosCompraSeleccionada() {

    int indice = comboComprobante.getSelectedIndex();

    if (indice < 0) {
        txtNombreProveedor.setText("");
        txtFechaDelComprobante.setText("");
        return;
    }

    java.util.List<pe.ferreteria.modelo.Compra> compras =
            recepcionController.listarComprasPendientesRecepcion();

    if (indice >= compras.size()) {
        txtNombreProveedor.setText("");
        txtFechaDelComprobante.setText("");
        return;
    }

    pe.ferreteria.modelo.Compra compra =
            compras.get(indice);

    txtFechaDelComprobante.setText(
            compra.getFechaCompra()
                    .toLocalDate()
                    .toString()
    );

    String nombreProveedor = "";

    for (pe.ferreteria.modelo.Proveedor proveedor
            : proveedorController.listar()) {

        if (proveedor.getIdProveedor()
                == compra.getIdProveedor()) {

            nombreProveedor = proveedor.getEmpresa();
            break;
        }
    }

    txtNombreProveedor.setText(nombreProveedor);
}
    private void configurarTablaRecepcion() {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    tableRecepcionDelComprobante.getModel();

    modelo = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Lote",
                "Producto",
                "Categoría",
                "Marca",
                "Recibida",
                "Comprada",
                "Observada",
                "F.Venc.",
                "Estante",
                "Nivel",
                "Estado"
            }
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tableRecepcionDelComprobante.setModel(modelo);
}
    private void cargarTablaRecepcion() {

    int indice = comboComprobante.getSelectedIndex();

    if (indice < 0) {
        return;
    }

    java.util.List<pe.ferreteria.modelo.Compra> compras =
            recepcionController.listarComprasPendientesRecepcion();

    if (indice >= compras.size()) {
        return;
    }

    pe.ferreteria.modelo.Compra compra =
            compras.get(indice);

    java.util.List<pe.ferreteria.modelo.DetalleCompra> detallesCompra =
            recepcionController.listarDetallesCompra(
                    compra.getIdCompra()
            );

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    tableRecepcionDelComprobante.getModel();

    modelo.setRowCount(0);

    pe.ferreteria.controlador.ProductoController productoController =
            new pe.ferreteria.controlador.ProductoController();

    pe.ferreteria.controlador.CategoriaController categoriaController =
            new pe.ferreteria.controlador.CategoriaController();

    pe.ferreteria.controlador.MarcaController marcaController =
            new pe.ferreteria.controlador.MarcaController();

    java.util.List<pe.ferreteria.modelo.Producto> productos =
            productoController.listar();

    java.util.List<pe.ferreteria.modelo.Categoria> categorias =
            categoriaController.listar();

    java.util.List<pe.ferreteria.modelo.Marca> marcas =
            marcaController.listar();

    pe.ferreteria.modelo.Recepcion recepcion =
            recepcionController.buscarUltimaPorCompra(
                    compra.getIdCompra()
            );

    java.util.List<pe.ferreteria.modelo.DetalleRecepcion> detallesRecepcion =
            new java.util.ArrayList<>();

    if (recepcion != null) {

        detallesRecepcion =
                recepcionController.listarDetallesPorRecepcion(
                        recepcion.getIdRecepcion()
                );
    }

    for (pe.ferreteria.modelo.DetalleCompra detalle
            : detallesCompra) {

        pe.ferreteria.modelo.Producto productoEncontrado = null;

        for (pe.ferreteria.modelo.Producto producto : productos) {

            if (producto.getIdProducto()
                    == detalle.getIdProducto()) {

                productoEncontrado = producto;
                break;
            }
        }

        if (productoEncontrado == null) {
            continue;
        }

        String nombreCategoria = "";

        for (pe.ferreteria.modelo.Categoria categoria
                : categorias) {

            if (categoria.getIdCategoria()
                    == productoEncontrado.getIdCategoria()) {

                nombreCategoria = categoria.getNombre();
                break;
            }
        }

        String nombreMarca = "";

        for (pe.ferreteria.modelo.Marca marca : marcas) {

            if (marca.getIdMarca()
                    == productoEncontrado.getIdMarca()) {

                nombreMarca = marca.getNombre();
                break;
            }
        }

        String fechaVencimiento =
                detalle.getFechaVencimiento() == null
                        ? "Sin vencimiento"
                        : detalle.getFechaVencimiento().toString();

        String estado = "Pendiente";

        int cantidadRecibidaMostrar = 0;
        int cantidadObservadaMostrar = 0;

        for (pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion
                : detallesRecepcion) {

            if (detalleRecepcion.getIdDetalleCompra()
                    == detalle.getIdDetalle()) {

                if (detalleRecepcion.getEstado() != null) {
                    estado = detalleRecepcion.getEstado();
                }

                if (detalleRecepcion.getCantidadRecibida() != null) {
                    cantidadRecibidaMostrar =
                            detalleRecepcion.getCantidadRecibida();
                }
                cantidadObservadaMostrar = detalleRecepcion.getCantidadDefectuosa();

                break;
            }
        }

        modelo.addRow(new Object[]{
            detalle.getLote(),
            productoEncontrado.getNombre(),
            nombreCategoria,
            nombreMarca,
            cantidadRecibidaMostrar,
            detalle.getCantidad(),
            cantidadObservadaMostrar,
            fechaVencimiento,
            productoEncontrado.getEstante(),
            productoEncontrado.getNivel(),
            estado
        });
    }
}
   private void cargarProductoSeleccionado() {

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    if (fila == -1) {
        return;
    }

    String lote =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 0)
                    .toString();

    String nombreProducto =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 1)
                    .toString();

    String cantidad =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 5)
                    .toString();

    txtProductoSeleccionado.setText(
            lote + " - " + nombreProducto
    );

    txtCantidaddelProducto.setText(cantidad);

    txtObservacion.setText("");
    txtCantidadObservada.setText("");

    pe.ferreteria.modelo.Compra compra =
            obtenerCompraSeleccionada();

    pe.ferreteria.modelo.DetalleCompra detalleCompra =
            obtenerDetalleSeleccionado();

    if (compra == null || detalleCompra == null) {
        return;
    }

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    if (recepcion == null) {
        return;
    }

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    recepcion.getIdRecepcion(),
                    detalleCompra.getIdDetalle()
            );

    if (detalleRecepcion == null) {
        return;
    }

    if ("Observada".equalsIgnoreCase(
            detalleRecepcion.getEstado())) {

        if (detalleRecepcion.getObservacion() != null) {
            txtObservacion.setText(
                    detalleRecepcion.getObservacion()
            );
        }

        if (detalleRecepcion.getCantidadDefectuosa() > 0) {
            txtCantidadObservada.setText(
                    String.valueOf(
                            detalleRecepcion.getCantidadDefectuosa()
                    )
            );
        }
    }
}
    private pe.ferreteria.modelo.Compra obtenerCompraSeleccionada() {

    int indice = comboComprobante.getSelectedIndex();

    if (indice < 0) {
        return null;
    }

    java.util.List<pe.ferreteria.modelo.Compra> compras =
            recepcionController.listarComprasPendientesRecepcion();

    if (indice >= compras.size()) {
        return null;
    }

    return compras.get(indice);
}
    private pe.ferreteria.modelo.DetalleCompra obtenerDetalleSeleccionado() {

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    if (fila == -1) {
        return null;
    }

    pe.ferreteria.modelo.Compra compra =
            obtenerCompraSeleccionada();

    if (compra == null) {
        return null;
    }

    String lote =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 0)
                    .toString();

    java.util.List<pe.ferreteria.modelo.DetalleCompra> detalles =
            recepcionController.listarDetallesCompra(
                    compra.getIdCompra()
            );

    for (pe.ferreteria.modelo.DetalleCompra detalle : detalles) {

        if (detalle.getLote().equals(lote)) {
            return detalle;
        }
    }

    return null;
}
    private boolean validarSeleccionRecepcion() {

    if (comboComprobante.getSelectedIndex() == -1) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un comprobante de compra."
        );
        return false;
    }

    if (tableRecepcionDelComprobante.getSelectedRow() == -1) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un producto de la tabla."
        );
        return false;
    }

    if (obtenerCompraSeleccionada() == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar la compra seleccionada."
        );
        return false;
    }

    if (obtenerDetalleSeleccionado() == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el producto seleccionado."
        );
        return false;
    }

    return true;
}
    private Integer obtenerCantidadObservadaValidada() {

    String texto = txtCantidadObservada.getText().trim();

    if (texto.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese la cantidad observada."
        );
        txtCantidadObservada.requestFocus();
        return null;
    }

    if (!texto.matches("\\d+")) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe contener solo números."
        );
        txtCantidadObservada.requestFocus();
        return null;
    }

    int cantidadObservada;

    try {
        cantidadObservada = Integer.parseInt(texto);
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad ingresada no es válida."
        );
        txtCantidadObservada.requestFocus();
        return null;
    }

    if (cantidadObservada <= 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad observada debe ser mayor que cero."
        );
        txtCantidadObservada.requestFocus();
        return null;
    }

    return cantidadObservada;
}
    private void limpiarProductoSeleccionado() {

    txtProductoSeleccionado.setText("");
    txtCantidaddelProducto.setText("");
    txtObservacion.setText("");
    txtCantidadObservada.setText("");
    lblProductoEscaneado.setText("--");
    lblPresentacionEscaneada.setText("--");
    lblCantidadRegistrada.setText("-");

    tableRecepcionDelComprobante.clearSelection();
}
    private void actualizarComprobanteSeleccionado() {

    if (comboComprobante.getSelectedIndex() == -1) {

        txtNombreProveedor.setText("");
        txtFechaDelComprobante.setText("");

        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel)
                        tableRecepcionDelComprobante.getModel();

        modelo.setRowCount(0);

        limpiarProductoSeleccionado();

        return;
    }

    limpiarProductoSeleccionado();

    cargarDatosCompraSeleccionada();
    cargarTablaRecepcion();
}
    private void actualizarListaComprobantes() {

    cargarComprasPendientes();

    txtNombreProveedor.setText("");
    txtFechaDelComprobante.setText("");

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    tableRecepcionDelComprobante.getModel();

    modelo.setRowCount(0);

    limpiarProductoSeleccionado();
}
    private void filtrarTablaRecepcion() {

    if (comboComprobante.getSelectedIndex() == -1) {
        return;
    }

    cargarTablaRecepcion();

    String textoBusqueda =
            txtBuscarProducto.getText().trim().toLowerCase();

    String estadoSeleccionado =
            comboEstado.getSelectedItem() == null
                    ? ""
                    : comboEstado.getSelectedItem().toString();

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    tableRecepcionDelComprobante.getModel();

    for (int fila = modelo.getRowCount() - 1; fila >= 0; fila--) {

        String lote =
                modelo.getValueAt(fila, 0).toString().toLowerCase();

        String producto =
                modelo.getValueAt(fila, 1).toString().toLowerCase();

        String categoria =
                modelo.getValueAt(fila, 2).toString().toLowerCase();

        String marca =
                modelo.getValueAt(fila, 3).toString().toLowerCase();

        String estado =
                modelo.getValueAt(fila, 10).toString();

        boolean coincideTexto =
                textoBusqueda.isEmpty()
                || lote.contains(textoBusqueda)
                || producto.contains(textoBusqueda)
                || categoria.contains(textoBusqueda)
                || marca.contains(textoBusqueda);

        boolean coincideEstado =
                estadoSeleccionado.isEmpty()
                || "Todos".equalsIgnoreCase(estadoSeleccionado)
                || estado.equalsIgnoreCase(estadoSeleccionado);

        if (!coincideTexto || !coincideEstado) {
            modelo.removeRow(fila);
        }
    }
}
    private pe.ferreteria.modelo.Recepcion obtenerRecepcionActual() {

    pe.ferreteria.modelo.Compra compra =
            obtenerCompraSeleccionada();

    if (compra == null) {
        return null;
    }

    return recepcionController.buscarUltimaPorCompra(
            compra.getIdCompra()
    );
}
    private boolean validarObservacion() {

    if (!validarSeleccionRecepcion()) {
        return false;
    }

    pe.ferreteria.modelo.DetalleCompra detalle =
            obtenerDetalleSeleccionado();

    if (detalle == null) {
        return false;
    }

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    String estadoActual =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 10)
                    .toString();

    if ("Completada".equalsIgnoreCase(estadoActual)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este producto ya está completado y no puede ser observado."
        );
        return false;
    }

    String observacion =
            txtObservacion.getText().trim();

    if (observacion.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese una observación."
        );
        txtObservacion.requestFocus();
        return false;
    }

    Integer cantidadRecibida =
            obtenerCantidadObservadaValidada();

    return cantidadRecibida != null;
}
    private int obtenerOCrearRecepcion(int idUsuario) {

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    if (recepcion != null) {
        return recepcion.getIdRecepcion();
    }

    pe.ferreteria.modelo.Compra compra =
            obtenerCompraSeleccionada();

    if (compra == null) {
        return 0;
    }

    pe.ferreteria.modelo.Recepcion nuevaRecepcion =
            new pe.ferreteria.modelo.Recepcion();

    nuevaRecepcion.setIdCompra(
            compra.getIdCompra()
    );

    nuevaRecepcion.setIdUsuario(idUsuario);

    nuevaRecepcion.setFechaRecepcion(
            java.time.LocalDateTime.now()
    );

    nuevaRecepcion.setEstado("Pendiente");

    nuevaRecepcion.setObservacion(null);

    return recepcionController.registrarRecepcion(
            nuevaRecepcion
    );
}
    private boolean observarProducto() {

    if (!validarObservacion()) {
        return false;
    }

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario.getIdUsuario();

    if (idUsuario == 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );
        return false;
    }

    pe.ferreteria.modelo.DetalleCompra detalleCompra =
            obtenerDetalleSeleccionado();

    if (detalleCompra == null) {
        return false;
    }

    int idRecepcion = obtenerOCrearRecepcion(idUsuario);

    if (idRecepcion == 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar la recepción."
        );
        return false;
    }

    Integer cantidadDefectuosa =
            obtenerCantidadObservadaValidada();

    if (cantidadDefectuosa == null) {
        return false;
    }

    String resultado = recepcionController.observarRecepcion(
            idRecepcion, detalleCompra.getIdDetalle(), cantidadDefectuosa,
            txtObservacion.getText().trim());

    if (!"OK".equals(resultado)) {
        javax.swing.JOptionPane.showMessageDialog(this, resultado);
        txtCantidadObservada.requestFocusInWindow();
        return false;
    }

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Recepción observada correctamente."
    );

    cargarTablaRecepcion();

    return true;
}
   private boolean validarCompletar() {

    if (!validarSeleccionRecepcion()) {
        return false;
    }

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    String estado =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 10)
                    .toString();

    if ("Completada".equalsIgnoreCase(estado)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este producto ya está completado."
        );
        return false;
    }

    return obtenerDetalleSeleccionado() != null;
}
 private boolean completarProducto() {

    if (!validarCompletar()) {
        return false;
    }

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario
                    .getIdUsuario();

    if (idUsuario == 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );

        return false;
    }

    pe.ferreteria.modelo.Compra compra =
            obtenerCompraSeleccionada();

    pe.ferreteria.modelo.DetalleCompra detalleCompra =
            obtenerDetalleSeleccionado();

    if (compra == null
            || detalleCompra == null) {

        return false;
    }

    pe.ferreteria.modelo.Recepcion recepcionActual = obtenerRecepcionActual();
    pe.ferreteria.modelo.DetalleRecepcion detalleActual = recepcionActual == null
            ? null : recepcionController.buscarDetalleRecepcion(
                    recepcionActual.getIdRecepcion(), detalleCompra.getIdDetalle());
    int recibidas = detalleActual == null || detalleActual.getCantidadRecibida() == null
            ? 0 : detalleActual.getCantidadRecibida();
    int observadas = detalleActual == null ? 0 : detalleActual.getCantidadDefectuosa();
    int tope = detalleCompra.getCantidad() - observadas;
    int faltante = tope - recibidas;

    if (faltante <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "Ya se recibieron todas las unidades en buen estado.\n"
                + "Si hay unidades observadas, deben resolverse con «Corregir recepción»." );
        return false;
    }

    int cantidadAIngresar = faltante;
    if (recibidas > 0) {
        Object respuesta = javax.swing.JOptionPane.showInputDialog(
                this,
                "Recibidas: " + recibidas + " de " + tope + "\n"
                + "Faltan: " + faltante + "\n\n"
                + "Cantidad a ingresar (1 a " + faltante + "):",
                "Completar recepción", javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, null, String.valueOf(faltante));
        if (respuesta == null) return false;
        String texto = respuesta.toString().trim();
        if (!texto.matches("\\d+")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Cantidad inválida. Ingrese solo números.");
            return false;
        }
        try {
            cantidadAIngresar = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            return false;
        }
        if (cantidadAIngresar < 1 || cantidadAIngresar > faltante) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "La cantidad debe estar entre 1 y " + faltante + ".");
            return false;
        }
    }

    int idRecepcion = obtenerOCrearRecepcion(idUsuario);
    if (idRecepcion == 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "No se pudo obtener la recepción.");
        return false;
    }

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion = detalleActual;
    if (detalleRecepcion == null) {
        detalleRecepcion = new pe.ferreteria.modelo.DetalleRecepcion();
        detalleRecepcion.setIdRecepcion(idRecepcion);
        detalleRecepcion.setIdDetalleCompra(detalleCompra.getIdDetalle());
        detalleRecepcion.setCantidadRecibida(0);
        detalleRecepcion.setEstado("Pendiente");
    }
    int cantidadObjetivo = recibidas + cantidadAIngresar;

    /*
     * Buscar el producto maestro.
     */
    pe.ferreteria.controlador.ProductoController
            productoController =
            new pe.ferreteria.controlador.ProductoController();

    pe.ferreteria.modelo.Producto producto = null;

    for (pe.ferreteria.modelo.Producto p
            : productoController.listar()) {

        if (p.getIdProducto()
                == detalleCompra.getIdProducto()) {

            producto = p;
            break;
        }
    }

    if (producto == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se encontró el producto para completar la recepción."
        );

        return false;
    }

    /*
     * Registrar Inventario + Recepción + Kardex
     * dentro de una sola transacción.
     */
    int idInventario =
            recepcionController
                    .completarRecepcionConInventarioYKardex(
                            compra,
                            detalleCompra,
                            producto,
                            detalleRecepcion,
                            idRecepcion,
                            idUsuario,
                            cantidadObjetivo
                    );

    if (idInventario == 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo completar la recepción."
        );

        return false;
    }

    int nuevoFaltante = faltante - cantidadAIngresar;
    String mensaje = "Se ingresaron " + cantidadAIngresar
            + " unidades al Inventario y al Kardex.";
    if (nuevoFaltante > 0) mensaje += "\nFaltan " + nuevoFaltante + " unidades por recibir.";
    javax.swing.JOptionPane.showMessageDialog(this, mensaje);

    actualizarInterfazDespuesDeCompletar();
    cargarTablaRecepcion();

    return true;
}
   private void actualizarEstadoRecepcionActual() {

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    if (recepcion == null) {
        return;
    }

    recepcionController.actualizarEstadoSegunDetalles(
            recepcion.getIdRecepcion()
    );
}
   private boolean validarCorreccionRecepcion() {

    pe.ferreteria.modelo.Usuario usuario =
            pe.ferreteria.infraestructura.SesionUsuario.getUsuarioActual();
    if (usuario == null || usuario.getRol() == null
            || !usuario.getRol().equalsIgnoreCase("Administrador")) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "Solo un administrador puede corregir recepciones.");
        return false;
    }

    if (!validarSeleccionRecepcion()) {
        return false;
    }

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    String estado =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 10)
                    .toString();

    if (!"Observada".equalsIgnoreCase(estado)) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Solo se puede corregir una recepción observada."
        );

        return false;
    }

    pe.ferreteria.modelo.DetalleCompra detalle =
            obtenerDetalleSeleccionado();

    if (detalle == null) {
        return false;
    }

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    if (recepcion == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se encontró la recepción."
        );

        return false;
    }

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    recepcion.getIdRecepcion(),
                    detalle.getIdDetalle()
            );

    if (detalleRecepcion == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se encontró el detalle de recepción."
        );

        return false;
    }

    if (!"Observada".equalsIgnoreCase(
            detalleRecepcion.getEstado())) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El detalle seleccionado no está observado."
        );

        return false;
    }

    if (detalleRecepcion.getCantidadRecibida() == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No existe una cantidad recibida para corregir."
        );

        return false;
    }

    if (detalleRecepcion.getCantidadDefectuosa() <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "El detalle no tiene unidades observadas para corregir.");
        return false;
    }

    int faltanBuenas = detalle.getCantidad()
            - detalleRecepcion.getCantidadDefectuosa()
            - detalleRecepcion.getCantidadRecibida();
    if (faltanBuenas > 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "Aún faltan " + faltanBuenas + " unidades buenas por recibir.");
        return false;
    }

    return true;
}

private boolean corregirProducto() {

    if (!validarCorreccionRecepcion()) {
        return false;
    }

    int idUsuario =
            pe.ferreteria.infraestructura.SesionUsuario.getIdUsuario();

    if (idUsuario == 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );
        return false;
    }

    pe.ferreteria.modelo.DetalleCompra detalleCompra =
            obtenerDetalleSeleccionado();

    pe.ferreteria.modelo.Compra compra = obtenerCompraSeleccionada();

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    recepcion.getIdRecepcion(),
                    detalleCompra.getIdDetalle()
            );

    if (detalleRecepcion == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "No se encontró el detalle de recepción.");
        return false;
    }

    pe.ferreteria.modelo.Producto producto = null;
    for (pe.ferreteria.modelo.Producto candidato :
            new pe.ferreteria.controlador.ProductoController().listar()) {
        if (candidato.getIdProducto() == detalleCompra.getIdProducto()) {
            producto = candidato;
            break;
        }
    }
    if (producto == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "No se encontró el producto.");
        return false;
    }

    javax.swing.JTextArea comentarioTecnico = new javax.swing.JTextArea(
            detalleRecepcion.getObservacion() == null ? "" : detalleRecepcion.getObservacion(), 3, 40);
    comentarioTecnico.setEditable(false);
    comentarioTecnico.setLineWrap(true);
    comentarioTecnico.setWrapStyleWord(true);
    javax.swing.JTextArea comentarioAdministrador = new javax.swing.JTextArea(3, 40);
    comentarioAdministrador.setLineWrap(true);
    comentarioAdministrador.setWrapStyleWord(true);
    javax.swing.JPanel panel = new javax.swing.JPanel();
    panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
    panel.add(new javax.swing.JLabel("Producto: " + producto.getNombre()
            + " (Lote " + detalleCompra.getLote() + ")"));
    panel.add(new javax.swing.JLabel("Unidades observadas: "
            + detalleRecepcion.getCantidadDefectuosa()));
    panel.add(javax.swing.Box.createVerticalStrut(8));
    panel.add(new javax.swing.JLabel("Observación del almacenero:"));
    panel.add(new javax.swing.JScrollPane(comentarioTecnico));
    panel.add(javax.swing.Box.createVerticalStrut(8));
    panel.add(new javax.swing.JLabel("Comentario del administrador (obligatorio, máximo 255 caracteres):"));
    panel.add(new javax.swing.JScrollPane(comentarioAdministrador));

    String[] opciones = {"Ingresar como stock bueno", "Ingresar como stock defectuoso", "Cancelar"};
    boolean ingresarComoBueno;
    String comentario;
    while (true) {
        int opcion = javax.swing.JOptionPane.showOptionDialog(this, panel,
                "Corregir recepción", javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[2]);
        if (opcion != 0 && opcion != 1) return false;
        comentario = comentarioAdministrador.getText().trim();
        if (comentario.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Escriba el comentario de la decisión.");
            continue;
        }
        if (comentario.length() > 255) {
            javax.swing.JOptionPane.showMessageDialog(this, "El comentario no puede superar 255 caracteres.");
            continue;
        }
        ingresarComoBueno = opcion == 0;
        break;
    }

    String resultado = recepcionController.corregirRecepcion(
            recepcion.getIdRecepcion(), compra, detalleCompra, producto,
            idUsuario, ingresarComoBueno, comentario);
    if (!"OK".equals(resultado)) {
        javax.swing.JOptionPane.showMessageDialog(this, resultado);
        return false;
    }
    javax.swing.JOptionPane.showMessageDialog(this,
            "Recepción corregida. Las unidades observadas se registraron como "
            + (ingresarComoBueno ? "stock bueno." : "stock defectuoso."));
    actualizarInterfazDespuesDeCompletar();
    cargarTablaRecepcion();
    limpiarProductoSeleccionado();
    return true;
}


   private void configurarAccesoAdministrador(String rolActual) {

    boolean esAdministrador =
            rolActual != null
            && rolActual.equalsIgnoreCase("Administrador");

    btnCorreguirRecepcion.setEnabled(esAdministrador);
}
   private void actualizarInterfazDespuesDeCompletar() {

    cargarComprasPendientes();

    if (comboComprobante.getSelectedIndex() == -1) {

        txtNombreProveedor.setText("");
        txtFechaDelComprobante.setText("");

        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel)
                        tableRecepcionDelComprobante.getModel();

        modelo.setRowCount(0);

        limpiarProductoSeleccionado();
    }
}
   private void ejecutarObservar() {

    observarProducto();
}
   private void ejecutarCompletar() {

    if (completarProducto()) {
        txtObservacion.setText("");
        txtCantidadObservada.setText("");
    }
}
   private void ejecutarCorregir() {

    if (corregirProducto()) {
        txtObservacion.setText("");
        txtCantidadObservada.setText("");
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        tableRecepcionDelComprobante = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        txtFechaDelComprobante = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtNombreProveedor = new javax.swing.JLabel();
        comboComprobante = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        txtBuscarProducto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboEstado = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtEscaner = new javax.swing.JTextField();
        lblProductoEscaneado = new javax.swing.JLabel();
        lblPresentacionEscaneada = new javax.swing.JLabel();
        lblCantidadRegistrada = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        txtCantidaddelProducto = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCantidadObservada = new javax.swing.JTextField();
        txtObservacion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnCompletarRecibido = new javax.swing.JButton();
        btnObservar = new javax.swing.JButton();
        txtProductoSeleccionado = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnCorreguirRecepcion = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 250, 242));

        tableRecepcionDelComprobante.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Lote", "Producto", "Categoria", "Marca", "Recibida", "Comprada", "Observada", "F. Venc.", "Estante", "Nivel", "Estado"
            }
        ));
        tableRecepcionDelComprobante.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRecepcionDelComprobanteMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableRecepcionDelComprobante);

        jPanel5.setBackground(new java.awt.Color(255, 250, 242));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RECEPCION DE MERCADERIA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18))); // NOI18N
        jPanel5.setToolTipText("");

        jLabel21.setText("Fecha");

        btnActualizar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        txtFechaDelComprobante.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtFechaDelComprobante.setText("__");

        jLabel9.setText("Comprobante");

        jLabel13.setText("Proveedor");

        txtNombreProveedor.setFont(new java.awt.Font("Arial", 0, 22)); // NOI18N
        txtNombreProveedor.setText("--");

        comboComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboComprobanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(comboComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(jLabel21)
                .addGap(44, 44, 44)
                .addComponent(txtFechaDelComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(140, 140, 140)
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(comboComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(txtNombreProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(44, 44, 44))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(txtFechaDelComprobante))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel6.setBackground(new java.awt.Color(255, 250, 242));

        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyReleased(evt);
            }
        });

        jLabel6.setText("Buscar Producto");

        jLabel5.setText("Estado");

        comboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Pendiente", "Observada", "Completada" }));
        comboEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEstadoActionPerformed(evt);
            }
        });

        jLabel12.setText("Escanear código");

        txtEscaner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEscanerActionPerformed(evt);
            }
        });

        lblProductoEscaneado.setText("--");

        lblPresentacionEscaneada.setText("--");

        lblCantidadRegistrada.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCantidadRegistrada.setText("-");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jLabel5)
                .addGap(32, 32, 32)
                .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEscaner, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductoEscaneado, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblPresentacionEscaneada, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(lblCantidadRegistrada, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtEscaner, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(lblProductoEscaneado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPresentacionEscaneada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCantidadRegistrada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(255, 250, 242));

        jLabel7.setText("Producto Seleccionado");

        jLabel8.setText("Observacion");

        jLabel10.setText("Cantidad Obserbada");

        btnCompletarRecibido.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCompletarRecibido.setText("COMPLETAR / RECIBIDO");
        btnCompletarRecibido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompletarRecibidoActionPerformed(evt);
            }
        });

        btnObservar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnObservar.setText("OBSERVAR");
        btnObservar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObservarActionPerformed(evt);
            }
        });

        jLabel11.setText("Cantidad ");

        btnCorreguirRecepcion.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCorreguirRecepcion.setText("CORREGIR RECEPCION");
        btnCorreguirRecepcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorreguirRecepcionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnCompletarRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(215, 215, 215)
                        .addComponent(btnCorreguirRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnObservar, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(178, 178, 178))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProductoSeleccionado)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantidaddelProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtCantidadObservada, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(txtCantidaddelProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCantidadObservada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtProductoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCompletarRecibido)
                    .addComponent(btnObservar)
                    .addComponent(btnCorreguirRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 9, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1471, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
actualizarListaComprobantes();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void comboEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboEstadoActionPerformed
filtrarTablaRecepcion();        // TODO add your handling code here:
    }//GEN-LAST:event_comboEstadoActionPerformed

    private void btnCompletarRecibidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompletarRecibidoActionPerformed
   ejecutarCompletar();     // TODO add your handling code here:
    }//GEN-LAST:event_btnCompletarRecibidoActionPerformed

    private void btnObservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObservarActionPerformed
ejecutarObservar();        // TODO add your handling code here:
    }//GEN-LAST:event_btnObservarActionPerformed

    private void btnCorreguirRecepcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorreguirRecepcionActionPerformed
  ejecutarCorregir();      // TODO add your handling code here:
    }//GEN-LAST:event_btnCorreguirRecepcionActionPerformed

    private void comboComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboComprobanteActionPerformed
actualizarComprobanteSeleccionado();        // TODO add your handling code here:
    }//GEN-LAST:event_comboComprobanteActionPerformed

    private void txtBuscarProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyReleased
filtrarTablaRecepcion();        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductoKeyReleased

    private void txtEscanerActionPerformed(java.awt.event.ActionEvent evt) {
        if (temporizadorEscaner != null) temporizadorEscaner.stop();
        procesarCodigoEscaneadoSeguro();
    }

    private void tableRecepcionDelComprobanteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRecepcionDelComprobanteMouseClicked
cargarProductoSeleccionado();        // TODO add your handling code here:
    }//GEN-LAST:event_tableRecepcionDelComprobanteMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCompletarRecibido;
    private javax.swing.JButton btnCorreguirRecepcion;
    private javax.swing.JButton btnObservar;
    private javax.swing.JComboBox<String> comboComprobante;
    private javax.swing.JComboBox<String> comboEstado;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCantidadRegistrada;
    private javax.swing.JLabel lblPresentacionEscaneada;
    private javax.swing.JLabel lblProductoEscaneado;
    private javax.swing.JTable tableRecepcionDelComprobante;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCantidadObservada;
    private javax.swing.JTextField txtCantidaddelProducto;
    private javax.swing.JTextField txtEscaner;
    private javax.swing.JLabel txtFechaDelComprobante;
    private javax.swing.JLabel txtNombreProveedor;
    private javax.swing.JTextField txtObservacion;
    private javax.swing.JTextField txtProductoSeleccionado;
    // End of variables declaration//GEN-END:variables
}
