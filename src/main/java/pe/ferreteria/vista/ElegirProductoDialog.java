
package pe.ferreteria.vista;
import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.controlador.MarcaController;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.modelo.Producto;


public class ElegirProductoDialog extends javax.swing.JDialog {
    private final ProductoController productoController =
        new ProductoController();

private final CategoriaController categoriaController =
        new CategoriaController();

private final MarcaController marcaController =
        new MarcaController();

private java.util.List<Producto> todosLosProductos =
        new java.util.ArrayList<>();

private java.util.List<Producto> productosFiltrados =
        new java.util.ArrayList<>();

private java.util.Map<Integer, String> nombresCategoria =
        new java.util.HashMap<>();

private java.util.Map<Integer, String> nombresMarca =
        new java.util.HashMap<>();

private Producto productoSeleccionado = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ElegirProductoDialog.class.getName());

    
    public ElegirProductoDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    configurarDialogo();
}

private void configurarDialogo() {

    configurarTabla();
    cargarProductos();

    // Tecla Esc = Cancelar (cierra sin elegir nada)
    getRootPane().registerKeyboardAction(
            e -> cancelar(),
            javax.swing.KeyStroke.getKeyStroke(
                    java.awt.event.KeyEvent.VK_ESCAPE, 0),
            javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
    );

    // Tecla Enter sobre la tabla = Seleccionar
    tablaProductos.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
            .put(javax.swing.KeyStroke.getKeyStroke(
                    java.awt.event.KeyEvent.VK_ENTER, 0), "elegir");

    tablaProductos.getActionMap().put("elegir",
            new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            seleccionarProducto();
        }
    });

    txtBuscar.requestFocusInWindow();
}

private void configurarTabla() {

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(
                    new Object[]{
                        "ID", "Código", "Nombre", "Categoría", "Marca",
                        "Unidad", "Estante", "Nivel", "Descripción"
                    },
                    0
            ) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    tablaProductos.setModel(modelo);
    tablaProductos.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
}

private void cargarProductos() {

    todosLosProductos = productoController.listar();

    nombresCategoria.clear();
    for (Categoria categoria : categoriaController.listar()) {
        nombresCategoria.put(
                categoria.getIdCategoria(), categoria.getNombre());
    }

    nombresMarca.clear();
    for (Marca marca : marcaController.listar()) {
        nombresMarca.put(
                marca.getIdMarca(), marca.getNombre());
    }

    filtrarProductos();
}

private void filtrarProductos() {

    String criterio = txtBuscar.getText().trim().toLowerCase();

    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) tablaProductos.getModel();

    modelo.setRowCount(0);
    productosFiltrados.clear();

    for (Producto producto : todosLosProductos) {

        String categoria =
                nombresCategoria.getOrDefault(producto.getIdCategoria(), "");
        String marca =
                nombresMarca.getOrDefault(producto.getIdMarca(), "");

        String texto = (
                (producto.getCodigo() == null ? "" : producto.getCodigo())
                + " " + (producto.getNombre() == null ? "" : producto.getNombre())
                + " " + categoria
                + " " + marca
                + " " + (producto.getDescripcion() == null ? "" : producto.getDescripcion())
        ).toLowerCase();

        if (!criterio.isEmpty() && !texto.contains(criterio)) {
            continue;
        }

        productosFiltrados.add(producto);

        modelo.addRow(new Object[]{
            producto.getIdProducto(),
            producto.getCodigo(),
            producto.getNombre(),
            categoria,
            marca,
            producto.getUnidad(),
            producto.getEstante(),
            producto.getNivel(),
            producto.getDescripcion()
        });
    }

    if (!productosFiltrados.isEmpty()) {
        tablaProductos.setRowSelectionInterval(0, 0);
    }
}

private void seleccionarDesdeBuscador() {

    // Enter en el buscador (sirve tambien para el lector de codigos)
    filtrarProductos();

    if (productosFiltrados.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No se encontró ningún producto con ese dato."
        );
        return;
    }

    String texto = txtBuscar.getText().trim();

    // Si el codigo coincide exacto, se elige ese producto
    for (int i = 0; i < productosFiltrados.size(); i++) {

        String codigo = productosFiltrados.get(i).getCodigo();

        if (codigo != null && codigo.equalsIgnoreCase(texto)) {
            tablaProductos.setRowSelectionInterval(i, i);
            seleccionarProducto();
            return;
        }
    }

    // Si queda un solo resultado, se elige directamente
    if (productosFiltrados.size() == 1) {
        seleccionarProducto();
        return;
    }

    // Si hay varios, se pasa a la tabla para elegir con las flechas
    tablaProductos.requestFocusInWindow();
}

private void seleccionarProducto() {

    int fila = tablaProductos.getSelectedRow();

    if (fila < 0 || fila >= productosFiltrados.size()) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Seleccione un producto de la tabla."
        );
        return;
    }

    productoSeleccionado = productosFiltrados.get(fila);
    dispose();
}

private void cancelar() {

    productoSeleccionado = null;
    dispose();
}

public Producto getProductoSeleccionado() {
    return productoSeleccionado;
}

    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnSeleccionar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Buscar (codigo, nombre, categoria, marca)");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        btnSeleccionar.setText("Seleccionar");
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaProductos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(154, 154, 154)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(214, 214, 214))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1086, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(79, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
seleccionarDesdeBuscador();        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
     filtrarProductos();   // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked
    if (evt.getClickCount() == 2) {
    seleccionarProducto();
}    // TODO add your handling code here:
    }//GEN-LAST:event_tablaProductosMouseClicked

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
seleccionarProducto();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
cancelar();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ElegirProductoDialog dialog = new ElegirProductoDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
