
package pe.ferreteria.vista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.controlador.MarcaController;

import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Marca;

public class ProductoPanel extends javax.swing.JPanel {
private final ProductoController productoController =
        new ProductoController();

private final CategoriaController categoriaController =
        new CategoriaController();

private final MarcaController marcaController =
        new MarcaController();
    private java.util.List<Producto> listaProductos =
            new java.util.ArrayList<>();
  private java.util.List<Categoria> listaCategorias =
        new java.util.ArrayList<>();

private java.util.List<Marca> listaMarcas =
        new java.util.ArrayList<>();  
   
    public ProductoPanel() {
        initComponents();
        inicializarPanel();
    }
    private void inicializarPanel() {
    cargarCategorias();
    cargarMarcas();
    cargarProductos();
    limpiarCampos();
}
private void cargarCategorias() {

    listaCategorias = categoriaController.listar();

    comboCategoria.removeAllItems();

    for (Categoria categoria : listaCategorias) {
        comboCategoria.addItem(categoria.getNombre());
    }
}

private void cargarMarcas() {

    listaMarcas = marcaController.listar();

    comboMarca.removeAllItems();

    for (Marca marca : listaMarcas) {
        comboMarca.addItem(marca.getNombre());
    }
}
private void cargarProductos() {

    listaProductos = productoController.listar();

    DefaultTableModel modelo =
            (DefaultTableModel) tablaProductos.getModel();

    modelo.setRowCount(0);

    java.util.List<Categoria> categorias =
            categoriaController.listar();

    java.util.List<Marca> marcas =
            marcaController.listar();

    for (Producto producto : listaProductos) {

        String nombreCategoria = "";
        String nombreMarca = "";

        for (Categoria categoria : categorias) {

            if (categoria.getIdCategoria()
                    == producto.getIdCategoria()) {

                nombreCategoria = categoria.getNombre();
                break;
            }
        }

        for (Marca marca : marcas) {

            if (marca.getIdMarca()
                    == producto.getIdMarca()) {

                nombreMarca = marca.getNombre();
                break;
            }
        }

        modelo.addRow(new Object[]{
    producto.getIdProducto(),
    producto.getCodigo(),
    producto.getNombre(),
    nombreCategoria,
    nombreMarca,
    producto.getUnidad(),
    producto.getEstante(),
    producto.getNivel(),
    producto.getDescripcion()
});
    }
}
private void limpiarCampos() {

    txtCodigoBarras.setText("");
    txtNombre.setText("");
    txtEstante.setText("");
    txtNivel.setText("");
    txtDescripcion.setText("");

    if (comboUnidad.getItemCount() > 0) {
        comboUnidad.setSelectedIndex(0);
    }

    if (comboCategoria.getItemCount() > 0) {
        comboCategoria.setSelectedIndex(0);
    }

    if (comboMarca.getItemCount() > 0) {
        comboMarca.setSelectedIndex(0);
    }

    txtCodigoBarras.setEditable(true);

    tablaProductos.clearSelection();

    txtCodigoBarras.requestFocus();
}

private Producto obtenerProductoDesdeFormulario() {

    Producto producto = new Producto();

    producto.setCodigo(txtCodigoBarras.getText().trim());
    producto.setNombre(txtNombre.getText().trim());

    String nombreCategoria =
            comboCategoria.getSelectedItem() != null
                    ? comboCategoria.getSelectedItem().toString()
                    : "";

    String nombreMarca =
            comboMarca.getSelectedItem() != null
                    ? comboMarca.getSelectedItem().toString()
                    : "";

    for (Categoria categoria : categoriaController.listar()) {

        if (categoria.getNombre().equals(nombreCategoria)) {
            producto.setIdCategoria(categoria.getIdCategoria());
            break;
        }
    }

    for (Marca marca : marcaController.listar()) {

        if (marca.getNombre().equals(nombreMarca)) {
            producto.setIdMarca(marca.getIdMarca());
            break;
        }
    }

    producto.setUnidad(
            comboUnidad.getSelectedItem() != null
                    ? comboUnidad.getSelectedItem().toString()
                    : ""
    );

    producto.setEstante(txtEstante.getText().trim());
    producto.setNivel(txtNivel.getText().trim());
    producto.setDescripcion(txtDescripcion.getText().trim());
    producto.setActivo(true);

    return producto;
}

private boolean validarCamposObligatorios() {

    if (txtCodigoBarras.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el código del producto.");
        txtCodigoBarras.requestFocus();
        return false;
    }

    if (txtNombre.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el nombre del producto.");
        txtNombre.requestFocus();
        return false;
    }

    if (comboCategoria.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una categoría.");
        comboCategoria.requestFocus();
        return false;
    }

    if (comboUnidad.getSelectedItem() == null
            || comboUnidad.getSelectedItem().toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una unidad.");
        comboUnidad.requestFocus();
        return false;
    }

    if (comboMarca.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una marca.");
        comboMarca.requestFocus();
        return false;
    }

    if (txtEstante.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el estante.");
        txtEstante.requestFocus();
        return false;
    }

    if (txtNivel.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el nivel.");
        txtNivel.requestFocus();
        return false;
    }

    return true;
}
private boolean validarCodigo() {

    String codigo = txtCodigoBarras.getText().trim();

    if (!codigo.matches("\\d+")) {
        JOptionPane.showMessageDialog(this,
                "El código debe contener solo números.");
        txtCodigoBarras.requestFocus();
        return false;
    }

    return true;
}
private boolean validarUbicacion() {

    String estante = txtEstante.getText().trim();
    String nivel = txtNivel.getText().trim();

    if (!estante.matches("[A-Za-z]+")) {
        JOptionPane.showMessageDialog(this,
                "El estante debe contener solo letras.");
        txtEstante.requestFocus();
        return false;
    }

    if (!nivel.matches("\\d+")) {
        JOptionPane.showMessageDialog(this,
                "El nivel debe contener solo números.");
        txtNivel.requestFocus();
        return false;
    }

    return true;
}
private void registrarProducto() {

    if (!validarCamposObligatorios()) return;

    if (!validarCodigo()) return;

    if (!validarUbicacion()) return;

    String codigo = txtCodigoBarras.getText().trim();

    if (productoController.existeCodigo(codigo, 0)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe un producto activo con ese código.");
        txtCodigoBarras.requestFocus();
        return;
    }

    Producto producto = obtenerProductoDesdeFormulario();

    if (productoController.registrar(producto)) {

        JOptionPane.showMessageDialog(this,
                "Producto registrado correctamente.");

        cargarProductos();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el producto.");
    }
}
private void cargarProductoSeleccionado() {

    int fila = tablaProductos.getSelectedRow();

    if (fila == -1) {
        return;
    }

    int idProducto = Integer.parseInt(
            tablaProductos.getValueAt(fila, 0).toString()
    );

    for (Producto producto : productoController.listar()) {

        if (producto.getIdProducto() == idProducto) {

            txtCodigoBarras.setText(producto.getCodigo());
            txtNombre.setText(producto.getNombre());
            txtEstante.setText(producto.getEstante());
            txtNivel.setText(producto.getNivel());
            txtDescripcion.setText(producto.getDescripcion());

            seleccionarCategoria(producto.getIdCategoria());
            seleccionarMarca(producto.getIdMarca());
            seleccionarUnidad(producto.getUnidad());

            txtCodigoBarras.setEditable(false);

            break;
        }
    }
}
private void seleccionarCategoria(int idCategoria) {

    for (Categoria categoria : categoriaController.listar()) {

        if (categoria.getIdCategoria() == idCategoria) {

            comboCategoria.setSelectedItem(categoria.getNombre());
            break;
        }
    }
}

private void seleccionarMarca(int idMarca) {

    for (Marca marca : marcaController.listar()) {

        if (marca.getIdMarca() == idMarca) {

            comboMarca.setSelectedItem(marca.getNombre());
            break;
        }
    }
}

private void seleccionarUnidad(String unidad) {

    for (int i = 0; i < comboUnidad.getItemCount(); i++) {

        if (comboUnidad.getItemAt(i).toString().equals(unidad)) {
            comboUnidad.setSelectedIndex(i);
            break;
        }
    }
}
private void editarProducto() {

    int fila = tablaProductos.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la tabla.");
        return;
    }

    if (!validarCamposObligatorios()) return;

    if (!validarCodigo()) return;

    if (!validarUbicacion()) return;

    int idProducto = Integer.parseInt(
            tablaProductos.getValueAt(fila, 0).toString()
    );

    String codigo = txtCodigoBarras.getText().trim();

    if (productoController.existeCodigo(codigo, idProducto)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe otro producto activo con ese código.");
        txtCodigoBarras.requestFocus();
        return;
    }

    Producto producto = obtenerProductoDesdeFormulario();
    producto.setIdProducto(idProducto);

    if (productoController.actualizar(producto)) {

        JOptionPane.showMessageDialog(this,
                "Producto actualizado correctamente.");

        cargarProductos();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo actualizar el producto.");
    }
}
private void eliminarProducto() {

    int fila = tablaProductos.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la tabla.");
        return;
    }

    int idProducto = Integer.parseInt(
            tablaProductos.getValueAt(fila, 0).toString()
    );

    String nombreProducto =
            tablaProductos.getValueAt(fila, 2).toString();

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el producto \""
                    + nombreProducto + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (productoController.eliminar(idProducto)) {

        JOptionPane.showMessageDialog(this,
                "Producto eliminado correctamente.");

        cargarProductos();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo eliminar el producto.");
    }
}
private void buscarProducto() {

    String texto =
            txtBuscarCodigo.getText().trim().toLowerCase();

    DefaultTableModel modelo =
            (DefaultTableModel) tablaProductos.getModel();

    modelo.setRowCount(0);

    for (Producto producto : listaProductos) {

        String nombreCategoria = "";
        String nombreMarca = "";

        for (Categoria categoria : listaCategorias) {

            if (categoria.getIdCategoria()
                    == producto.getIdCategoria()) {

                nombreCategoria = categoria.getNombre();
                break;
            }
        }

        for (Marca marca : listaMarcas) {

            if (marca.getIdMarca()
                    == producto.getIdMarca()) {

                nombreMarca = marca.getNombre();
                break;
            }
        }

        if (producto.getCodigo().toLowerCase().contains(texto)
                || producto.getNombre().toLowerCase().contains(texto)
                || nombreCategoria.toLowerCase().contains(texto)
                || producto.getUnidad().toLowerCase().contains(texto)
                || producto.getEstante().toLowerCase().contains(texto)
                || producto.getNivel().toLowerCase().contains(texto)
                || nombreMarca.toLowerCase().contains(texto)) {

            modelo.addRow(new Object[]{
    producto.getIdProducto(),
    producto.getCodigo(),
    producto.getNombre(),
    nombreCategoria,
    nombreMarca,
    producto.getUnidad(),
    producto.getEstante(),
    producto.getNivel(),
    producto.getDescripcion()
});
        }
    }
}

private void nuevaCategoria() {

    String nombre = JOptionPane.showInputDialog(
            this,
            "Ingrese el nombre de la nueva categoría:",
            "Nueva Categoría",
            JOptionPane.PLAIN_MESSAGE
    );

    if (nombre == null) {
        return;
    }

    nombre = nombre.trim();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el nombre de la categoría.");
        return;
    }

    if (categoriaController.existeNombre(nombre, 0)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe una categoría activa con ese nombre.");
        return;
    }

    Categoria categoria = new Categoria();
    categoria.setNombre(nombre);
    categoria.setDescripcion(null);
    categoria.setActivo(true);

    if (categoriaController.registrar(categoria)) {

        JOptionPane.showMessageDialog(this,
                "Categoría registrada correctamente.");

        cargarCategorias();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo registrar la categoría.");
    }
}
private void eliminarCategoria() {

    String nombreCategoria =
            comboCategoria.getSelectedItem() != null
                    ? comboCategoria.getSelectedItem().toString()
                    : "";

    if (nombreCategoria.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una categoría.");
        return;
    }

    int idCategoria = -1;

    for (Categoria categoria : categoriaController.listar()) {

        if (categoria.getNombre().equals(nombreCategoria)) {
            idCategoria = categoria.getIdCategoria();
            break;
        }
    }

    if (idCategoria == -1) {
        return;
    }

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar la categoría \""
                    + nombreCategoria + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (categoriaController.eliminar(idCategoria)) {

        JOptionPane.showMessageDialog(this,
                "Categoría eliminada correctamente.");

        cargarCategorias();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo eliminar la categoría.");
    }
}
private void nuevaMarca() {

    String nombre = JOptionPane.showInputDialog(
            this,
            "Ingrese el nombre de la nueva marca:",
            "Nueva Marca",
            JOptionPane.PLAIN_MESSAGE
    );

    if (nombre == null) {
        return;
    }

    nombre = nombre.trim();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el nombre de la marca.");
        return;
    }

    if (marcaController.existeNombre(nombre, 0)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe una marca activa con ese nombre.");
        return;
    }

    Marca marca = new Marca();
    marca.setNombre(nombre);
    marca.setDescripcion(null);
    marca.setActivo(true);

    if (marcaController.registrar(marca)) {

        JOptionPane.showMessageDialog(this,
                "Marca registrada correctamente.");

        cargarMarcas();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo registrar la marca.");
    }
}
private void eliminarMarca() {

    String nombreMarca =
            comboMarca.getSelectedItem() != null
                    ? comboMarca.getSelectedItem().toString()
                    : "";

    if (nombreMarca.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Seleccione una marca.");
        return;
    }

    int idMarca = -1;

    for (Marca marca : marcaController.listar()) {

        if (marca.getNombre().equals(nombreMarca)) {
            idMarca = marca.getIdMarca();
            break;
        }
    }

    if (idMarca == -1) {
        return;
    }

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar la marca \""
                    + nombreMarca + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (marcaController.eliminar(idMarca)) {

        JOptionPane.showMessageDialog(this,
                "Marca eliminada correctamente.");

        cargarMarcas();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo eliminar la marca.");
    }
}
private void seleccionarProducto() {
    cargarProductoSeleccionado();
}
private void ejecutarBusquedaProducto() {
    buscarProducto();
}
private void limpiarBusqueda() {

    txtBuscarCodigo.setText("");
    cargarProductos();
}
private void refrescarCategorias() {

    String nombreSeleccionado =
            comboCategoria.getSelectedItem() != null
                    ? comboCategoria.getSelectedItem().toString()
                    : "";

    cargarCategorias();

    if (!nombreSeleccionado.isEmpty()) {
        comboCategoria.setSelectedItem(nombreSeleccionado);
    }
}

private void refrescarMarcas() {

    String nombreSeleccionado =
            comboMarca.getSelectedItem() != null
                    ? comboMarca.getSelectedItem().toString()
                    : "";

    cargarMarcas();

    if (!nombreSeleccionado.isEmpty()) {
        comboMarca.setSelectedItem(nombreSeleccionado);
    }
}
private void recargarDatos() {

    cargarCategorias();
    cargarMarcas();
    cargarProductos();
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtCodigoBarras = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel59 = new javax.swing.JLabel();
        comboUnidad = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        comboCategoria = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        txtNivel = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEstante = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        comboMarca = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        btnMarcaMas = new javax.swing.JButton();
        btnCategoriaMas = new javax.swing.JButton();
        btnMarcaMenos = new javax.swing.JButton();
        btnCategoriaMenos = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtBuscarCodigo = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 250, 242));

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar Campos");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar Producto de la tabla");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 250, 242));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Registrar Producto"));

        txtCodigoBarras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoBarrasActionPerformed(evt);
            }
        });
        txtCodigoBarras.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoBarrasKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Codigo: ");

        jLabel5.setText("Producto:");

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreKeyReleased(evt);
            }
        });

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane8.setViewportView(txtDescripcion);

        jLabel59.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel59.setText("Descripcion");

        comboUnidad.setEditable(true);
        comboUnidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unidad", "Caja", "Metro", "Rollo", "Litro", "Galón", "1/4 galón", "Balde", "Kilogramo", "Gramo", "Par", "Paquete", "Bolsa", "Juego", "Tubo" }));
        comboUnidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUnidadActionPerformed(evt);
            }
        });

        jLabel22.setText("Unidad de Medida");

        comboCategoria.setEditable(true);
        comboCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCategoriaActionPerformed(evt);
            }
        });

        jLabel6.setText("Categoria");

        btnGuardar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnGuardar.setText("Guardar Producto");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jLabel9.setText("Nivel");

        jLabel8.setText("Estante");

        jLabel23.setText("Marca");

        btnMarcaMas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMarcaMas.setText("+");
        btnMarcaMas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcaMasActionPerformed(evt);
            }
        });

        btnCategoriaMas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCategoriaMas.setText("+");
        btnCategoriaMas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriaMasActionPerformed(evt);
            }
        });

        btnMarcaMenos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMarcaMenos.setText("-");
        btnMarcaMenos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcaMenosActionPerformed(evt);
            }
        });

        btnCategoriaMenos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCategoriaMenos.setText("-");
        btnCategoriaMenos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriaMenosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel23))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtEstante, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel59)
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                        .addComponent(jLabel22)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboMarca, 0, 210, Short.MAX_VALUE)
                            .addComponent(comboUnidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnMarcaMas, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnMarcaMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 265, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCategoriaMas, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCategoriaMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(comboUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22)
                                .addComponent(jLabel6)
                                .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCategoriaMas, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCategoriaMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(16, 16, 16)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnMarcaMas, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnMarcaMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtEstante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(17, 17, 17)
                                        .addComponent(jLabel59)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)))
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 250, 242));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tabla Productos"));

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Codigo", "Producto", "Categoria", "Marca", "Unidad", "Estante", "Nivel", "Descripcion"
            }
        ));
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaProductos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jLabel7.setText("Buscar Producto");

        txtBuscarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarCodigoActionPerformed(evt);
            }
        });
        txtBuscarCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarCodigoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtBuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
  limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
    editarProducto();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
     eliminarProducto();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtCodigoBarrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoBarrasActionPerformed

    }//GEN-LAST:event_txtCodigoBarrasActionPerformed

    private void txtCodigoBarrasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoBarrasKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoBarrasKeyReleased

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreKeyReleased

    private void comboUnidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUnidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboUnidadActionPerformed

    private void comboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboCategoriaActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
       registrarProducto();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked
        seleccionarProducto();
    }//GEN-LAST:event_tablaProductosMouseClicked

    private void btnCategoriaMasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriaMasActionPerformed
       nuevaCategoria(); // TODO add your handling code here:
    }//GEN-LAST:event_btnCategoriaMasActionPerformed

    private void btnCategoriaMenosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriaMenosActionPerformed
eliminarCategoria();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCategoriaMenosActionPerformed

    private void btnMarcaMasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcaMasActionPerformed
nuevaMarca();        // TODO add your handling code here:
    }//GEN-LAST:event_btnMarcaMasActionPerformed

    private void btnMarcaMenosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcaMenosActionPerformed
eliminarMarca();        // TODO add your handling code here:
    }//GEN-LAST:event_btnMarcaMenosActionPerformed

    private void txtBuscarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarCodigoActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarCodigoActionPerformed

    private void txtBuscarCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarCodigoKeyReleased
ejecutarBusquedaProducto();         // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarCodigoKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategoriaMas;
    private javax.swing.JButton btnCategoriaMenos;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnMarcaMas;
    private javax.swing.JButton btnMarcaMenos;
    private javax.swing.JComboBox<String> comboCategoria;
    private javax.swing.JComboBox<String> comboMarca;
    private javax.swing.JComboBox<String> comboUnidad;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField txtBuscarCodigo;
    private javax.swing.JTextField txtCodigoBarras;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtEstante;
    private javax.swing.JTextField txtNivel;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
