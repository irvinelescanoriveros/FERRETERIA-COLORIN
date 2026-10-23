package pe.ferreteria.vista;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pe.ferreteria.dao.UsuarioDAO;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Usuario;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.MovimientoCaja;
import pe.ferreteria.servicio.CajaService;

public class CajaDialog extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(CajaDialog.class.getName());

    private Caja caja;

    private final CajaService cajaService =
            new CajaService();
    
    private final UsuarioDAO usuarioDAO =
        new UsuarioDAO();

    // Componentes creados manualmente
    private JLabel lblEstadoValor;
    private JLabel lblCajaValor;
    private JLabel lblUsuarioAperturaValor;
    private JLabel lblAperturaValor;

    private JLabel lblMontoAperturaValor;
    private JLabel lblTotalIngresosValor;
    private JLabel lblTotalSalidasValor;
    private JLabel lblMontoEsperadoValor;

    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    private JButton btnActualizar;
    private JButton btnCerrarCaja;

    public CajaDialog(
            java.awt.Frame parent,
            boolean modal,
            Caja caja) {

        super(parent, modal);

        this.caja = caja;

        initComponents();

        configurarPanelSuperior();
        configurarPanelResumen();
        configurarPanelMovimientos();
        configurarPanelBotones();

        cargarDatosCaja();
    }

    private void configurarPanelSuperior() {

        PanelSuperior.removeAll();

        PanelSuperior.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblEstado =
                new JLabel("Estado:");

        lblEstadoValor =
                new JLabel("SIN CAJA");

        JLabel lblCaja =
                new JLabel("Caja N.º:");

        lblCajaValor =
                new JLabel("-");

        JLabel lblUsuarioApertura =
                new JLabel("Usuario apertura:");

        lblUsuarioAperturaValor =
                new JLabel("-");

        JLabel lblApertura =
                new JLabel("Apertura:");

        lblAperturaValor =
                new JLabel("-");

        JLabel lblUsuario =
                new JLabel("Usuario:");

        JComboBox<String> cmbUsuario =
                new JComboBox<>();

        cmbUsuario.addItem("Todos");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        PanelSuperior.add(lblEstado, gbc);

        gbc.gridx = 1;
        PanelSuperior.add(lblEstadoValor, gbc);

        gbc.gridx = 2;
        PanelSuperior.add(lblCaja, gbc);

        gbc.gridx = 3;
        PanelSuperior.add(lblCajaValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        PanelSuperior.add(lblUsuarioApertura, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        PanelSuperior.add(lblUsuarioAperturaValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        PanelSuperior.add(lblApertura, gbc);

        gbc.gridx = 1;
        PanelSuperior.add(lblAperturaValor, gbc);

        gbc.gridx = 2;
        PanelSuperior.add(lblUsuario, gbc);

        gbc.gridx = 3;
        PanelSuperior.add(cmbUsuario, gbc);

        PanelSuperior.revalidate();
        PanelSuperior.repaint();
    }

    private void configurarPanelResumen() {

        PanelResumen.removeAll();

        PanelResumen.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo =
                new JLabel("RESUMEN");

        JLabel lblMontoApertura =
                new JLabel("Monto apertura:");

        lblMontoAperturaValor =
                new JLabel("S/ 0.00");

        JLabel lblTotalIngresos =
                new JLabel("Total ingresos:");

        lblTotalIngresosValor =
                new JLabel("S/ 0.00");

        JLabel lblTotalSalidas =
                new JLabel("Total salidas:");

        lblTotalSalidasValor =
                new JLabel("S/ 0.00");

        JLabel lblMontoEsperado =
                new JLabel("Monto esperado:");

        lblMontoEsperadoValor =
                new JLabel("S/ 0.00");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        PanelResumen.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        PanelResumen.add(lblMontoApertura, gbc);

        gbc.gridx = 1;
        PanelResumen.add(lblMontoAperturaValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        PanelResumen.add(lblTotalIngresos, gbc);

        gbc.gridx = 1;
        PanelResumen.add(lblTotalIngresosValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        PanelResumen.add(lblTotalSalidas, gbc);

        gbc.gridx = 1;
        PanelResumen.add(lblTotalSalidasValor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        PanelResumen.add(lblMontoEsperado, gbc);

        gbc.gridx = 1;
        PanelResumen.add(lblMontoEsperadoValor, gbc);

        PanelResumen.revalidate();
        PanelResumen.repaint();
    }

    private void configurarPanelMovimientos() {

        PanelMoviminetos.removeAll();

        PanelMoviminetos.setLayout(
                new BorderLayout(5, 5)
        );

        JLabel lblTitulo =
                new JLabel("MOVIMIENTOS");

        PanelMoviminetos.add(
                lblTitulo,
                BorderLayout.NORTH
        );

        String[] columnas = {
            "Fecha",
            "Tipo",
            "Concepto",
            "Monto",
            "Método"
        };

        modeloMovimientos =
                new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        tablaMovimientos =
                new JTable(modeloMovimientos);

        tablaMovimientos.setRowHeight(22);

        tablaMovimientos.setAutoCreateRowSorter(true);

        tablaMovimientos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(tablaMovimientos);

        PanelMoviminetos.add(
                scroll,
                BorderLayout.CENTER
        );

        PanelMoviminetos.revalidate();
        PanelMoviminetos.repaint();
    }

    private void configurarPanelBotones() {

        PanelBotonesInferior.removeAll();

        PanelBotonesInferior.setLayout(
                new BorderLayout(10, 5)
        );

        btnActualizar =
                new JButton("Actualizar");

        btnCerrarCaja =
                new JButton("Cerrar Caja");

        btnActualizar.addActionListener(e ->
                cargarDatosCaja()
        );

        btnCerrarCaja.addActionListener(e ->
                cerrarCaja()
        );

        PanelBotonesInferior.add(
                btnActualizar,
                BorderLayout.WEST
        );

        PanelBotonesInferior.add(
                btnCerrarCaja,
                BorderLayout.EAST
        );

        PanelBotonesInferior.revalidate();
        PanelBotonesInferior.repaint();
    }

    private void cargarDatosCaja() {

        if (caja == null) {
            return;
        }

        Caja cajaActual =
                cajaService.buscarPorId(
                        caja.getIdCaja()
                );

        if (cajaActual == null) {
            return;
        }

        caja = cajaActual;

        lblEstadoValor.setText(
                caja.getEstado()
        );

        lblCajaValor.setText(
                String.valueOf(
                        caja.getIdCaja()
                )
        );

        Usuario usuarioApertura =
        usuarioDAO.buscarPorId(caja.getIdUsuarioApertura());

lblUsuarioAperturaValor.setText(
        usuarioApertura != null
                ? usuarioApertura.getUsername()
                : String.valueOf(caja.getIdUsuarioApertura())
);

        lblAperturaValor.setText(
                caja.getFechaApertura() != null
                        ? caja.getFechaApertura().toString()
                        : "-"
        );

        lblMontoAperturaValor.setText(
                formatoMonto(
                        caja.getMontoApertura()
                )
        );

        lblTotalIngresosValor.setText(
                formatoMonto(
                        caja.getTotalIngresos()
                )
        );

        lblTotalSalidasValor.setText(
                formatoMonto(
                        caja.getTotalSalidas()
                )
        );

        lblMontoEsperadoValor.setText(
                formatoMonto(
                        caja.getMontoEsperado()
                )
        );

        cargarMovimientos();
    }

    private void cargarMovimientos() {

        modeloMovimientos.setRowCount(0);

        List<MovimientoCaja> movimientos =
                cajaService.listarMovimientosPorCaja(
                        caja.getIdCaja()
                );

        for (MovimientoCaja movimiento : movimientos) {

            modeloMovimientos.addRow(
                    new Object[]{
                        movimiento.getFechaMovimiento(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getConcepto(),
                        formatoMonto(
                                movimiento.getMonto()
                        ),
                        movimiento.getMetodoPago()
                    }
            );
        }
    }

    private String formatoMonto(BigDecimal monto) {

        if (monto == null) {
            return "S/ 0.00";
        }

        return "S/ " +
                monto.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                ).toString();
    }

  private void cerrarCaja() {

    JTextField txtMontoContado = new JTextField();
    JTextField txtObservacion = new JTextField();

    JLabel lblMontoEsperado =
            new JLabel("S/ " + formatoMonto(caja.getMontoEsperado()));

    JLabel lblDiferencia =
            new JLabel("S/ 0.00");

    JPanel panel =
            new JPanel(new GridLayout(0, 2, 8, 8));

    panel.add(new JLabel("Monto esperado:"));
    panel.add(lblMontoEsperado);

    panel.add(new JLabel("Monto contado:"));
    panel.add(txtMontoContado);

    panel.add(new JLabel("Diferencia:"));
    panel.add(lblDiferencia);

    panel.add(new JLabel("Observación:"));
    panel.add(txtObservacion);

    javax.swing.JButton btnCancelar =
            new javax.swing.JButton("Cancelar");

    javax.swing.JButton btnConfirmar =
            new javax.swing.JButton("Confirmar");

    JPanel panelBotones =
            new JPanel();

    panelBotones.add(btnCancelar);
    panelBotones.add(btnConfirmar);

    JPanel panelPrincipal =
            new JPanel(new BorderLayout(8, 8));

    panelPrincipal.add(panel, BorderLayout.CENTER);
    panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

    javax.swing.JDialog dialogo =
            new javax.swing.JDialog(
                    this,
                    "Cerrar Caja",
                    true
            );

    dialogo.setLayout(new BorderLayout(8, 8));
    dialogo.add(panelPrincipal, BorderLayout.CENTER);
    dialogo.pack();
    dialogo.setLocationRelativeTo(this);

    txtMontoContado.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void actualizar() {

                    try {

                        BigDecimal montoContado =
                                new BigDecimal(
                                        txtMontoContado
                                                .getText()
                                                .trim()
                                );

                        BigDecimal diferencia =
                                montoContado
                                        .subtract(
                                                caja.getMontoEsperado()
                                        )
                                        .setScale(
                                                2,
                                                java.math.RoundingMode.HALF_UP
                                        );

                        lblDiferencia.setText(
                                "S/ " + formatoMonto(diferencia)
                        );

                    } catch (Exception e) {

                        lblDiferencia.setText("S/ 0.00");
                    }
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizar();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizar();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizar();
                }
            }
    );

    btnCancelar.addActionListener(e ->
            dialogo.dispose()
    );

    btnConfirmar.addActionListener(e -> {

        BigDecimal montoContado;

        try {

            montoContado =
                    new BigDecimal(
                            txtMontoContado
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    dialogo,
                    "Ingrese un monto contado válido.",
                    "Dato inválido",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (montoContado.compareTo(BigDecimal.ZERO) < 0) {

            JOptionPane.showMessageDialog(
                    dialogo,
                    "El monto contado no puede ser negativo.",
                    "Dato inválido",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        BigDecimal diferencia =
                montoContado
                        .subtract(caja.getMontoEsperado())
                        .setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        );

        Usuario usuario =
                SesionUsuario.getUsuarioActual();

        if (usuario == null) {

            JOptionPane.showMessageDialog(
                    dialogo,
                    "No hay un usuario con sesión activa.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        boolean cerrado =
                cajaService.cerrarCaja(
                        caja.getIdCaja(),
                        usuario.getIdUsuario(),
                        montoContado,
                        diferencia,
                        txtObservacion.getText().trim()
                );

        if (!cerrado) {

            JOptionPane.showMessageDialog(
                    dialogo,
                    "No se pudo cerrar la Caja.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        dialogo.dispose();

        JOptionPane.showMessageDialog(
                this,
                "Caja cerrada correctamente.",
                "Caja",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    });

    dialogo.setVisible(true);
}  


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMoviminetos = new javax.swing.JPanel();
        PanelSuperior = new javax.swing.JPanel();
        PanelResumen = new javax.swing.JPanel();
        PanelBotonesInferior = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(232, 245, 233));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        PanelMoviminetos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelMoviminetosLayout = new javax.swing.GroupLayout(PanelMoviminetos);
        PanelMoviminetos.setLayout(PanelMoviminetosLayout);
        PanelMoviminetosLayout.setHorizontalGroup(
            PanelMoviminetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PanelMoviminetosLayout.setVerticalGroup(
            PanelMoviminetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );

        PanelSuperior.setBackground(new java.awt.Color(232, 245, 233));
        PanelSuperior.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelSuperiorLayout = new javax.swing.GroupLayout(PanelSuperior);
        PanelSuperior.setLayout(PanelSuperiorLayout);
        PanelSuperiorLayout.setHorizontalGroup(
            PanelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        PanelSuperiorLayout.setVerticalGroup(
            PanelSuperiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 79, Short.MAX_VALUE)
        );

        PanelResumen.setBackground(new java.awt.Color(232, 245, 233));
        PanelResumen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelResumenLayout = new javax.swing.GroupLayout(PanelResumen);
        PanelResumen.setLayout(PanelResumenLayout);
        PanelResumenLayout.setHorizontalGroup(
            PanelResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );
        PanelResumenLayout.setVerticalGroup(
            PanelResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        PanelBotonesInferior.setBackground(new java.awt.Color(232, 245, 233));
        PanelBotonesInferior.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout PanelBotonesInferiorLayout = new javax.swing.GroupLayout(PanelBotonesInferior);
        PanelBotonesInferior.setLayout(PanelBotonesInferiorLayout);
        PanelBotonesInferiorLayout.setHorizontalGroup(
            PanelBotonesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PanelBotonesInferiorLayout.setVerticalGroup(
            PanelBotonesInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelResumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PanelMoviminetos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelBotonesInferior, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelResumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelMoviminetos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelBotonesInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  
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
                CajaDialog dialog = new CajaDialog(
        new javax.swing.JFrame(),
        true,
        null
);
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
    private javax.swing.JPanel PanelBotonesInferior;
    private javax.swing.JPanel PanelMoviminetos;
    private javax.swing.JPanel PanelResumen;
    private javax.swing.JPanel PanelSuperior;
    // End of variables declaration//GEN-END:variables
}
