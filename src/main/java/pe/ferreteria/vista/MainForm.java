
package pe.ferreteria.vista;
import javax.swing.JOptionPane;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.servicio.CajaService;

public class MainForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainForm.class.getName());
private final pe.ferreteria.servicio.CajaService cajaService =
        new pe.ferreteria.servicio.CajaService();
     private ProductoPanel productoPanel;
    private VentaPanel ventaPanel;
    private InventarioPanel inventarioPanel;
    private ProveedorPanel proveedorPanel;
    private CompraPanel compraPanel;
    private UsuarioPanel usuarioPanel;
    private ClientePanel clientePanel;
    private KardexPanel kardexPanel;
    private DashboardPanel dashboardPanel;
    private RecepcionPanel recepcionPanel;
    
   public MainForm() {
    initComponents();

    setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
    );

    addWindowListener(
            new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(
                        java.awt.event.WindowEvent e) {

                    if (!puedeSalir()) {
                        return;
                    }

                    SesionUsuario.cerrarSesion();
                    dispose();
                }
            }
    );

    cargarDatosUsuarioSesion();

    productoPanel = new ProductoPanel();
    ventaPanel = new VentaPanel();
    inventarioPanel = new InventarioPanel();
    proveedorPanel = new ProveedorPanel();
    compraPanel = new CompraPanel();
    usuarioPanel = new UsuarioPanel();
    clientePanel = new ClientePanel();
    kardexPanel = new KardexPanel();
    dashboardPanel = new DashboardPanel();
    recepcionPanel = new RecepcionPanel();

    configurarAccesoPorRol();

if ("Vendedor".equals(
        SesionUsuario.getUsuarioActual().getRol())) {

    mostrarPanel(ventaPanel);
    marcarBotonSeleccionado(btnVentas1);

} else {

    mostrarPanel(productoPanel);
    marcarBotonSeleccionado(btnProductos1);
}
}
    private void cargarDatosUsuarioSesion() {

    if (SesionUsuario.getUsuarioActual() == null) {
        return;
    }

    pe.ferreteria.modelo.Usuario usuario =
            SesionUsuario.getUsuarioActual();

    txtUsuarioNombre.setText(
            usuario.getNombre() + " " + usuario.getApellidos()
    );

    txtUsuarioRol.setText(
            usuario.getRol()
    );
}
   private void abrirGestionCaja() {

    pe.ferreteria.modelo.Caja caja =
            cajaService.buscarCajaAbierta();

    if (caja != null) {

        CajaDialog dialog =
                new CajaDialog(this, true, caja);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return;
    }

    if (caja == null) {

        pe.ferreteria.modelo.Usuario usuario =
                SesionUsuario.getUsuarioActual();

        javax.swing.JTextField txtMontoApertura =
                new javax.swing.JTextField();

        javax.swing.JPanel panel =
                new javax.swing.JPanel(
                        new java.awt.GridLayout(0, 2, 8, 8)
                );
        panel.add(new javax.swing.JLabel("Usuario:"));
        panel.add(new javax.swing.JLabel(
                usuario.getNombre() + " "
                + usuario.getApellidos()
        ));

        panel.add(new javax.swing.JLabel("Fecha:"));
        panel.add(new javax.swing.JLabel(
                java.time.LocalDate.now().toString()
        ));

        panel.add(new javax.swing.JLabel("Hora:"));
        panel.add(new javax.swing.JLabel(
                java.time.LocalTime.now()
                        .withNano(0)
                        .toString()
        ));

        panel.add(new javax.swing.JLabel("Monto de apertura:"));
        panel.add(txtMontoApertura);

        int opcion =
                javax.swing.JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "ABRIR CAJA",
                        javax.swing.JOptionPane.OK_CANCEL_OPTION,
                        javax.swing.JOptionPane.PLAIN_MESSAGE
                );

        if (opcion != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {

            java.math.BigDecimal montoApertura =
                    new java.math.BigDecimal(
                            txtMontoApertura.getText().trim()
                    );

            if (montoApertura.compareTo(
                    java.math.BigDecimal.ZERO) < 0) {

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "El monto de apertura no puede ser negativo.",
                        "ABRIR CAJA",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            caja = new pe.ferreteria.modelo.Caja();

            caja.setIdUsuarioApertura(
                    usuario.getIdUsuario()
            );

            caja.setFechaApertura(
                    java.time.LocalDateTime.now()
            );

            caja.setMontoApertura(
                    montoApertura
            );

            caja.setTotalIngresos(
                    java.math.BigDecimal.ZERO
            );

            caja.setTotalSalidas(
                    java.math.BigDecimal.ZERO
            );

            caja.setMontoEsperado(
                    montoApertura
            );

            caja.setEstado("Abierta");

            boolean registrada =
                    cajaService.registrarApertura(caja);

            if (!registrada) {

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "No se pudo abrir la Caja.",
                        "ABRIR CAJA",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Caja abierta correctamente.",
                    "CAJA",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un monto de apertura válido.",
                    "ABRIR CAJA",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
        }
    }}
    
    
    private void mostrarPanel(javax.swing.JPanel panel) {

    Usuario usuario = SesionUsuario.getUsuarioActual();

    if (usuario == null) {
        return;
    }

    String rol = usuario.getRol();
if ("Almacen".equals(rol)) {

    if (panel == ventaPanel
            || panel == clientePanel
            || panel == usuarioPanel
            || panel == dashboardPanel) {

        return;
    }
}
    // Vendedor no puede acceder a estos módulos
    if ("Vendedor".equals(rol)) {

        if (panel == productoPanel
                || panel == proveedorPanel
                || panel == compraPanel
                || panel == usuarioPanel
                || panel == dashboardPanel
                || panel == recepcionPanel) {

            return;
        }
    }

    panelContenedor.removeAll();
    panelContenedor.setLayout(
            new java.awt.BorderLayout()
    );
    panelContenedor.add(
            panel,
            java.awt.BorderLayout.CENTER
    );
    panelContenedor.revalidate();
    panelContenedor.repaint();
}
    private boolean puedeSalir() {

    Usuario usuario =
            SesionUsuario.getUsuarioActual();

    if (usuario == null) {
        return true;
    }

    CajaService cajaService =
            new CajaService();

    Caja cajaAbierta =
            cajaService.buscarCajaAbiertaPorUsuario(
                    usuario.getIdUsuario()
            );

    if (cajaAbierta != null) {

        JOptionPane.showMessageDialog(
                this,
                "Su Caja sigue abierta.\n"
                + "Debe cerrar la Caja antes de salir.",
                "Caja abierta",
                JOptionPane.WARNING_MESSAGE
        );

        return false;
    }

    return true;
}
    private void configurarAccesoPorRol() {

    Usuario usuario = SesionUsuario.getUsuarioActual();

    if (usuario == null) {
        return;
    }

    String rol = usuario.getRol();

    // Administrador: acceso total
    if ("Administrador".equals(rol)) {
        return;
    }

    // Vendedor: ocultar módulos no permitidos
    if ("Vendedor".equals(rol)) {

        btnProductos1.setVisible(false);
        btnProveedores1.setVisible(false);
        btnCompras1.setVisible(false);
        btnUsuarios1.setVisible(false);
        btnDashBoard.setVisible(false);
        btnRecepcion.setVisible(false);
    }
    if ("Almacen".equals(rol)) {

    btnVentas1.setVisible(false);
    btnClientes1.setVisible(false);
    btnUsuarios1.setVisible(false);
    btnDashBoard.setVisible(false);
    btnCaja.setVisible(false);
}
}
    private void marcarBotonSeleccionado(javax.swing.JButton botonSeleccionado) {

    javax.swing.JButton[] botones = {
        btnProductos1,
        btnVentas1,
        btnInventario1,
        btnProveedores1,
        btnCompras1,
        btnUsuarios1,
        btnClientes1,
        btnKardex,
        btnDashBoard,
        btnRecepcion,
        btnCaja
    };

    for (javax.swing.JButton boton : botones) {
        boton.setBackground(
                new java.awt.Color(204, 204, 204)
        );
    }

    botonSeleccionado.setBackground(
            new java.awt.Color(100, 149, 237)
    );
}
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelContenedor = new javax.swing.JPanel();
        panelBotones = new javax.swing.JPanel();
        btnProductos1 = new javax.swing.JButton();
        btnVentas1 = new javax.swing.JButton();
        btnInventario1 = new javax.swing.JButton();
        btnProveedores1 = new javax.swing.JButton();
        btnCompras1 = new javax.swing.JButton();
        btnUsuarios1 = new javax.swing.JButton();
        btnClientes1 = new javax.swing.JButton();
        btnKardex = new javax.swing.JButton();
        btnRecepcion = new javax.swing.JButton();
        btnDashBoard = new javax.swing.JButton();
        PanelEncabezado = new javax.swing.JPanel();
        txtUsuarioNombre = new javax.swing.JLabel();
        txtUsuarioRol = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnCaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(230, 230, 255));

        panelContenedor.setBackground(new java.awt.Color(230, 230, 255));
        panelContenedor.setLayout(new java.awt.CardLayout());

        panelBotones.setBackground(new java.awt.Color(230, 230, 255));

        btnProductos1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnProductos1.setText("PRODUCTOS");
        btnProductos1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnProductos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductos1ActionPerformed(evt);
            }
        });

        btnVentas1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnVentas1.setText("VENTAS");
        btnVentas1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnVentas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentas1ActionPerformed(evt);
            }
        });

        btnInventario1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnInventario1.setText("INVENTARIO");
        btnInventario1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnInventario1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventario1ActionPerformed(evt);
            }
        });

        btnProveedores1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnProveedores1.setText("PROVEEDORES");
        btnProveedores1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnProveedores1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedores1ActionPerformed(evt);
            }
        });

        btnCompras1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCompras1.setText("COMPRAS");
        btnCompras1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCompras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompras1ActionPerformed(evt);
            }
        });

        btnUsuarios1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnUsuarios1.setText("USUARIOS");
        btnUsuarios1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnUsuarios1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuarios1ActionPerformed(evt);
            }
        });

        btnClientes1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnClientes1.setText("CLIENTES");
        btnClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnClientes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientes1ActionPerformed(evt);
            }
        });

        btnKardex.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnKardex.setText("KARDEX");
        btnKardex.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnKardex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKardexActionPerformed(evt);
            }
        });

        btnRecepcion.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnRecepcion.setText("RECEPCION");
        btnRecepcion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRecepcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecepcionActionPerformed(evt);
            }
        });

        btnDashBoard.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnDashBoard.setText("DASHBOARD");
        btnDashBoard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnDashBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashBoardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnProductos1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVentas1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnClientes1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnInventario1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProveedores1)
                .addGap(18, 18, 18)
                .addComponent(btnCompras1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKardex, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUsuarios1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRecepcion, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDashBoard, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProductos1)
                    .addComponent(btnVentas1)
                    .addComponent(btnInventario1)
                    .addComponent(btnProveedores1)
                    .addComponent(btnCompras1)
                    .addComponent(btnUsuarios1)
                    .addComponent(btnClientes1)
                    .addComponent(btnKardex)
                    .addComponent(btnRecepcion)
                    .addComponent(btnDashBoard))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        PanelEncabezado.setBackground(new java.awt.Color(230, 230, 255));

        txtUsuarioNombre.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtUsuarioNombre.setText("------");

        txtUsuarioRol.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtUsuarioRol.setText("-----");

        btnCerrarSesion.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCerrarSesion.setText("Cerrar sesion");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setText("USUARIO: ");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setText("ROL: ");

        btnCaja.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnCaja.setText("CAJA");
        btnCaja.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelEncabezadoLayout = new javax.swing.GroupLayout(PanelEncabezado);
        PanelEncabezado.setLayout(PanelEncabezadoLayout);
        PanelEncabezadoLayout.setHorizontalGroup(
            PanelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEncabezadoLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtUsuarioNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtUsuarioRol, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(146, 146, 146)
                .addComponent(btnCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        PanelEncabezadoLayout.setVerticalGroup(
            PanelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuarioNombre)
                    .addComponent(txtUsuarioRol)
                    .addComponent(btnCerrarSesion)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(btnCaja))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PanelEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProductos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductos1ActionPerformed
       mostrarPanel(productoPanel);
marcarBotonSeleccionado(btnProductos1);
    }//GEN-LAST:event_btnProductos1ActionPerformed

    private void btnVentas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentas1ActionPerformed
        mostrarPanel(ventaPanel);
marcarBotonSeleccionado(btnVentas1);
    }//GEN-LAST:event_btnVentas1ActionPerformed

    private void btnInventario1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventario1ActionPerformed
       mostrarPanel(inventarioPanel);
marcarBotonSeleccionado(btnInventario1);
    }//GEN-LAST:event_btnInventario1ActionPerformed

    private void btnProveedores1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedores1ActionPerformed
        mostrarPanel(proveedorPanel);
marcarBotonSeleccionado(btnProveedores1);
    }//GEN-LAST:event_btnProveedores1ActionPerformed

    private void btnCompras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompras1ActionPerformed
        mostrarPanel(compraPanel);
marcarBotonSeleccionado(btnCompras1);
    }//GEN-LAST:event_btnCompras1ActionPerformed

    private void btnUsuarios1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuarios1ActionPerformed
       mostrarPanel(usuarioPanel);
marcarBotonSeleccionado(btnUsuarios1);
    }//GEN-LAST:event_btnUsuarios1ActionPerformed

    private void btnClientes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientes1ActionPerformed
 mostrarPanel(clientePanel);
marcarBotonSeleccionado(btnClientes1);
    }//GEN-LAST:event_btnClientes1ActionPerformed

    private void btnKardexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKardexActionPerformed
        mostrarPanel(kardexPanel);
marcarBotonSeleccionado(btnKardex);
    }//GEN-LAST:event_btnKardexActionPerformed

    private void btnDashBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashBoardActionPerformed
      mostrarPanel(dashboardPanel);
marcarBotonSeleccionado(btnDashBoard);
    }//GEN-LAST:event_btnDashBoardActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        if (!puedeSalir()) {
        return;
    }

    SesionUsuario.cerrarSesion();

    LoginForm login =
            new LoginForm();

    login.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnRecepcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecepcionActionPerformed
        mostrarPanel(recepcionPanel);
marcarBotonSeleccionado(btnRecepcion);
    }//GEN-LAST:event_btnRecepcionActionPerformed

    private void btnCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajaActionPerformed
      abrirGestionCaja();
marcarBotonSeleccionado(btnCaja);  // TODO add your handling code here:
    }//GEN-LAST:event_btnCajaActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelEncabezado;
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnClientes1;
    private javax.swing.JButton btnCompras1;
    private javax.swing.JButton btnDashBoard;
    private javax.swing.JButton btnInventario1;
    private javax.swing.JButton btnKardex;
    private javax.swing.JButton btnProductos1;
    private javax.swing.JButton btnProveedores1;
    private javax.swing.JButton btnRecepcion;
    private javax.swing.JButton btnUsuarios1;
    private javax.swing.JButton btnVentas1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JLabel txtUsuarioNombre;
    private javax.swing.JLabel txtUsuarioRol;
    // End of variables declaration//GEN-END:variables
}
