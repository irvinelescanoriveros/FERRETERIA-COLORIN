package pe.ferreteria.vista;

public class RecepcionPanel extends javax.swing.JPanel {
private final pe.ferreteria.controlador.RecepcionController recepcionController =
        new pe.ferreteria.controlador.RecepcionController();

private final pe.ferreteria.controlador.ProveedorController proveedorController =
        new pe.ferreteria.controlador.ProveedorController();
    public RecepcionPanel() {
        initComponents();
        inicializarRecepcion();
    }

    private void inicializarRecepcion() {

    configurarTablaRecepcion();
    cargarComprasPendientes();

    txtNombreProveedor.setText("");
    txtFechaDelComprobante.setText("");

    limpiarProductoSeleccionado();
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
                "Cant.",
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

        int cantidadMostrar = detalle.getCantidad();

        for (pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion
                : detallesRecepcion) {

            if (detalleRecepcion.getIdDetalleCompra()
                    == detalle.getIdDetalle()) {

                if (detalleRecepcion.getEstado() != null) {
                    estado = detalleRecepcion.getEstado();
                }

                if (detalleRecepcion.getCantidadRecibida() != null) {
                    cantidadMostrar =
                            detalleRecepcion.getCantidadRecibida();
                }

                break;
            }
        }

        modelo.addRow(new Object[]{
            detalle.getLote(),
            productoEncontrado.getNombre(),
            nombreCategoria,
            nombreMarca,
            cantidadMostrar,
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
                    .getValueAt(fila, 4)
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

        if (detalleRecepcion.getCantidadRecibida() != null) {
            txtCantidadObservada.setText(
                    String.valueOf(
                            detalleRecepcion.getCantidadRecibida()
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
                "Ingrese la cantidad recibida."
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
                "La cantidad recibida debe ser mayor que cero."
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
                modelo.getValueAt(fila, 8).toString();

        boolean coincideTexto =
                textoBusqueda.isEmpty()
                || lote.contains(textoBusqueda)
                || producto.contains(textoBusqueda)
                || categoria.contains(textoBusqueda)
                || marca.contains(textoBusqueda);

        boolean coincideEstado =
                estadoSeleccionado.isEmpty()
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
                    .getValueAt(fila, 8)
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

    Integer cantidadRecibida =
            obtenerCantidadObservadaValidada();

    if (cantidadRecibida == null) {
        return false;
    }

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    idRecepcion,
                    detalleCompra.getIdDetalle()
            );

    if (detalleRecepcion == null) {

        detalleRecepcion =
                new pe.ferreteria.modelo.DetalleRecepcion();

        detalleRecepcion.setIdRecepcion(idRecepcion);

        detalleRecepcion.setIdDetalleCompra(
                detalleCompra.getIdDetalle()
        );
    }

    detalleRecepcion.setEstado("Observada");

    detalleRecepcion.setFechaConfirmacion(
            java.time.LocalDateTime.now()
    );

    detalleRecepcion.setCantidadRecibida(
            cantidadRecibida
    );

    detalleRecepcion.setObservacion(
            txtObservacion.getText().trim()
    );

    boolean resultado;

    if (detalleRecepcion.getIdDetalleRecepcion() == 0) {

        resultado =
                recepcionController.registrarDetalle(
                        detalleRecepcion
                );

    } else {

        resultado =
                recepcionController.actualizarDetalle(
                        detalleRecepcion
                );
    }

    if (!resultado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar la observación."
        );

        return false;
    }

    actualizarEstadoRecepcionActual();

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
                    .getValueAt(fila, 8)
                    .toString();

    if ("Completada".equalsIgnoreCase(estado)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este producto ya está completado."
        );
        return false;
    }

    if ("Observada".equalsIgnoreCase(estado)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Este producto tiene una recepción observada. Debe corregirla antes de completarla."
        );
        return false;
    }

    String observacion =
            txtObservacion.getText().trim();

    if (!observacion.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Existe una observación. Debe observar la recepción antes de completar."
        );
        return false;
    }

    pe.ferreteria.modelo.DetalleCompra detalle =
            obtenerDetalleSeleccionado();

    if (detalle == null) {
        return false;
    }

    String textoCantidad =
            txtCantidadObservada.getText().trim();

    if (!textoCantidad.isEmpty()) {

        Integer cantidadIngresada =
                obtenerCantidadObservadaValidada();

        if (cantidadIngresada == null) {
            return false;
        }

        if (cantidadIngresada != detalle.getCantidad()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "La cantidad recibida no coincide con la cantidad comprada. Debe observar la recepción."
            );
            return false;
        }
    }

    return true;
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

    int idRecepcion =
            obtenerOCrearRecepcion(
                    idUsuario
            );

    if (idRecepcion == 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo obtener la recepción."
        );

        return false;
    }

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    idRecepcion,
                    detalleCompra.getIdDetalle()
            );

    if (detalleRecepcion == null) {

        detalleRecepcion =
                new pe.ferreteria.modelo.DetalleRecepcion();

        detalleRecepcion.setIdRecepcion(
                idRecepcion
        );

        detalleRecepcion.setIdDetalleCompra(
                detalleCompra.getIdDetalle()
        );
    }

    /*
     * La cantidad recibida al completar debe ser
     * igual a la cantidad comprada.
     */
    int cantidadRecibida =
            detalleCompra.getCantidad();

    detalleRecepcion.setEstado(
            "Completada"
    );

    detalleRecepcion.setFechaConfirmacion(
            java.time.LocalDateTime.now()
    );

    detalleRecepcion.setCantidadRecibida(
            cantidadRecibida
    );

    detalleRecepcion.setObservacion(
            null
    );

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
                            cantidadRecibida
                    );

    if (idInventario == 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo completar la recepción."
        );

        return false;
    }

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Producto recibido correctamente,\n"
            + "registrado en Inventario y en Kardex."
    );

    actualizarInterfazDespuesDeCompletar();

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

    if (!validarSeleccionRecepcion()) {
        return false;
    }

    int fila = tableRecepcionDelComprobante.getSelectedRow();

    String estado =
            tableRecepcionDelComprobante
                    .getValueAt(fila, 8)
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

    pe.ferreteria.modelo.Recepcion recepcion =
            obtenerRecepcionActual();

    pe.ferreteria.modelo.DetalleRecepcion detalleRecepcion =
            recepcionController.buscarDetalleRecepcion(
                    recepcion.getIdRecepcion(),
                    detalleCompra.getIdDetalle()
            );

    if (detalleRecepcion == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se encontró el detalle de recepción."
        );
        return false;
    }

    Integer cantidadObservada =
            detalleRecepcion.getCantidadRecibida();

    if (cantidadObservada == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No existe una cantidad observada para corregir."
        );
        return false;
    }

    int respuesta = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "¿Desea corregir la cantidad de recepción?\n"
            + "La cantidad observada ("
            + cantidadObservada
            + ") será tomada como la cantidad recibida.",
            "Confirmar corrección",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE
    );

    if (respuesta != javax.swing.JOptionPane.YES_OPTION) {
        return false;
    }

    /*
     * La corrección NO completa la recepción.
     * La deja nuevamente en estado Pendiente.
     */
    detalleRecepcion.setEstado("Pendiente");

    detalleRecepcion.setFechaConfirmacion(
            null
    );

    /*
     * Se conserva la cantidad observada.
     * Ejemplo: 20 compradas → 18 recibidas → 18 queda.
     */
    detalleRecepcion.setCantidadRecibida(
            cantidadObservada
    );

    detalleRecepcion.setObservacion(null);

    if (!recepcionController.actualizarDetalle(
            detalleRecepcion)) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se pudo corregir la recepción."
        );

        return false;
    }

    recepcionController.actualizarEstadoSegunDetalles(
            recepcion.getIdRecepcion()
    );

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "Recepción corregida correctamente.\n"
            + "Cantidad recibida: "
            + cantidadObservada
            + "\n\nLa recepción quedó pendiente de confirmación."
    );

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
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Lote", "Producto", "Categoria", "Marca", "Cant.", "F. Venc.", "Estante", "Nivel", "Estado"
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

        comboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Observada", "Completada" }));
        comboEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEstadoActionPerformed(evt);
            }
        });

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
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 920, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
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
    private javax.swing.JTable tableRecepcionDelComprobante;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCantidadObservada;
    private javax.swing.JTextField txtCantidaddelProducto;
    private javax.swing.JLabel txtFechaDelComprobante;
    private javax.swing.JLabel txtNombreProveedor;
    private javax.swing.JTextField txtObservacion;
    private javax.swing.JTextField txtProductoSeleccionado;
    // End of variables declaration//GEN-END:variables
}
