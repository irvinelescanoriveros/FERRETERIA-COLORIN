
package pe.ferreteria.vista;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.controlador.CompraController;
import pe.ferreteria.controlador.MarcaController;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.controlador.ProveedorController;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Proveedor;

public class CompraPanel extends javax.swing.JPanel {
private final ProveedorController proveedorController =
        new ProveedorController();

private final ProductoController productoController =
        new ProductoController();

private List<Proveedor> listaProveedores =
        new ArrayList<>();

private List<Producto> listaProductos =
        new ArrayList<>();
private final MarcaController marcaController =
        new MarcaController();

private final CategoriaController categoriaController =
        new CategoriaController();

private List<Marca> listaMarcas =
        new ArrayList<>();

private List<Categoria> listaCategorias =
        new ArrayList<>();
  
    public CompraPanel() {
    initComponents();
    cargarProveedoresYProductos();
    cargarFechaCompraActual();
    actualizarEstadoFechaVencimiento();
    limpiarFilasInicialesTabla();
}
private void cargarProveedoresYProductos() {

    listaProveedores = proveedorController.listar();
    listaProductos = productoController.listar();
    listaMarcas = marcaController.listar();
    listaCategorias = categoriaController.listar();

    comboBuscarNombreEmpresa.removeAllItems();
    comboBuscarNombreProducto.removeAllItems();

    for (Proveedor proveedor : listaProveedores) {
        comboBuscarNombreEmpresa.addItem(
                proveedor.getEmpresa()
        );
    }

    for (Producto producto : listaProductos) {
        comboBuscarNombreProducto.addItem(
                producto.getNombre()
        );
    }
}
private void cargarDatosProveedorSeleccionado() {

    int indice = comboBuscarNombreEmpresa.getSelectedIndex();

    if (indice < 0 || indice >= listaProveedores.size()) {
        return;
    }

    Proveedor proveedor = listaProveedores.get(indice);

    txtEmpresa.setText(proveedor.getEmpresa());
    txtRUC.setText(proveedor.getRuc());
    txtContacto.setText(proveedor.getContacto());
}
private void cargarDatosProductoSeleccionado() {

    int indice = comboBuscarNombreProducto.getSelectedIndex();

    if (indice < 0 || indice >= listaProductos.size()) {
        return;
    }

    Producto producto = listaProductos.get(indice);

    txtUnidadMedida.setText(producto.getUnidad());

    for (Marca marca : listaMarcas) {
        if (marca.getIdMarca() == producto.getIdMarca()) {
            txtMarca.setText(marca.getNombre());
            break;
        }
    }

    for (Categoria categoria : listaCategorias) {
        if (categoria.getIdCategoria() == producto.getIdCategoria()) {
            txtCategoria.setText(categoria.getNombre());
            break;
        }
    }
}
 private void actualizarEstadoFechaVencimiento() {

    boolean sinVencimiento = rbSinVencimineto.isSelected();

    comboDia.setEnabled(!sinVencimiento);
    comboMes.setEnabled(!sinVencimiento);
    comboAnio.setEnabled(!sinVencimiento);

    if (sinVencimiento) {
        comboDia.setSelectedIndex(-1);
        comboMes.setSelectedIndex(-1);
        comboAnio.setSelectedIndex(-1);
    }
}
 private java.time.LocalDate obtenerFechaVencimiento() {

    if (rbSinVencimineto.isSelected()) {
        return null;
    }

    if (comboDia.getSelectedIndex() == -1
            || comboMes.getSelectedIndex() == -1
            || comboAnio.getSelectedIndex() == -1) {

        return null;
    }

    int dia = Integer.parseInt(
            comboDia.getSelectedItem().toString()
    );

    int mes = comboMes.getSelectedIndex() + 1;

    int anio = Integer.parseInt(
            comboAnio.getSelectedItem().toString()
    );

    try {

        return java.time.LocalDate.of(
                anio,
                mes,
                dia
        );

    } catch (java.time.DateTimeException e) {

        return null;
    }
}
 private java.math.BigDecimal calcularSubtotal() {

    int cantidad = Integer.parseInt(
            txtCantidad.getText().trim()
    );

    java.math.BigDecimal precio =
            new java.math.BigDecimal(
                    txtPrecioCompra.getText().trim()
            );

    return precio.multiply(
            java.math.BigDecimal.valueOf(cantidad)
    );
}
 
 private void actualizarTotalCompra() {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    java.math.BigDecimal total =
            java.math.BigDecimal.ZERO;

    for (int i = 0; i < modelo.getRowCount(); i++) {

        Object valor = modelo.getValueAt(i, 5);

        if (valor != null && !valor.toString().trim().isEmpty()) {

            java.math.BigDecimal subtotal =
                    new java.math.BigDecimal(
                            valor.toString()
                    );

            total = total.add(subtotal);
        }
    }

    txtTotal.setText(
            total.setScale(2).toString()
    );
}
 private void limpiarCamposProducto() {

    comboBuscarNombreProducto.setSelectedIndex(-1);

    txtUnidadMedida.setText("");
    txtMarca.setText("");
    txtCategoria.setText("");

    txtCantidad.setText("");
    txtPrecioCompra.setText("");

    comboDia.setSelectedIndex(-1);
    comboMes.setSelectedIndex(-1);
    comboAnio.setSelectedIndex(-1);

    rbSinVencimineto.setSelected(false);

    comboDia.setEnabled(true);
    comboMes.setEnabled(true);
    comboAnio.setEnabled(true);
}
 private void limpiarCompraCompleta() {

    comboBuscarNombreEmpresa.setSelectedIndex(-1);

    txtEmpresa.setText("");
    txtRUC.setText("");
    txtContacto.setText("");

    txtNcomprobante.setText("");

    txtFechaCompra.setText(
            java.time.LocalDate.now().toString()
    );

    limpiarCamposProducto();

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    modelo.setRowCount(0);

    txtTotal.setText("0.00");
}

private void agregarProductoATabla() {

    if (!validarProductoCompra()) {
        return;
    }

    int indiceProducto =
            comboBuscarNombreProducto.getSelectedIndex();

    if (indiceProducto < 0
            || indiceProducto >= listaProductos.size()) {

        return;
    }

    Producto producto =
            listaProductos.get(indiceProducto);

    int cantidad = Integer.parseInt(
            txtCantidad.getText().trim()
    );

    java.math.BigDecimal precioCompra =
            new java.math.BigDecimal(
                    txtPrecioCompra.getText().trim()
            );

    java.math.BigDecimal subtotal =
            calcularSubtotal();

    java.time.LocalDate fechaVencimiento =
            obtenerFechaVencimiento();

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    String fechaMostrar =
            fechaVencimiento == null
                    ? "Sin vencimiento"
                    : fechaVencimiento.toString();

    for (int i = 0; i < modelo.getRowCount(); i++) {

        Object valorId =
                modelo.getValueAt(i, 0);

        // Ignorar filas vacías de la tabla
        if (valorId == null) {
            continue;
        }

        int idProductoTabla =
                Integer.parseInt(
                        valorId.toString()
                );

        Object valorFecha =
                modelo.getValueAt(i, 2);

        String fechaTabla =
                valorFecha == null
                        ? "Sin vencimiento"
                        : valorFecha.toString();

        if (idProductoTabla == producto.getIdProducto()
                && fechaTabla.equals(fechaMostrar)) {

            int cantidadActual =
                    Integer.parseInt(
                            modelo.getValueAt(i, 3).toString()
                    );

            java.math.BigDecimal precioActual =
                    new java.math.BigDecimal(
                            modelo.getValueAt(i, 4).toString()
                    );

            // Si el precio es diferente, no mezclamos las filas
            if (precioActual.compareTo(precioCompra) != 0) {
                continue;
            }

            int nuevaCantidad =
                    cantidadActual + cantidad;

            java.math.BigDecimal nuevoSubtotal =
                    precioCompra.multiply(
                            java.math.BigDecimal.valueOf(
                                    nuevaCantidad
                            )
                    );

            modelo.setValueAt(
                    nuevaCantidad,
                    i,
                    3
            );

            modelo.setValueAt(
                    nuevoSubtotal,
                    i,
                    5
            );

            actualizarTotalCompra();

            limpiarCamposProducto();

            return;
        }
    }

    // Agregar nueva fila
    modelo.addRow(new Object[]{
        producto.getIdProducto(),
        producto.getNombre(),
        fechaMostrar,
        cantidad,
        precioCompra,
        subtotal
    });

    actualizarTotalCompra();

    limpiarCamposProducto();
}
 
private void eliminarProductoSeleccionado() {

    int filaSeleccionada =
            TablaResumenCompra.getSelectedRow();

    if (filaSeleccionada == -1) {
        return;
    }

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    modelo.removeRow(filaSeleccionada);

    actualizarTotalCompra();
}

private boolean validarProductoCompra() {

    if (comboBuscarNombreProducto.getSelectedIndex() == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un producto."
        );

        return false;
    }

    if (txtCantidad.getText().trim().isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese la cantidad."
        );

        txtCantidad.requestFocus();
        return false;
    }

    if (txtPrecioCompra.getText().trim().isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese el precio de compra."
        );

        txtPrecioCompra.requestFocus();
        return false;
    }

    // Validar cantidad
    try {

        int cantidad = Integer.parseInt(
                txtCantidad.getText().trim()
        );

        if (cantidad <= 0) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe ser mayor que cero."
            );

            txtCantidad.requestFocus();
            return false;
        }

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser un número entero."
        );

        txtCantidad.requestFocus();
        return false;
    }

    // Validar precio
    try {

        java.math.BigDecimal precio =
                new java.math.BigDecimal(
                        txtPrecioCompra.getText().trim()
                );

        if (precio.compareTo(
                java.math.BigDecimal.ZERO) <= 0) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "El precio debe ser mayor que cero."
            );

            txtPrecioCompra.requestFocus();
            return false;
        }

    } catch (NumberFormatException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El precio debe ser un número válido."
        );

        txtPrecioCompra.requestFocus();
        return false;
    }

    // Validar fecha de vencimiento
    if (!rbSinVencimineto.isSelected()) {

        if (comboDia.getSelectedIndex() == -1
                || comboMes.getSelectedIndex() == -1
                || comboAnio.getSelectedIndex() == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una fecha de vencimiento completa."
            );

            return false;
        }

        if (obtenerFechaVencimiento() == null) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "La fecha de vencimiento no es válida."
            );

            return false;
        }
    }

    return true;
}

private boolean validarCompra() {

    if (!validarSesionUsuario()) {
        return false;
    }

    if (comboBuscarNombreEmpresa.getSelectedIndex() == -1) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un proveedor."
        );

        return false;
    }

    if (!validarFechaCompra()) {
        return false;
    }

    if (txtNcomprobante.getText().trim().isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese el número de comprobante."
        );

        txtNcomprobante.requestFocus();
        return false;
    }
    if (!validarComprobanteNoRepetido()) {
    return false;
}

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    if (modelo.getRowCount() == 0) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Agregue al menos un producto a la compra."
        );

        return false;
    }

    return true;
}

private List<DetalleCompra> crearDetallesCompraDesdeTabla() {

    List<DetalleCompra> detalles =
            new ArrayList<>();

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    for (int i = 0; i < modelo.getRowCount(); i++) {

        int idProducto =
                Integer.parseInt(
                        modelo.getValueAt(i, 0).toString()
                );

        int cantidad =
                Integer.parseInt(
                        modelo.getValueAt(i, 3).toString()
                );

        java.math.BigDecimal precioCompra =
                new java.math.BigDecimal(
                        modelo.getValueAt(i, 4).toString()
                );

        String textoFecha =
                modelo.getValueAt(i, 2).toString();

        java.time.LocalDate fechaVencimiento = null;

        if (!textoFecha.equals("Sin vencimiento")) {

            fechaVencimiento =
                    java.time.LocalDate.parse(
                            textoFecha
                    );
        }

        DetalleCompra detalle =
                new DetalleCompra();

        detalle.setIdProducto(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioCompra(precioCompra);
        detalle.setFechaVencimiento(
                fechaVencimiento
        );

        detalles.add(detalle);
    }

    return detalles;
}

private Compra crearCompra() {

    int indiceProveedor =
            comboBuscarNombreEmpresa.getSelectedIndex();

    Proveedor proveedor =
            listaProveedores.get(indiceProveedor);

    java.time.LocalDate fecha =
            java.time.LocalDate.parse(
                    txtFechaCompra.getText().trim()
            );

    Compra compra = new Compra();

    compra.setIdProveedor(
            proveedor.getIdProveedor()
    );

    compra.setIdUsuario(
            pe.ferreteria.infraestructura.SesionUsuario
                    .getIdUsuario()
    );

    compra.setFechaCompra(
            fecha.atStartOfDay()
    );

    compra.setNroComprobante(
            txtNcomprobante.getText().trim()
    );

    compra.setTotal(
            new java.math.BigDecimal(
                    txtTotal.getText().trim()
            )
    );

    compra.setActivo(true);

    return compra;
}
private boolean validarSesionUsuario() {

    if (pe.ferreteria.infraestructura.SesionUsuario
            .getUsuarioActual() == null) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay un usuario con sesión iniciada."
        );

        return false;
    }

    return true;
}

private boolean registrarCompra() {

    if (!validarCompra()) {
        return false;
    }

    Compra compra = crearCompra();

    List<DetalleCompra> detalles =
            crearDetallesCompraDesdeTabla();

    CompraController compraController =
            new CompraController();

    boolean registrado =
            compraController.registrarCompra(
                    compra,
                    detalles
            );

    if (registrado) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Compra registrada correctamente."
        );

        limpiarCompraCompleta();

        return true;
    }

    javax.swing.JOptionPane.showMessageDialog(
            this,
            "No se pudo registrar la compra."
    );

    return false;
}
private void cargarFechaCompraActual() {

    txtFechaCompra.setText(
            java.time.LocalDate.now().toString()
    );
}
private boolean validarFechaCompra() {

    String textoFecha =
            txtFechaCompra.getText().trim();

    if (textoFecha.isEmpty()) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Ingrese la fecha de compra."
        );

        txtFechaCompra.requestFocus();
        return false;
    }

    try {

        java.time.LocalDate.parse(textoFecha);

        return true;

    } catch (DateTimeException e) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "La fecha de compra no es válida."
        );

        txtFechaCompra.requestFocus();
        return false;
    }
}
private void actualizarDatosCompra() {

    listaProveedores =
            proveedorController.listar();

    listaProductos =
            productoController.listar();

    listaMarcas =
            marcaController.listar();

    listaCategorias =
            categoriaController.listar();

    comboBuscarNombreEmpresa.removeAllItems();
    comboBuscarNombreProducto.removeAllItems();

    for (Proveedor proveedor : listaProveedores) {

        comboBuscarNombreEmpresa.addItem(
                proveedor.getEmpresa()
        );
    }

    for (Producto producto : listaProductos) {

        comboBuscarNombreProducto.addItem(
                producto.getNombre()
        );
    }
}
private void cancelarCompra() {

    limpiarCompraCompleta();

    cargarFechaCompraActual();
}
private boolean validarComprobanteNoRepetido() {

    String nroComprobante =
            txtNcomprobante.getText().trim();

    if (nroComprobante.isEmpty()) {
        return true;
    }

    CompraController compraController =
            new CompraController();

    if (compraController.existeNroComprobante(
            nroComprobante)) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "El número de comprobante ya está registrado."
        );

        txtNcomprobante.requestFocus();
        return false;
    }

    return true;
}
private void limpiarFilasInicialesTabla() {

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel)
                    TablaResumenCompra.getModel();

    modelo.setRowCount(0);
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        comboBuscarNombreEmpresa = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtEmpresa = new javax.swing.JTextField();
        txtRUC = new javax.swing.JTextField();
        txtContacto = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        comboBuscarNombreProducto = new javax.swing.JComboBox<>();
        txtUnidadMedida = new javax.swing.JTextField();
        txtCategoria = new javax.swing.JTextField();
        txtMarca = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaResumenCompra = new javax.swing.JTable();
        btnEliminarProductoTabla = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtNcomprobante = new javax.swing.JTextField();
        btnLimpiar = new javax.swing.JToggleButton();
        btnAgregarATablaResumen = new javax.swing.JButton();
        btnGuardarCompra = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        txtFechaCompra = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JToggleButton();
        jPanel8 = new javax.swing.JPanel();
        rbSinVencimineto = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        comboAnio = new javax.swing.JComboBox<>();
        comboMes = new javax.swing.JComboBox<>();
        comboDia = new javax.swing.JComboBox<>();
        txtPrecioCompra = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 250, 242));

        jPanel2.setBackground(new java.awt.Color(255, 243, 224));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Proveedor"));

        comboBuscarNombreEmpresa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBuscarNombreEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBuscarNombreEmpresaActionPerformed(evt);
            }
        });

        jLabel1.setText("Buscador");

        jLabel8.setText("Empresa");

        jLabel9.setText("RUC");

        jLabel10.setText("Contacto");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBuscarNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboBuscarNombreEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 243, 224));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Producto"));

        jLabel3.setText("Buscador:");

        comboBuscarNombreProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBuscarNombreProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBuscarNombreProductoActionPerformed(evt);
            }
        });

        jLabel11.setText("Unid Medida");

        jLabel12.setText("Marca");

        jLabel13.setText("Categoria");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBuscarNombreProducto, 0, 282, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboBuscarNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 250, 242));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen Compra"));

        TablaResumenCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Producto", "F.  Vencimiento", "Cantidad", "Precio U.", "Sub Total"
            }
        ));
        jScrollPane1.setViewportView(TablaResumenCompra);

        btnEliminarProductoTabla.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnEliminarProductoTabla.setText("Eliminar producto en la tabla");
        btnEliminarProductoTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoTablaActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("N° de comprobante");

        txtNcomprobante.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        btnLimpiar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnLimpiar.setText("Limpiar campos");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnAgregarATablaResumen.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnAgregarATablaResumen.setText("Agregar a Resumen");
        btnAgregarATablaResumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarATablaResumenActionPerformed(evt);
            }
        });

        btnGuardarCompra.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnGuardarCompra.setText("REGISTRAR COMPRA");
        btnGuardarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCompraActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel4.setText("Total Compra");

        txtTotal.setBackground(new java.awt.Color(255, 243, 224));
        txtTotal.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        txtTotal.setText("------");

        txtFechaCompra.setEditable(false);

        jLabel2.setText("Fecha de Compra");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtNcomprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                                .addComponent(txtFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(116, 116, 116))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addGap(131, 131, 131)))
                        .addComponent(btnAgregarATablaResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(btnEliminarProductoTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(637, 637, 637)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(btnGuardarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNcomprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnEliminarProductoTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregarATablaResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btnGuardarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(255, 243, 224));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Sin fecha"));

        rbSinVencimineto.setText("Sin vencimiento");
        rbSinVencimineto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSinVenciminetoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(rbSinVencimineto)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(rbSinVencimineto)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 243, 224));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha de Venc    Dia  -  Mes  -  Año"));

        comboAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040" }));

        comboMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        comboDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", " " }));
        comboDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDiaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboMes, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(comboAnio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtPrecioCompra.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel6.setText("P. Compra");

        txtCantidad.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel5.setText("Cantidad");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel6)
                        .addGap(85, 85, 85)
                        .addComponent(jLabel5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPrecioCompra))))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboBuscarNombreEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBuscarNombreEmpresaActionPerformed
       cargarDatosProveedorSeleccionado();
    }//GEN-LAST:event_comboBuscarNombreEmpresaActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarDatosCompra();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void comboBuscarNombreProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBuscarNombreProductoActionPerformed
cargarDatosProductoSeleccionado();      
    }//GEN-LAST:event_comboBuscarNombreProductoActionPerformed

    private void comboDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDiaActionPerformed
      
    }//GEN-LAST:event_comboDiaActionPerformed

    private void btnEliminarProductoTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoTablaActionPerformed
         eliminarProductoSeleccionado();
    }//GEN-LAST:event_btnEliminarProductoTablaActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCamposProducto();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnAgregarATablaResumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarATablaResumenActionPerformed
        agregarProductoATabla();
    }//GEN-LAST:event_btnAgregarATablaResumenActionPerformed

    private void btnGuardarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCompraActionPerformed
        registrarCompra();
    }//GEN-LAST:event_btnGuardarCompraActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
       cancelarCompra();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void rbSinVenciminetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSinVenciminetoActionPerformed
      actualizarEstadoFechaVencimiento();  // TODO add your handling code here:
    }//GEN-LAST:event_rbSinVenciminetoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaResumenCompra;
    private javax.swing.JToggleButton btnActualizar;
    private javax.swing.JButton btnAgregarATablaResumen;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminarProductoTabla;
    private javax.swing.JButton btnGuardarCompra;
    private javax.swing.JToggleButton btnLimpiar;
    private javax.swing.JComboBox<String> comboAnio;
    private javax.swing.JComboBox<String> comboBuscarNombreEmpresa;
    private javax.swing.JComboBox<String> comboBuscarNombreProducto;
    private javax.swing.JComboBox<String> comboDia;
    private javax.swing.JComboBox<String> comboMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbSinVencimineto;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCategoria;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtEmpresa;
    private javax.swing.JTextField txtFechaCompra;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtNcomprobante;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtRUC;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JTextField txtUnidadMedida;
    // End of variables declaration//GEN-END:variables
}
