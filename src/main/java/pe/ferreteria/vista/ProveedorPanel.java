
package pe.ferreteria.vista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import pe.ferreteria.controlador.ProveedorController;
import pe.ferreteria.modelo.Proveedor;

public class ProveedorPanel extends javax.swing.JPanel {
private final ProveedorController proveedorController =
        new ProveedorController();
private java.util.List<Proveedor> listaProveedores =
        new java.util.ArrayList<>();

   public ProveedorPanel() {
    initComponents();
    inicializarPanel();
}
   private void inicializarPanel() {
    cargarProveedores();
    limpiarCampos();
}

private void cargarProveedores() {

    listaProveedores = proveedorController.listar();

    DefaultTableModel modelo =
            (DefaultTableModel) tableProveedor.getModel();

    modelo.setRowCount(0);

    for (Proveedor proveedor : listaProveedores) {

        modelo.addRow(new Object[]{
            proveedor.getIdProveedor(),
            proveedor.getEmpresa(),
            proveedor.getRuc(),
            proveedor.getContacto(),
            proveedor.getTelefono(),
            proveedor.getCorreo(),
            proveedor.getDireccion()
        });
    }
}

private void limpiarCampos() {
    
    txtEmpresa.setText("");
    txtRUC.setText("");
    txtContacto.setText("");
    txtTelefono.setText("");
    txtCorreo.setText("");
    txtDireccion.setText("");

    txtEmpresa.setEditable(true);
    txtRUC.setEditable(true);

    tableProveedor.clearSelection();

    txtEmpresa.requestFocus();
}
private void cargarProveedorSeleccionado() {

    int fila = tableProveedor.getSelectedRow();

    if (fila == -1) {
        return;
    }

    int idProveedor = Integer.parseInt(
            tableProveedor.getValueAt(fila, 0).toString()
    );

    for (Proveedor proveedor : listaProveedores) {

        if (proveedor.getIdProveedor() == idProveedor) {

            txtEmpresa.setText(proveedor.getEmpresa());
            txtRUC.setText(proveedor.getRuc());
            txtContacto.setText(proveedor.getContacto());
            txtTelefono.setText(proveedor.getTelefono());
            txtCorreo.setText(proveedor.getCorreo());
            txtDireccion.setText(proveedor.getDireccion());

            txtEmpresa.setEditable(false);
            txtRUC.setEditable(false);

            break;
        }
    }
}
private boolean validarCamposObligatorios() {

    if (txtEmpresa.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese la empresa.");
        txtEmpresa.requestFocus();
        return false;
    }

    if (txtRUC.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el RUC.");
        txtRUC.requestFocus();
        return false;
    }

    if (!validarRUC()) {
        return false;
    }

    if (txtContacto.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el contacto.");
        txtContacto.requestFocus();
        return false;
    }

    if (txtTelefono.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el teléfono.");
        txtTelefono.requestFocus();
        return false;
    }

    if (!validarTelefono()) {
        return false;
    }

    if (txtDireccion.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese la dirección.");
        txtDireccion.requestFocus();
        return false;
    }

    return true;
}
private boolean validarRUC() {

    String ruc = txtRUC.getText().trim();

    if (!ruc.matches("\\d{11}")) {
        JOptionPane.showMessageDialog(this,
                "El RUC debe contener exactamente 11 dígitos.");
        txtRUC.requestFocus();
        return false;
    }

    return true;
}
private boolean validarTelefono() {

    String telefono = txtTelefono.getText().trim();

    if (!telefono.matches("\\d+")) {
        JOptionPane.showMessageDialog(this,
                "El teléfono solo debe contener números.");
        txtTelefono.requestFocus();
        return false;
    }

    return true;
}
private Proveedor obtenerProveedorDesdeFormulario() {

    Proveedor proveedor = new Proveedor();

    proveedor.setEmpresa(txtEmpresa.getText().trim());
    proveedor.setRuc(txtRUC.getText().trim());
    proveedor.setContacto(txtContacto.getText().trim());
    proveedor.setTelefono(txtTelefono.getText().trim());
    proveedor.setCorreo(txtCorreo.getText().trim());
    proveedor.setDireccion(txtDireccion.getText().trim());
    proveedor.setActivo(true);

    return proveedor;
}
private void registrarProveedor() {

    if (!validarCamposObligatorios()) {
        return;
    }

    String ruc = txtRUC.getText().trim();

    if (proveedorController.existeRuc(ruc, 0)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe un proveedor activo con ese RUC.");
        txtRUC.requestFocus();
        return;
    }

    Proveedor proveedor = obtenerProveedorDesdeFormulario();

    if (proveedorController.registrar(proveedor)) {

        JOptionPane.showMessageDialog(this,
                "Proveedor registrado correctamente.");

        cargarProveedores();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el proveedor.");
    }
}
private void editarProveedor() {

    int fila = tableProveedor.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
                "Seleccione un proveedor de la tabla.");
        return;
    }

    if (!validarCamposObligatorios()) {
        return;
    }

    int idProveedor = Integer.parseInt(
            tableProveedor.getValueAt(fila, 0).toString()
    );

    Proveedor proveedor = obtenerProveedorDesdeFormulario();
    proveedor.setIdProveedor(idProveedor);

    if (proveedorController.actualizar(proveedor)) {

        JOptionPane.showMessageDialog(this,
                "Proveedor actualizado correctamente.");

        cargarProveedores();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo actualizar el proveedor.");
    }
}
private void eliminarProveedor() {

    int fila = tableProveedor.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
                "Seleccione un proveedor de la tabla.");
        return;
    }

    int idProveedor = Integer.parseInt(
            tableProveedor.getValueAt(fila, 0).toString()
    );

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar este proveedor?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (proveedorController.eliminar(idProveedor)) {

        JOptionPane.showMessageDialog(this,
                "Proveedor eliminado correctamente.");

        cargarProveedores();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(this,
                "No se pudo eliminar el proveedor.");
    }
}
private void buscarProveedor() {

    String texto = txtBuscarProveedor.getText().trim().toLowerCase();

    DefaultTableModel modelo =
            (DefaultTableModel) tableProveedor.getModel();

    modelo.setRowCount(0);

    for (Proveedor proveedor : listaProveedores) {

        if (proveedor.getEmpresa().toLowerCase().contains(texto)
                || proveedor.getRuc().contains(texto)
                || proveedor.getContacto().toLowerCase().contains(texto)
                || proveedor.getTelefono().contains(texto)
                || proveedor.getCorreo().toLowerCase().contains(texto)
                || proveedor.getDireccion().toLowerCase().contains(texto)) {

            modelo.addRow(new Object[]{
                proveedor.getIdProveedor(),
                proveedor.getEmpresa(),
                proveedor.getRuc(),
                proveedor.getContacto(),
                proveedor.getTelefono(),
                proveedor.getCorreo(),
                proveedor.getDireccion()
            });
        }
    }
} 

private void limpiarBusqueda() {
    txtBuscarProveedor.setText("");
    cargarProveedores();
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txtEmpresa = new javax.swing.JTextField();
        txtContacto = new javax.swing.JTextField();
        txtRUC = new javax.swing.JTextField();
        txtBuscarProveedor = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        jSeparator17 = new javax.swing.JSeparator();
        btnLimpiar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jSeparator18 = new javax.swing.JSeparator();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableProveedor = new javax.swing.JTable();
        jLabel49 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 250, 242));

        jPanel7.setBackground(new java.awt.Color(255, 250, 242));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel43.setText("GESTION DE PROVEEDORES");

        jLabel44.setText("Empresa: ");

        jLabel45.setText("RUC: ");

        jLabel46.setText("Contacto:");

        jLabel47.setText("Direccion: ");

        jLabel48.setText("Correo:");

        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });

        txtBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProveedorActionPerformed(evt);
            }
        });
        txtBuscarProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProveedorKeyReleased(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tableProveedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Empresa", "RUC", "Contacto", "Telefono", "Correo", "Direccion"
            }
        ));
        tableProveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProveedorMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tableProveedor);

        jLabel49.setText("Telefono:");

        txtCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoActionPerformed(evt);
            }
        });

        jLabel50.setText("Buscar:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator16)
            .addComponent(jSeparator18, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator17, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(jLabel48))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel45))
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel47)
                        .addGap(18, 18, 18)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(67, 67, 67)
                .addComponent(jLabel46)
                .addGap(39, 39, 39)
                .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel43))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel50)
                        .addGap(42, 42, 42)
                        .addComponent(txtBuscarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(97, 97, 97)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane6)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jSeparator16, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel44)
                                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel47))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel45)
                        .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel50))
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator18, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarProveedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProveedorKeyReleased
 buscarProveedor();
    }//GEN-LAST:event_txtBuscarProveedorKeyReleased

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    limpiarBusqueda();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        registrarProveedor();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
    editarProveedor();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProveedor();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tableProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProveedorMouseClicked
        cargarProveedorSeleccionado();
    }//GEN-LAST:event_tableProveedorMouseClicked

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoActionPerformed

    private void txtBuscarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProveedorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JTable tableProveedor;
    private javax.swing.JTextField txtBuscarProveedor;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmpresa;
    private javax.swing.JTextField txtRUC;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
