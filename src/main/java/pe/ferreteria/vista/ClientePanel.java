package pe.ferreteria.vista;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import pe.ferreteria.controlador.ClienteController;
import pe.ferreteria.modelo.Cliente;

public class ClientePanel extends javax.swing.JPanel {

    private final ClienteController clienteController = new ClienteController();
private java.util.List<Cliente> listaClientes =
        new java.util.ArrayList<>();
    public ClientePanel() {
        initComponents();
        inicializarPanel();
    }

    private void inicializarPanel() {
        cargarClientes();
        actualizarCamposSegunTipo();
        limpiarCampos();
    }

    private void actualizarCamposSegunTipo() {

        boolean natural = rbNatural.isSelected();

        // Persona Natural
        txtDNI.setEnabled(natural);
        txtNombrePersonaNatural.setEnabled(natural);
        txtApellidoPersonaNatural.setEnabled(natural);
        txtTelefonoPersonaNatural.setEnabled(natural);
        txtDireccionPersonaNatural.setEnabled(natural);

        // Persona Jurídica
        txtRUC.setEnabled(!natural);
        txtRazonSocial.setEnabled(!natural);
        txtContactodeRazonSocial.setEnabled(!natural);
        txtTelefonoPersonaJuridica.setEnabled(!natural);
        txtDireccionPersonaJuridica.setEnabled(!natural);
    }
private void cargarClientes() {

    listaClientes = clienteController.listar();

    DefaultTableModel modelo =
            (DefaultTableModel) TableCliente.getModel();

    modelo.setRowCount(0);

    for (Cliente cliente : listaClientes) {

        String identificacion;
        String nombre;
        String apellidoContacto;

        if ("Natural".equals(cliente.getTipoCliente())) {

            identificacion = cliente.getDni();
            nombre = cliente.getNombre();
            apellidoContacto = cliente.getApellidos();

        } else {

            identificacion = cliente.getRuc();
            nombre = cliente.getRazonSocial();
            apellidoContacto = cliente.getContacto();
        }

        modelo.addRow(new Object[]{
            cliente.getIdCliente(),
            identificacion,
            nombre,
            apellidoContacto,
            cliente.getTelefono(),
            cliente.getDireccion()
        });
    }
}
private void limpiarCampos() {

    txtDNI.setText("");
    txtNombrePersonaNatural.setText("");
    txtApellidoPersonaNatural.setText("");
    txtTelefonoPersonaNatural.setText("");
    txtDireccionPersonaNatural.setText("");

    txtRUC.setText("");
    txtRazonSocial.setText("");
    txtContactodeRazonSocial.setText("");
    txtTelefonoPersonaJuridica.setText("");
    txtDireccionPersonaJuridica.setText("");

    txtBusquedaEnTableClientes.setText("");

    TableCliente.clearSelection();

    rbNatural.setSelected(true);

    txtDNI.setEditable(true);
    txtRUC.setEditable(true);

    actualizarCamposSegunTipo();

    txtDNI.requestFocus();
}
private Cliente obtenerClienteDesdeFormulario() {

    Cliente cliente = new Cliente();

    cliente.setTipoCliente(rbNatural.isSelected() ? "Natural" : "Juridica");

    if (rbNatural.isSelected()) {

        cliente.setDni(txtDNI.getText().trim());
        cliente.setNombre(txtNombrePersonaNatural.getText().trim());
        cliente.setApellidos(txtApellidoPersonaNatural.getText().trim());
        cliente.setTelefono(txtTelefonoPersonaNatural.getText().trim());
        cliente.setDireccion(txtDireccionPersonaNatural.getText().trim());

        cliente.setRuc(null);
        cliente.setRazonSocial(null);
        cliente.setContacto(null);

    } else {

        cliente.setRuc(txtRUC.getText().trim());
        cliente.setRazonSocial(txtRazonSocial.getText().trim());
        cliente.setContacto(txtContactodeRazonSocial.getText().trim());
        cliente.setTelefono(txtTelefonoPersonaJuridica.getText().trim());
        cliente.setDireccion(txtDireccionPersonaJuridica.getText().trim());

        cliente.setDni(null);
        cliente.setNombre(null);
        cliente.setApellidos(null);
    }

    cliente.setActivo(true);

    return cliente;
}
private void cargarClienteSeleccionado() {

    int fila = TableCliente.getSelectedRow();

    if (fila == -1) {
        return;
    }

    int idCliente = Integer.parseInt(
            TableCliente.getValueAt(fila, 0).toString()
    );

    Cliente cliente = null;

    for (Cliente c : listaClientes) {

        if (c.getIdCliente() == idCliente) {
            cliente = c;
            break;
        }
    }

    if (cliente == null) {
        return;
    }

    if ("Natural".equals(cliente.getTipoCliente())) {

        rbNatural.setSelected(true);
        actualizarCamposSegunTipo();

        txtDNI.setText(cliente.getDni());
        txtNombrePersonaNatural.setText(cliente.getNombre());
        txtApellidoPersonaNatural.setText(cliente.getApellidos());
        txtTelefonoPersonaNatural.setText(cliente.getTelefono());
        txtDireccionPersonaNatural.setText(cliente.getDireccion());

        txtDNI.setEditable(false);

    } else {

        rbJuridico.setSelected(true);
        actualizarCamposSegunTipo();

        txtRUC.setText(cliente.getRuc());
        txtRazonSocial.setText(cliente.getRazonSocial());
        txtContactodeRazonSocial.setText(cliente.getContacto());
        txtTelefonoPersonaJuridica.setText(cliente.getTelefono());
        txtDireccionPersonaJuridica.setText(cliente.getDireccion());

        txtRUC.setEditable(false);
    }
}
private void guardarCliente() {

    if (rbNatural.isSelected()) {

        if (txtDNI.getText().trim().isEmpty()
                || txtNombrePersonaNatural.getText().trim().isEmpty()
                || txtApellidoPersonaNatural.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete DNI, Nombre y Apellidos."
            );
            return;
        }

        if (!txtDNI.getText().trim().matches("\\d{8}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El DNI debe tener exactamente 8 números."
            );
            return;
        }

        if (!txtTelefonoPersonaNatural.getText().trim().isEmpty()
                && !txtTelefonoPersonaNatural.getText().trim().matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return;
        }

        if (clienteController.existeDni(
                txtDNI.getText().trim(), 0)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe un cliente con ese DNI."
            );
            return;
        }

    } else {

        if (txtRUC.getText().trim().isEmpty()
                || txtRazonSocial.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete RUC y Razón Social."
            );
            return;
        }

        if (!txtRUC.getText().trim().matches("\\d{11}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El RUC debe tener exactamente 11 números."
            );
            return;
        }

        if (!txtTelefonoPersonaJuridica.getText().trim().isEmpty()
                && !txtTelefonoPersonaJuridica.getText().trim().matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return;
        }

        if (clienteController.existeRuc(
                txtRUC.getText().trim(), 0)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe un cliente con ese RUC."
            );
            return;
        }
    }

    Cliente cliente = obtenerClienteDesdeFormulario();

    if (clienteController.registrar(cliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente registrado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar el cliente."
        );
    }
}
private void editarCliente() {

    int fila = TableCliente.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Seleccione un cliente de la tabla."
        );
        return;
    }

    int idCliente = Integer.parseInt(
            TableCliente.getValueAt(fila, 0).toString()
    );

    if (rbNatural.isSelected()) {

        if (txtDNI.getText().trim().isEmpty()
                || txtNombrePersonaNatural.getText().trim().isEmpty()
                || txtApellidoPersonaNatural.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete DNI, Nombre y Apellidos."
            );
            return;
        }

        if (!txtDNI.getText().trim().matches("\\d{8}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El DNI debe tener exactamente 8 números."
            );
            return;
        }

        if (!txtTelefonoPersonaNatural.getText().trim().isEmpty()
                && !txtTelefonoPersonaNatural.getText().trim().matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return;
        }

        if (clienteController.existeDni(
                txtDNI.getText().trim(), idCliente)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe otro cliente con ese DNI."
            );
            return;
        }

    } else {

        if (txtRUC.getText().trim().isEmpty()
                || txtRazonSocial.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete RUC y Razón Social."
            );
            return;
        }

        if (!txtRUC.getText().trim().matches("\\d{11}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El RUC debe tener exactamente 11 números."
            );
            return;
        }

        if (!txtTelefonoPersonaJuridica.getText().trim().isEmpty()
                && !txtTelefonoPersonaJuridica.getText().trim().matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return;
        }

        if (clienteController.existeRuc(
                txtRUC.getText().trim(), idCliente)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe otro cliente con ese RUC."
            );
            return;
        }
    }

    Cliente cliente = obtenerClienteDesdeFormulario();
    cliente.setIdCliente(idCliente);

    if (clienteController.actualizar(cliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente actualizado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el cliente."
        );
    }
}
private void eliminarCliente() {

    int fila = TableCliente.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Seleccione un cliente de la tabla."
        );
        return;
    }

    int idCliente = Integer.parseInt(
            TableCliente.getValueAt(fila, 0).toString()
    );

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar este cliente?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (clienteController.eliminar(idCliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente eliminado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar el cliente."
        );
    }
}

private void buscarClientes() {

    String criterio = txtBusquedaEnTableClientes.getText()
            .trim()
            .toLowerCase();

    DefaultTableModel modelo =
            (DefaultTableModel) TableCliente.getModel();

    modelo.setRowCount(0);

    for (Cliente cliente : listaClientes) {

        String identificacion;
        String nombre;
        String apellidoContacto;

        if ("Natural".equals(cliente.getTipoCliente())) {

            identificacion = cliente.getDni();
            nombre = cliente.getNombre();
            apellidoContacto = cliente.getApellidos();

        } else {

            identificacion = cliente.getRuc();
            nombre = cliente.getRazonSocial();
            apellidoContacto = cliente.getContacto();
        }

        if ((identificacion != null
                && identificacion.toLowerCase().contains(criterio))
                || (nombre != null
                && nombre.toLowerCase().contains(criterio))
                || (apellidoContacto != null
                && apellidoContacto.toLowerCase().contains(criterio))
                || (cliente.getTelefono() != null
                && cliente.getTelefono().toLowerCase().contains(criterio))
                || (cliente.getDireccion() != null
                && cliente.getDireccion().toLowerCase().contains(criterio))) {

            modelo.addRow(new Object[]{
                cliente.getIdCliente(),
                identificacion,
                nombre,
                apellidoContacto,
                cliente.getTelefono(),
                cliente.getDireccion()
            });
        }
    }
}
private void cambiarTipoCliente() {

    actualizarCamposSegunTipo();

    txtDNI.setEditable(rbNatural.isSelected());
    txtRUC.setEditable(rbJuridico.isSelected());
}
private boolean validarTelefono(String telefono) {

    if (telefono == null || telefono.trim().isEmpty()) {
        return true;
    }

    return telefono.trim().matches("\\d+");
}
private boolean validarDNI() {

    String dni = txtDNI.getText().trim();

    if (dni.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Ingrese el DNI."
        );
        return false;
    }

    if (!dni.matches("\\d{8}")) {
        JOptionPane.showMessageDialog(
                this,
                "El DNI debe tener exactamente 8 números."
        );
        return false;
    }

    return true;
}
    private boolean validarRUC() {

    String ruc = txtRUC.getText().trim();

    if (ruc.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Ingrese el RUC."
        );
        return false;
    }

    if (!ruc.matches("\\d{11}")) {
        JOptionPane.showMessageDialog(
                this,
                "El RUC debe tener exactamente 11 números."
        );
        return false;
    }

    return true;
}
    private boolean validarCamposObligatorios() {

    if (rbNatural.isSelected()) {

        if (txtDNI.getText().trim().isEmpty()
                || txtNombrePersonaNatural.getText().trim().isEmpty()
                || txtApellidoPersonaNatural.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete DNI, Nombre y Apellidos."
            );
            return false;
        }

        return validarDNI();

    } else {

        if (txtRUC.getText().trim().isEmpty()
                || txtRazonSocial.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Complete RUC y Razón Social."
            );
            return false;
        }

        return validarRUC();
    }
}
    private boolean validarTelefonos() {

    if (rbNatural.isSelected()) {

        if (!validarTelefono(txtTelefonoPersonaNatural.getText())) {
            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return false;
        }

    } else {

        if (!validarTelefono(txtTelefonoPersonaJuridica.getText())) {
            JOptionPane.showMessageDialog(
                    this,
                    "El teléfono solo debe contener números."
            );
            return false;
        }
    }

    return true;
}
private void registrarCliente() {

    if (!validarCamposObligatorios()) {
        return;
    }

    if (!validarTelefonos()) {
        return;
    }

    if (rbNatural.isSelected()
            && clienteController.existeDni(txtDNI.getText().trim(), 0)) {

        JOptionPane.showMessageDialog(
                this,
                "Ya existe un cliente con ese DNI."
        );
        return;
    }

    if (rbJuridico.isSelected()
            && clienteController.existeRuc(txtRUC.getText().trim(), 0)) {

        JOptionPane.showMessageDialog(
                this,
                "Ya existe un cliente con ese RUC."
        );
        return;
    }

    Cliente cliente = obtenerClienteDesdeFormulario();

    if (clienteController.registrar(cliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente registrado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar el cliente."
        );
    }
}    
    
private void actualizarCliente() {

    int fila = TableCliente.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Seleccione un cliente de la tabla."
        );
        return;
    }

    int idCliente = Integer.parseInt(
            TableCliente.getValueAt(fila, 0).toString()
    );

    if (!validarCamposObligatorios()) {
        return;
    }

    if (!validarTelefonos()) {
        return;
    }

    if (rbNatural.isSelected()
            && clienteController.existeDni(
                    txtDNI.getText().trim(), idCliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Ya existe otro cliente con ese DNI."
        );
        return;
    }

    if (rbJuridico.isSelected()
            && clienteController.existeRuc(
                    txtRUC.getText().trim(), idCliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Ya existe otro cliente con ese RUC."
        );
        return;
    }

    Cliente cliente = obtenerClienteDesdeFormulario();
    cliente.setIdCliente(idCliente);

    if (clienteController.actualizar(cliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente actualizado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el cliente."
        );
    }
}    

private void confirmarEliminacionCliente() {

    int fila = TableCliente.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Seleccione un cliente de la tabla."
        );
        return;
    }

    int idCliente = Integer.parseInt(
            TableCliente.getValueAt(fila, 0).toString()
    );

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar este cliente?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (clienteController.eliminar(idCliente)) {

        JOptionPane.showMessageDialog(
                this,
                "Cliente eliminado correctamente."
        );

        cargarClientes();
        limpiarCampos();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar el cliente."
        );
    }
}
private void seleccionarCliente() {

    cargarClienteSeleccionado();
}
private void ejecutarBusquedaCliente() {

    buscarClientes();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtDNI = new javax.swing.JTextField();
        txtNombrePersonaNatural = new javax.swing.JTextField();
        txtApellidoPersonaNatural = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtTelefonoPersonaNatural = new javax.swing.JTextField();
        txtDireccionPersonaNatural = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        bntEditarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        btnLimpiarTodo = new javax.swing.JButton();
        btnGuardarCliente = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtRUC = new javax.swing.JTextField();
        txtRazonSocial = new javax.swing.JTextField();
        txtContactodeRazonSocial = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        txtTelefonoPersonaJuridica = new javax.swing.JTextField();
        txtDireccionPersonaJuridica = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        rbJuridico = new javax.swing.JRadioButton();
        rbNatural = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        txtBusquedaEnTableClientes = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        TableCliente = new javax.swing.JTable();

        setBackground(new java.awt.Color(247, 252, 248));

        jPanel1.setBackground(new java.awt.Color(247, 252, 248));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("REGISTRAR CLIENTE"));

        jPanel3.setBackground(new java.awt.Color(232, 245, 233));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Persona Natural"));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel39.setText("DNI *");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel36.setText("Nombre *");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel40.setText("Apellidos * ");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel43.setText("Telefono");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setText("Direccion");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40)
                            .addComponent(jLabel39)
                            .addComponent(jLabel38))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTelefonoPersonaNatural, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .addComponent(txtApellidoPersonaNatural)
                            .addComponent(txtNombrePersonaNatural)
                            .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDireccionPersonaNatural)))
                    .addComponent(jLabel43)
                    .addComponent(jLabel36))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNombrePersonaNatural, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApellidoPersonaNatural, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefonoPersonaNatural, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccionPersonaNatural, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(232, 245, 233));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Comandos"));

        bntEditarCliente.setText("EDITAR");
        bntEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntEditarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setText("ELIMINAR");
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        btnLimpiarTodo.setText("LIMPIAR");
        btnLimpiarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarTodoActionPerformed(evt);
            }
        });

        btnGuardarCliente.setText("GUARDAR");
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(btnLimpiarTodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bntEditarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bntEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLimpiarTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(232, 245, 233));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Persona Juridica"));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel47.setText("RUC *");

        jLabel48.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel48.setText("Razon Social *");

        jLabel49.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel49.setText("Contacto");

        jLabel50.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel50.setText("Telefono:");

        jLabel51.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel51.setText("Direccion: ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel49)
                            .addComponent(jLabel47)
                            .addComponent(jLabel51))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDireccionPersonaJuridica, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .addComponent(txtTelefonoPersonaJuridica)
                            .addComponent(txtContactodeRazonSocial)
                            .addComponent(txtRazonSocial)))
                    .addComponent(jLabel50)
                    .addComponent(jLabel48))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtContactodeRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefonoPersonaJuridica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccionPersonaJuridica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(247, 252, 248));

        buttonGroup1.add(rbJuridico);
        rbJuridico.setText("Persona Juridica");
        rbJuridico.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rbJuridico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbJuridicoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbNatural);
        rbNatural.setText("Persona Natural");
        rbNatural.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbNaturalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbNatural)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 395, Short.MAX_VALUE)
                .addComponent(rbJuridico)
                .addGap(168, 168, 168))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbJuridico)
                    .addComponent(rbNatural))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(117, 117, 117)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(168, 168, 168))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Busqueda: ");

        txtBusquedaEnTableClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusquedaEnTableClientesActionPerformed(evt);
            }
        });
        txtBusquedaEnTableClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaEnTableClientesKeyReleased(evt);
            }
        });

        TableCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "DNI/RUC", "Nombre/Razon", "Apellido/Contacto", "Telefono", "Direccion"
            }
        ));
        TableCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableClienteMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(TableCliente);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane5)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBusquedaEnTableClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(txtBusquedaEnTableClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bntEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEditarClienteActionPerformed
        actualizarCliente();
    }//GEN-LAST:event_bntEditarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
       confirmarEliminacionCliente();
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnLimpiarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarTodoActionPerformed
       limpiarCampos();
    }//GEN-LAST:event_btnLimpiarTodoActionPerformed

    private void btnGuardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteActionPerformed
       registrarCliente();
    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void txtBusquedaEnTableClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaEnTableClientesKeyReleased
       ejecutarBusquedaCliente();
    }//GEN-LAST:event_txtBusquedaEnTableClientesKeyReleased

    private void TableClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableClienteMouseClicked
      seleccionarCliente();
    }//GEN-LAST:event_TableClienteMouseClicked

    private void rbNaturalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNaturalActionPerformed
cambiarTipoCliente();        // TODO add your handling code here:
    }//GEN-LAST:event_rbNaturalActionPerformed

    private void rbJuridicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbJuridicoActionPerformed
cambiarTipoCliente();        // TODO add your handling code here:
    }//GEN-LAST:event_rbJuridicoActionPerformed

    private void txtBusquedaEnTableClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusquedaEnTableClientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusquedaEnTableClientesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableCliente;
    private javax.swing.JButton bntEditarCliente;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnLimpiarTodo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JRadioButton rbJuridico;
    private javax.swing.JRadioButton rbNatural;
    private javax.swing.JTextField txtApellidoPersonaNatural;
    private javax.swing.JTextField txtBusquedaEnTableClientes;
    private javax.swing.JTextField txtContactodeRazonSocial;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtDireccionPersonaJuridica;
    private javax.swing.JTextField txtDireccionPersonaNatural;
    private javax.swing.JTextField txtNombrePersonaNatural;
    private javax.swing.JTextField txtRUC;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelefonoPersonaJuridica;
    private javax.swing.JTextField txtTelefonoPersonaNatural;
    // End of variables declaration//GEN-END:variables
}
