package pe.ferreteria.vista;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import pe.ferreteria.util.GeneradorComprobante;
import pe.ferreteria.util.GeneradorPDF;
import pe.ferreteria.controlador.InventarioController;
import pe.ferreteria.controlador.MarcaController;
import pe.ferreteria.controlador.ProductoController;
import pe.ferreteria.controlador.VentaController;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Marca;
import pe.ferreteria.modelo.Producto;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pe.ferreteria.controlador.CategoriaController;
import pe.ferreteria.infraestructura.SesionUsuario;
import pe.ferreteria.modelo.Venta;
import pe.ferreteria.controlador.ClienteController;
import pe.ferreteria.modelo.Categoria;
import pe.ferreteria.modelo.Cliente;
import pe.ferreteria.modelo.DetalleVenta;

public class VentaPanel extends javax.swing.JPanel {

    private final ClienteController clienteController
            = new ClienteController();

    private java.util.List<Cliente> listaClientes
            = new java.util.ArrayList<>();
    private final InventarioController inventarioController
            = new InventarioController();

    private final ProductoController productoController
            = new ProductoController();

    private final MarcaController marcaController
            = new MarcaController();

    private final CategoriaController categoriaController
            = new CategoriaController();

    private List<Inventario> listaInventario
            = new ArrayList<>();

    private List<Producto> listaProductos
            = new ArrayList<>();

    private List<Marca> listaMarcas
            = new ArrayList<>();

    private List<Categoria> listaCategorias
            = new ArrayList<>();
    private List<DetalleVenta> listaDetallesVenta
            = new ArrayList<>();
    private final VentaController ventaController
            = new VentaController();

    public VentaPanel() {
        initComponents();
        inicializarVenta();
    }

    private void inicializarVenta() {

        configurarTablaClientes();
        cargarClientes();
        limpiarDatosCliente();

        cargarDatosProductos();
        configurarTablaProductos();
        configurarTablaResumenVenta();
        cargarFiltrosProductos();
        cargarTablaBuscarProducto();
    }

    private void configurarTablaClientes() {

        DefaultTableModel modelo
                = new DefaultTableModel(
                        new Object[]{
                            "ID",
                            "DNI / RUC",
                            "Nombre / Empresa",
                            "Apellido / Contacto",
                            "Teléfono"
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

        TableBuscarCliente.setModel(modelo);
    }

    private void cargarClientes() {

        listaClientes
                = clienteController.listar();

        DefaultTableModel modelo
                = (DefaultTableModel) TableBuscarCliente.getModel();

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
                cliente.getTelefono()
            });
        }
    }

    private void limpiarDatosCliente() {

        txtDNIRUCCLiente.setText("");
        txtNombreRazonsocialCliente.setText("");
        txtApellidoContacto.setText("");

        txtDNIRUCCLiente.setEditable(false);
        txtNombreRazonsocialCliente.setEditable(false);
        txtApellidoContacto.setEditable(false);

        TableBuscarCliente.clearSelection();
    }

    private void buscarClientes() {

        String criterio
                = txtBuscarCliente.getText()
                        .trim()
                        .toLowerCase();

        DefaultTableModel modelo
                = (DefaultTableModel) TableBuscarCliente.getModel();

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
                    && cliente.getTelefono().toLowerCase().contains(criterio))) {

                modelo.addRow(new Object[]{
                    cliente.getIdCliente(),
                    identificacion,
                    nombre,
                    apellidoContacto,
                    cliente.getTelefono()
                });
            }
        }
    }

    private void cargarClienteSeleccionado() {

        int fila = TableBuscarCliente.getSelectedRow();

        if (fila == -1) {
            return;
        }

        int idCliente = Integer.parseInt(
                TableBuscarCliente.getValueAt(fila, 0).toString()
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

        txtDNIRUCCLiente.setText(
                identificacion != null ? identificacion : ""
        );

        txtNombreRazonsocialCliente.setText(
                nombre != null ? nombre : ""
        );

        txtApellidoContacto.setText(
                apellidoContacto != null ? apellidoContacto : ""
        );
    }

    private void cargarDatosProductos() {

        listaInventario
                = inventarioController.listar();

        listaProductos
                = productoController.listar();

        listaMarcas
                = marcaController.listar();

        listaCategorias
                = categoriaController.listar();
    }

    private void configurarTablaProductos() {

        DefaultTableModel modelo
                = new DefaultTableModel(
                        new Object[]{
                            "Lote",
                            "Producto",
                            "Categoría",
                            "Unidad",
                            "Marca",
                            "StockLote",
                            "StockProducto",
                            "P. Venta"
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

        tableBuscarProducto.setModel(modelo);
    }

    private void cargarFiltrosProductos() {

        comboMarca.removeAllItems();
        comboCategoria.removeAllItems();

        comboMarca.addItem("Todas");
        comboCategoria.addItem("Todas");

        for (Marca marca : listaMarcas) {
            comboMarca.addItem(marca.getNombre());
        }

        for (Categoria categoria : listaCategorias) {
            comboCategoria.addItem(categoria.getNombre());
        }

        comboMarca.setSelectedIndex(0);
        comboCategoria.setSelectedIndex(0);
    }

    private void cargarTablaBuscarProducto() {

        DefaultTableModel modelo
                = (DefaultTableModel) tableBuscarProducto.getModel();

        modelo.setRowCount(0);

        List<Inventario> inventariosDisponibles
                = new ArrayList<>();

        for (Inventario inventario : listaInventario) {

            if (!inventario.isActivo()) {
                continue;
            }

            if (inventario.getStock() <= 0) {
                continue;
            }

            inventariosDisponibles.add(inventario);
        }

        inventariosDisponibles.sort(
                Comparator.comparing(
                        Inventario::getFechaIngreso,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        for (Inventario inventario : inventariosDisponibles) {

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null
                    || !productoEncontrado.isActivo()) {

                continue;
            }

            String nombreCategoria = "";

            for (Categoria categoria : listaCategorias) {

                if (categoria.getIdCategoria()
                        == productoEncontrado.getIdCategoria()) {

                    nombreCategoria
                            = categoria.getNombre();
                    break;
                }
            }

            String nombreMarca = "";

            for (Marca marca : listaMarcas) {

                if (marca.getIdMarca()
                        == productoEncontrado.getIdMarca()) {

                    nombreMarca
                            = marca.getNombre();
                    break;
                }
            }

            int stockProducto
                    = obtenerStockProductoVenta(
                            inventario.getIdProducto()
                    );

            modelo.addRow(new Object[]{
                inventario.getLote(),
                productoEncontrado.getNombre(),
                nombreCategoria,
                productoEncontrado.getUnidad(),
                nombreMarca,
                inventario.getStock(),
                stockProducto,
                inventario.getPrecioVenta()
            });
        }
    }

    private int obtenerStockProductoVenta(int idProducto) {

        int stockTotal = 0;

        for (Inventario inventario : listaInventario) {

            if (inventario.isActivo()
                    && inventario.getIdProducto()
                    == idProducto
                    && inventario.getStock() > 0) {

                stockTotal += inventario.getStock();
            }
        }

        return stockTotal;
    }

    private void filtrarTablaBuscarProducto() {

        String textoBusqueda
                = txtBuscarProductos.getText()
                        .trim()
                        .toLowerCase();

        String marcaSeleccionada
                = comboMarca.getSelectedItem() == null
                ? "Todas"
                : comboMarca.getSelectedItem().toString();

        String categoriaSeleccionada
                = comboCategoria.getSelectedItem() == null
                ? "Todas"
                : comboCategoria.getSelectedItem().toString();

        DefaultTableModel modelo
                = (DefaultTableModel) tableBuscarProducto.getModel();

        modelo.setRowCount(0);

        List<Inventario> inventariosDisponibles
                = new ArrayList<>();

        for (Inventario inventario : listaInventario) {

            if (!inventario.isActivo()) {
                continue;
            }

            if (inventario.getStock() <= 0) {
                continue;
            }

            inventariosDisponibles.add(inventario);
        }

        // Más antiguo arriba, más nuevo abajo
        inventariosDisponibles.sort(
                Comparator.comparing(
                        Inventario::getFechaIngreso,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        for (Inventario inventario : inventariosDisponibles) {

            Producto productoEncontrado = null;

            for (Producto producto : listaProductos) {

                if (producto.getIdProducto()
                        == inventario.getIdProducto()) {

                    productoEncontrado = producto;
                    break;
                }
            }

            if (productoEncontrado == null
                    || !productoEncontrado.isActivo()) {
                continue;
            }

            String nombreProducto
                    = productoEncontrado.getNombre();

            String nombreCategoria = "";

            for (Categoria categoria : listaCategorias) {

                if (categoria.getIdCategoria()
                        == productoEncontrado.getIdCategoria()) {

                    nombreCategoria
                            = categoria.getNombre();
                    break;
                }
            }

            String nombreMarca = "";

            for (Marca marca : listaMarcas) {

                if (marca.getIdMarca()
                        == productoEncontrado.getIdMarca()) {

                    nombreMarca
                            = marca.getNombre();
                    break;
                }
            }

            boolean coincideBusqueda
                    = textoBusqueda.isEmpty()
                    || nombreProducto.toLowerCase()
                            .contains(textoBusqueda)
                    || inventario.getLote()
                            .toLowerCase()
                            .contains(textoBusqueda);

            boolean coincideMarca
                    = marcaSeleccionada.equals("Todas")
                    || nombreMarca.equals(
                            marcaSeleccionada
                    );

            boolean coincideCategoria
                    = categoriaSeleccionada.equals("Todas")
                    || nombreCategoria.equals(
                            categoriaSeleccionada
                    );

            if (!coincideBusqueda
                    || !coincideMarca
                    || !coincideCategoria) {
                continue;
            }

            int stockProducto
                    = obtenerStockProductoVenta(
                            inventario.getIdProducto()
                    );

            modelo.addRow(new Object[]{
                inventario.getLote(),
                nombreProducto,
                nombreCategoria,
                productoEncontrado.getUnidad(),
                nombreMarca,
                inventario.getStock(),
                stockProducto,
                inventario.getPrecioVenta()
            });
        }
    }

    private void configurarTablaResumenVenta() {

        DefaultTableModel modelo
                = new DefaultTableModel(
                        new Object[]{
                            "Lote",
                            "Producto",
                            "Unidad",
                            "Cant.",
                            "P. Final",
                            "Desc.",
                            "Importe"
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

        tableResumenVenta.setModel(modelo);
    }

    private void cargarProductoSeleccionado() {

        int fila = tableBuscarProducto.getSelectedRow();

        if (fila == -1) {
            return;
        }

        String lote
                = tableBuscarProducto
                        .getValueAt(fila, 0)
                        .toString();

        Inventario inventarioSeleccionado = null;

        for (Inventario inventario : listaInventario) {

            if (inventario.getLote().equals(lote)
                    && inventario.isActivo()
                    && inventario.getStock() > 0) {

                inventarioSeleccionado = inventario;
                break;
            }
        }

        if (inventarioSeleccionado == null) {
            return;
        }

        Producto productoEncontrado = null;

        for (Producto producto : listaProductos) {

            if (producto.getIdProducto()
                    == inventarioSeleccionado.getIdProducto()) {

                productoEncontrado = producto;
                break;
            }
        }

        if (productoEncontrado == null) {
            return;
        }

        String nombreMarca = "";

        for (Marca marca : listaMarcas) {

            if (marca.getIdMarca()
                    == productoEncontrado.getIdMarca()) {

                nombreMarca = marca.getNombre();
                break;
            }
        }

        BigDecimal precioVenta
                = inventarioSeleccionado.getPrecioVenta();

        if (precioVenta == null) {
            precioVenta = BigDecimal.ZERO;
        }

        BigDecimal precioFinal
                = precioVenta;

        if (inventarioSeleccionado.isDescuentoActivo()
                && inventarioSeleccionado
                        .getDescuentoPorcentaje() != null) {

            BigDecimal porcentaje
                    = inventarioSeleccionado
                            .getDescuentoPorcentaje();

            BigDecimal montoDescuento
                    = precioVenta
                            .multiply(porcentaje)
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    java.math.RoundingMode.HALF_UP
                            );

            precioFinal
                    = precioVenta.subtract(
                            montoDescuento
                    );
        }

        precioFinal
                = precioFinal.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        txtLote.setText(
                inventarioSeleccionado.getLote()
        );

        txtNombreProducto.setText(
                productoEncontrado.getNombre()
        );

        txtMarca.setText(
                nombreMarca
        );

        txtUnidad.setText(
                productoEncontrado.getUnidad()
        );

        txtStockLote.setText(
                String.valueOf(
                        inventarioSeleccionado.getStock()
                )
        );

        txtDescripcionProducto.setText(
                productoEncontrado.getDescripcion() != null
                ? productoEncontrado.getDescripcion()
                : ""
        );

        txtDescuentoProducto.setText(
                obtenerTextoDescuento(
                        inventarioSeleccionado
                )
        );

        txtPrecioFinal.setText(
                precioFinal.toString()
        );

        txtCantidadVentaProducto.setText("");

        txtLote.setEditable(false);
        txtNombreProducto.setEditable(false);
        txtMarca.setEditable(false);
        txtUnidad.setEditable(false);
        txtStockLote.setEditable(false);
        txtDescripcionProducto.setEditable(false);
        txtDescuentoProducto.setEditable(false);
        txtPrecioFinal.setEditable(false);

        txtCantidadVentaProducto.setEditable(true);
        txtCantidadVentaProducto.requestFocus();

        txtDescuentoProducto.setEditable(false);
        txtPrecioFinal.setEditable(false);

        txtCantidadVentaProducto.setEditable(true);
        txtCantidadVentaProducto.requestFocus();
    }

    private void agregarProductoAResumenVenta() {

        if (tableBuscarProducto.getSelectedRow() == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un lote de la tabla de productos."
            );

            return;
        }

        String lote
                = txtLote.getText().trim();

        String nombreProducto
                = txtNombreProducto.getText().trim();

        String unidad
                = txtUnidad.getText().trim();

        String cantidadTexto
                = txtCantidadVentaProducto.getText().trim();

        if (cantidadTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese la cantidad que desea vender."
            );

            txtCantidadVentaProducto.requestFocus();
            return;
        }

        if (!cantidadTexto.matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe contener solo números."
            );

            txtCantidadVentaProducto.requestFocus();
            return;
        }

        int cantidad;

        try {

            cantidad = Integer.parseInt(
                    cantidadTexto
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad ingresada no es válida."
            );

            return;
        }

        if (cantidad <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe ser mayor que 0."
            );

            return;
        }

        /*
     * Buscar nuevamente el lote real.
         */
        Inventario inventarioSeleccionado = null;

        for (Inventario inventario : listaInventario) {

            if (inventario.getLote().equals(lote)
                    && inventario.isActivo()
                    && inventario.getStock() > 0) {

                inventarioSeleccionado = inventario;
                break;
            }
        }

        if (inventarioSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo identificar el lote seleccionado."
            );

            return;
        }

        int stockLote
                = inventarioSeleccionado.getStock();

        /*
     * Comprobar stock disponible.
         */
        if (cantidad > stockLote) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad no puede superar "
                    + "el stock disponible del lote."
            );

            txtCantidadVentaProducto.requestFocus();
            return;
        }

        /*
     * El precio normal siempre viene de Inventario.
         */
        BigDecimal precioVenta
                = inventarioSeleccionado.getPrecioVenta();

        if (precioVenta == null
                || precioVenta.compareTo(
                        BigDecimal.ZERO) <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "El lote no tiene un precio de venta válido."
            );

            return;
        }

        precioVenta
                = precioVenta.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
     * Determinar el porcentaje de descuento.
         */
        BigDecimal porcentajeDescuento
                = BigDecimal.ZERO;

        if (inventarioSeleccionado.isDescuentoActivo()
                && inventarioSeleccionado
                        .getDescuentoPorcentaje() != null) {

            porcentajeDescuento
                    = inventarioSeleccionado
                            .getDescuentoPorcentaje()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        /*
     * Calcular precio final por unidad.
         */
        BigDecimal descuentoUnitario
                = BigDecimal.ZERO;

        if (porcentajeDescuento.compareTo(
                BigDecimal.ZERO) > 0) {

            descuentoUnitario
                    = precioVenta
                            .multiply(
                                    porcentajeDescuento
                            )
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        BigDecimal precioFinal
                = precioVenta.subtract(
                        descuentoUnitario
                );

        precioFinal
                = precioFinal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
     * Subtotal normal de la línea.
         */
        BigDecimal subtotalNormal
                = precioVenta.multiply(
                        BigDecimal.valueOf(
                                cantidad
                        )
                );

        subtotalNormal
                = subtotalNormal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
     * Descuento total de la línea.
         */
        BigDecimal descuentoTotal
                = descuentoUnitario.multiply(
                        BigDecimal.valueOf(
                                cantidad
                        )
                );

        descuentoTotal
                = descuentoTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
     * Importe final de la línea.
         */
        BigDecimal importeFinal
                = subtotalNormal.subtract(
                        descuentoTotal
                );

        importeFinal
                = importeFinal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
     * Buscar si el mismo lote ya está en el resumen.
         */
        for (DetalleVenta detalle
                : listaDetallesVenta) {

            if (detalle.getIdInventario()
                    == inventarioSeleccionado
                            .getIdInventario()) {

                int cantidadAnterior
                        = detalle.getCantidad();

                int cantidadNueva
                        = cantidadAnterior + cantidad;

                if (cantidadNueva > stockLote) {

                    int disponible
                            = stockLote - cantidadAnterior;

                    JOptionPane.showMessageDialog(
                            this,
                            "No puede agregar esa cantidad."
                            + "\nStock disponible restante del lote: "
                            + disponible
                    );

                    txtCantidadVentaProducto.requestFocus();
                    return;
                }

                /*
             * Recalcular la línea completa
             * usando los datos actuales del lote.
                 */
                BigDecimal subtotalNormalNuevo
                        = precioVenta.multiply(
                                BigDecimal.valueOf(
                                        cantidadNueva
                                )
                        );

                BigDecimal descuentoTotalNuevo
                        = descuentoUnitario.multiply(
                                BigDecimal.valueOf(
                                        cantidadNueva
                                )
                        );

                BigDecimal importeFinalNuevo
                        = subtotalNormalNuevo.subtract(
                                descuentoTotalNuevo
                        );

                subtotalNormalNuevo
                        = subtotalNormalNuevo.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

                descuentoTotalNuevo
                        = descuentoTotalNuevo.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

                importeFinalNuevo
                        = importeFinalNuevo.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

                detalle.setCantidad(
                        cantidadNueva
                );

                /*
             * Guardamos el precio normal.
                 */
                detalle.setPrecioUnitario(
                        precioVenta
                );

                detalle.setSubtotal(
                        subtotalNormalNuevo
                );

                detalle.setDescuento(
                        descuentoTotalNuevo
                );

                detalle.setImporteTotal(
                        importeFinalNuevo
                );

                DefaultTableModel modelo
                        = (DefaultTableModel) tableResumenVenta.getModel();

                for (int i = 0;
                        i < modelo.getRowCount();
                        i++) {

                    if (modelo.getValueAt(i, 0)
                            .toString()
                            .equals(lote)) {

                        modelo.setValueAt(
                                cantidadNueva,
                                i,
                                3
                        );

                        modelo.setValueAt(
                                "S/ " + precioFinal,
                                i,
                                4
                        );

                        String textoDescuento;

                        if (porcentajeDescuento
                                .compareTo(
                                        BigDecimal.ZERO
                                ) > 0) {

                            textoDescuento
                                    = porcentajeDescuento
                                            .stripTrailingZeros()
                                            .toPlainString()
                                    + "%";

                        } else {

                            textoDescuento = "—";
                        }

                        modelo.setValueAt(
                                textoDescuento,
                                i,
                                5
                        );

                        modelo.setValueAt(
                                "S/ "
                                + importeFinalNuevo,
                                i,
                                6
                        );

                        break;
                    }
                }

                actualizarTotalesVenta();

                txtCantidadVentaProducto.setText("");

                return;
            }
        }

        /*
     * Crear una nueva línea.
         */
        DetalleVenta detalle
                = new DetalleVenta();

        detalle.setIdInventario(
                inventarioSeleccionado
                        .getIdInventario()
        );

        detalle.setCantidad(cantidad);

        /*
     * IMPORTANTE:
     * Se guarda el precio normal, no el promocional.
         */
        detalle.setPrecioUnitario(
                precioVenta
        );

        detalle.setSubtotal(
                subtotalNormal
        );

        detalle.setDescuento(
                descuentoTotal
        );

        detalle.setImporteTotal(
                importeFinal
        );

        listaDetallesVenta.add(
                detalle
        );

        DefaultTableModel modelo
                = (DefaultTableModel) tableResumenVenta.getModel();

        String textoDescuento;

        if (porcentajeDescuento.compareTo(
                BigDecimal.ZERO) > 0) {

            textoDescuento
                    = porcentajeDescuento
                            .stripTrailingZeros()
                            .toPlainString()
                    + "%";

        } else {

            textoDescuento = "—";
        }

        modelo.addRow(new Object[]{
            lote,
            nombreProducto,
            unidad,
            cantidad,
            "S/ " + precioFinal,
            textoDescuento,
            "S/ " + importeFinal
        });

        actualizarTotalesVenta();

        txtCantidadVentaProducto.setText("");
    }

    private void actualizarTotalesVenta() {

        BigDecimal importeTotal
                = BigDecimal.ZERO;

        for (DetalleVenta detalle : listaDetallesVenta) {

            BigDecimal importeDetalle
                    = detalle.getImporteTotal();

            if (importeDetalle == null) {

                importeDetalle
                        = BigDecimal.ZERO;
            }

            importeTotal
                    = importeTotal.add(
                            importeDetalle
                    );
        }

        importeTotal
                = importeTotal.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        // El precio final ya incluye IGV
        BigDecimal valorVenta
                = importeTotal.divide(
                        new BigDecimal("1.18"),
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        BigDecimal igv
                = importeTotal.subtract(
                        valorVenta
                );

        valorVenta
                = valorVenta.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        igv
                = igv.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        lblISubTotal.setText(
                "S/ " + valorVenta
        );

        lblIGV18.setText(
                "S/ " + igv
        );

        lblTotal.setText(
                "S/ " + importeTotal
        );
    }

    private void eliminarProductoResumenVenta() {

        int fila = tableResumenVenta.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un producto del resumen."
            );

            return;
        }

        int respuesta
                = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de eliminar este producto del resumen?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        String lote
                = tableResumenVenta
                        .getValueAt(fila, 0)
                        .toString();

        DetalleVenta detalleEliminar = null;

        for (DetalleVenta detalle : listaDetallesVenta) {

            for (Inventario inventario : listaInventario) {

                if (inventario.getIdInventario()
                        == detalle.getIdInventario()
                        && inventario.getLote().equals(lote)) {

                    detalleEliminar = detalle;
                    break;
                }
            }

            if (detalleEliminar != null) {
                break;
            }
        }

        if (detalleEliminar != null) {
            listaDetallesVenta.remove(detalleEliminar);
        }

        DefaultTableModel modelo
                = (DefaultTableModel) tableResumenVenta.getModel();

        modelo.removeRow(fila);

        actualizarTotalesVenta();
    }

    private void cancelarVenta() {

        int respuesta
                = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de cancelar la venta?\n"
                        + "Se eliminarán los productos del resumen.",
                        "Cancelar Venta",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        // Limpiar cliente
        txtDNIRUCCLiente.setText("");
        txtNombreRazonsocialCliente.setText("");
        txtApellidoContacto.setText("");

        // Limpiar producto seleccionado
        txtLote.setText("");
        txtNombreProducto.setText("");
        txtMarca.setText("");
        txtUnidad.setText("");
        txtStockLote.setText("");
        txtDescripcionProducto.setText("");

        // Limpiar campos editables
        txtCantidadVentaProducto.setText("");
        txtPrecioFinal.setText("");
        txtDescuentoProducto.setText("");

        // Vaciar lista de detalles
        listaDetallesVenta.clear();

        // Vaciar tabla de resumen
        DefaultTableModel modelo
                = (DefaultTableModel) tableResumenVenta.getModel();

        modelo.setRowCount(0);

        // Reiniciar totales
        actualizarTotalesVenta();

        // Quitar selecciones
        TableBuscarCliente.clearSelection();
        tableBuscarProducto.clearSelection();

        // Dejar nuevamente disponibles los campos
        txtCantidadVentaProducto.setEditable(true);

        txtCantidadVentaProducto.requestFocus();
    }

    private boolean validarVentaAntesDeCobrar() {

        // Validar cliente seleccionado
        int filaCliente
                = TableBuscarCliente.getSelectedRow();

        if (filaCliente == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente."
            );

            return false;
        }

        // Validar que haya productos en el resumen
        if (listaDetallesVenta.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Agregue al menos un producto al resumen de venta."
            );

            return false;
        }

        // Validar tipo de comprobante
        if (comboTipoBoletaFactura.getSelectedItem() == null
                || comboTipoBoletaFactura.getSelectedItem()
                        .toString()
                        .equals("Seleccionar")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione el tipo de comprobante."
            );

            return false;
        }

        // Validar método de pago
        if (comboMetododePago.getSelectedItem() == null
                || comboMetododePago.getSelectedItem()
                        .toString()
                        .equals("Seleccionar")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione el método de pago."
            );

            return false;
        }

        // Validar importe total
        if (lblTotal.getText() == null
                || lblTotal.getText()
                        .trim()
                        .isEmpty()
                || lblTotal.getText()
                        .equals("S/ 0.00")) {

            JOptionPane.showMessageDialog(
                    this,
                    "El importe total debe ser mayor que 0."
            );

            return false;
        }

        return true;
    }

    private void cobrarPedido() {

        if (!validarVentaAntesDeCobrar()) {
            return;
        }

        int idUsuario
                = SesionUsuario.getIdUsuario();

        if (idUsuario == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay un usuario con sesión iniciada."
            );

            return;
        }

        int filaCliente
                = TableBuscarCliente.getSelectedRow();

        int idCliente;

        try {

            idCliente
                    = Integer.parseInt(
                            TableBuscarCliente
                                    .getValueAt(filaCliente, 0)
                                    .toString()
                    );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo identificar el cliente seleccionado."
            );

            return;
        }

        String tipoComprobante
                = comboTipoBoletaFactura
                        .getSelectedItem()
                        .toString();

        String metodoPago
                = comboMetododePago
                        .getSelectedItem()
                        .toString();

        /*
     * Crear una nueva Venta.
         */
        Venta venta
                = new Venta();

        venta.setIdCliente(idCliente);
        venta.setIdUsuario(idUsuario);
        venta.setFechaVenta(
                java.time.LocalDateTime.now()
        );

        venta.setTipoComprobante(
                tipoComprobante
        );

        venta.setMetodoPago(
                metodoPago
        );

        venta.setEstadoComprobante(
                "Pendiente"
        );

        venta.setActivo(true);

        /*
     * Confirmación final.
         */
        BigDecimal importeTotal
                = obtenerTotalActualVenta();

        if (!confirmarCobro(
                venta,
                importeTotal)) {

            return;
        }

        /*
     * Registrar Venta + Detalles + Stock
     * dentro de una sola transacción.
         */
        boolean resultado;

try {

    resultado =
            ventaController.registrarVentaCompleta(
                    venta,
                    listaDetallesVenta
            );

} catch (RuntimeException e) {

    if (e.getMessage() != null
            && e.getMessage().contains(
                    "No existe una Caja abierta")) {

        JOptionPane.showMessageDialog(
                this,
                "No hay una Caja abierta."
                + "\nDebe abrir una Caja antes de realizar una venta.",
                "Caja cerrada",
                JOptionPane.WARNING_MESSAGE
        );

    } else {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar la venta."
                + "\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    return;
}

if (!resultado) {

    JOptionPane.showMessageDialog(
            this,
            "No se pudo registrar la venta."
            + "\nNo se realizaron cambios en el inventario.",
            "Error",
            JOptionPane.ERROR_MESSAGE
    );

    return;
}
        /*
     * Actualizar nuevamente Inventario desde BD.
         */
 /*
 * La venta ya fue registrada correctamente.
         */
        cargarDatosProductos();

        filtrarTablaBuscarProducto();

        JOptionPane.showMessageDialog(
                this,
                "Venta registrada correctamente."
                + "\n\n"
                + "Comprobante: "
                + venta.getNroComprobante()
                + "\n"
                + "Importe total: "
                + venta.getTotal(),
                "Venta registrada",
                JOptionPane.INFORMATION_MESSAGE
        );

        /*
 * Obtener el cliente de la venta.
         */
        Cliente clienteSeleccionado
                = buscarClientePorId(
                        idCliente
                );

        /*
 * Generar siempre el PDF y preguntar
 * solamente si desea abrirlo.
 *
 * IMPORTANTE:
 * Esto ocurre antes de limpiar
 * listaDetallesVenta.
         */
        generarYMostrarComprobante(
                venta,
                clienteSeleccionado
        );

        /*
 * Finalmente limpiar la venta.
         */
        limpiarVentaDespuesDeCobrar();
    }

    private void limpiarVentaDespuesDeCobrar() {

        // Cliente
        txtDNIRUCCLiente.setText("");
        txtNombreRazonsocialCliente.setText("");
        txtApellidoContacto.setText("");

        TableBuscarCliente.clearSelection();

        // Producto seleccionado
        txtLote.setText("");
        txtNombreProducto.setText("");
        txtMarca.setText("");
        txtUnidad.setText("");
        txtStockLote.setText("");
        txtDescripcionProducto.setText("");
        txtCantidadVentaProducto.setText("");
        txtPrecioFinal.setText("");
        txtDescuentoProducto.setText("");

        tableBuscarProducto.clearSelection();

        // Resumen
        listaDetallesVenta.clear();

        DefaultTableModel modelo
                = (DefaultTableModel) tableResumenVenta.getModel();

        modelo.setRowCount(0);

        actualizarTotalesVenta();

        // Comprobante y pago
        comboTipoBoletaFactura.setSelectedItem(
                "Seleccionar"
        );

        comboMetododePago.setSelectedItem(
                "Seleccionar"
        );
    }

    private String obtenerTextoDescuento(
            Inventario inventario) {

        if (!inventario.isDescuentoActivo()) {
            return "Desactivado";
        }

        if (inventario.getDescuentoPorcentaje() == null) {
            return "Activado";
        }

        return "Activado _"
                + inventario.getDescuentoPorcentaje()
                        .stripTrailingZeros()
                        .toPlainString()
                + "%";
    }

    private boolean confirmarCobro(
            Venta venta,
            BigDecimal importeTotal) {

        while (true) {

            javax.swing.JPanel panel
                    = new javax.swing.JPanel(
                            new java.awt.GridLayout(
                                    0,
                                    2,
                                    5,
                                    5
                            )
                    );

            panel.add(
                    new javax.swing.JLabel(
                            "Tipo de comprobante:"
                    )
            );

            panel.add(
                    new javax.swing.JLabel(
                            venta.getTipoComprobante()
                    )
            );

            panel.add(
                    new javax.swing.JLabel(
                            "Método de pago:"
                    )
            );

            panel.add(
                    new javax.swing.JLabel(
                            venta.getMetodoPago()
                    )
            );

            panel.add(
                    new javax.swing.JLabel(
                            "Importe total:"
                    )
            );

            panel.add(
                    new javax.swing.JLabel(
                            "S/ "
                            + importeTotal.setScale(
                                    2,
                                    java.math.RoundingMode.HALF_UP
                            )
                    )
            );

            javax.swing.JTextField txtMontoRecibido
                    = null;

            javax.swing.JLabel lblVuelto
                    = null;

            if ("Efectivo".equalsIgnoreCase(
                    venta.getMetodoPago())) {

                txtMontoRecibido
                        = new javax.swing.JTextField(
                                10
                        );

                lblVuelto
                        = new javax.swing.JLabel(
                                "S/ 0.00"
                        );

                panel.add(
                        new javax.swing.JLabel(
                                "Monto recibido:"
                        )
                );

                panel.add(
                        txtMontoRecibido
                );

                panel.add(
                        new javax.swing.JLabel(
                                "Vuelto:"
                        )
                );

                panel.add(
                        lblVuelto
                );

                final javax.swing.JLabel lblVueltoFinal
                        = lblVuelto;

                final javax.swing.JTextField txtMontoFinal
                        = txtMontoRecibido;

                txtMontoRecibido
                        .getDocument()
                        .addDocumentListener(
                                new javax.swing.event.DocumentListener() {

                            private void actualizarVuelto() {

                                String texto
                                        = txtMontoFinal
                                                .getText()
                                                .trim()
                                                .replace(
                                                        ",",
                                                        "."
                                                );

                                if (texto.isEmpty()) {

                                    lblVueltoFinal.setText(
                                            "S/ 0.00"
                                    );

                                    return;
                                }

                                try {

                                    BigDecimal monto
                                            = new BigDecimal(
                                                    texto
                                            );

                                    BigDecimal vuelto
                                            = monto.subtract(
                                                    importeTotal
                                            );

                                    lblVueltoFinal.setText(
                                            "S/ "
                                            + vuelto.setScale(
                                                    2,
                                                    java.math.RoundingMode.HALF_UP
                                            )
                                    );

                                } catch (NumberFormatException e) {

                                    lblVueltoFinal.setText(
                                            "S/ 0.00"
                                    );
                                }
                            }

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e) {

                                actualizarVuelto();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e) {

                                actualizarVuelto();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e) {

                                actualizarVuelto();
                            }
                        }
                        );
            }

            int respuesta
                    = JOptionPane.showConfirmDialog(
                            this,
                            panel,
                            "Confirmar Venta",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (respuesta
                    != JOptionPane.YES_OPTION) {

                return false;
            }

            /*
         * Pago en efectivo.
             */
            if ("Efectivo".equalsIgnoreCase(
                    venta.getMetodoPago())) {

                String texto
                        = txtMontoRecibido
                                .getText()
                                .trim()
                                .replace(
                                        ",",
                                        "."
                                );

                if (texto.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ingrese el monto recibido."
                    );

                    continue;
                }

                BigDecimal montoRecibido;

                try {

                    montoRecibido
                            = new BigDecimal(texto);

                } catch (NumberFormatException e) {

                    JOptionPane.showMessageDialog(
                            this,
                            "El monto recibido no es válido."
                    );

                    continue;
                }

                if (montoRecibido.compareTo(
                        BigDecimal.ZERO) <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "El monto recibido debe ser mayor que 0."
                    );

                    continue;
                }

                montoRecibido
                        = montoRecibido.setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        );

                if (montoRecibido.compareTo(
                        importeTotal) < 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "El monto recibido es insuficiente."
                            + "\nImporte total: S/ "
                            + importeTotal
                            + "\nMonto recibido: S/ "
                            + montoRecibido
                    );

                    continue;
                }

                BigDecimal vuelto
                        = montoRecibido.subtract(
                                importeTotal
                        );

                venta.setMontoRecibido(
                        montoRecibido
                );

                venta.setVuelto(
                        vuelto.setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        )
                );

            } else {

                /*
             * Tarjeta y transferencia
             * no utilizan monto recibido ni vuelto.
                 */
                venta.setMontoRecibido(
                        BigDecimal.ZERO
                );

                venta.setVuelto(
                        BigDecimal.ZERO
                );
            }

            return true;
        }
    }

    private BigDecimal obtenerTotalActualVenta() {

        BigDecimal total
                = BigDecimal.ZERO;

        for (DetalleVenta detalle
                : listaDetallesVenta) {

            if (detalle.getImporteTotal() != null) {

                total
                        = total.add(
                                detalle.getImporteTotal()
                        );
            }
        }

        return total.setScale(
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    private void actualizarVenta() {

        cargarClientes();

        cargarDatosProductos();

        cargarFiltrosProductos();

        cargarTablaBuscarProducto();
    }

    private void generarYMostrarComprobante(
            Venta venta,
            Cliente cliente) {

        if (venta == null || cliente == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo preparar el comprobante."
            );

            return;
        }

        String username = "";

        if (SesionUsuario.getUsuarioActual() != null) {

            username
                    = SesionUsuario
                            .getUsuarioActual()
                            .getUsername();
        }

        try {

            /*
         * Preparar los datos del comprobante
         * usando la venta recién registrada.
             */
            GeneradorComprobante.DatosComprobante datos
                    = GeneradorComprobante.preparar(
                            venta,
                            cliente,
                            listaDetallesVenta,
                            listaInventario,
                            listaProductos,
                            username
                    );

            /*
         * Generar siempre el PDF.
             */
            Path rutaPDF
                    = GeneradorPDF.generar(
                            datos
                    );

            /*
         * Preguntar solamente si desea abrirlo.
             */
            String nombreComprobante
                    = venta.getTipoComprobante()
                            .toLowerCase();

            int respuesta
                    = JOptionPane.showConfirmDialog(
                            this,
                            "¿Desea imprimir la "
                            + nombreComprobante
                            + "?\n\n"
                            + "Comprobante: "
                            + venta.getNroComprobante(),
                            "Imprimir comprobante",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (respuesta
                    != JOptionPane.YES_OPTION) {

                return;
            }

            /*
         * Abrir el PDF con el programa
         * predeterminado de Windows.
             */
            if (!Desktop.isDesktopSupported()
                    || !Desktop.getDesktop()
                            .isSupported(
                                    Desktop.Action.OPEN
                            )) {

                JOptionPane.showMessageDialog(
                        this,
                        "El PDF fue generado correctamente, "
                        + "pero no se pudo abrir automáticamente.\n\n"
                        + "Ubicación:\n"
                        + rutaPDF.toAbsolutePath()
                );

                return;
            }

            Desktop.getDesktop().open(
                    rutaPDF.toFile()
            );

        } catch (IOException
                | IllegalArgumentException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "La venta se registró correctamente, "
                    + "pero ocurrió un problema al generar "
                    + "o abrir el comprobante.\n\n"
                    + e.getMessage(),
                    "Comprobante",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Cliente buscarClientePorId(
            int idCliente) {

        for (Cliente cliente
                : listaClientes) {

            if (cliente.getIdCliente()
                    == idCliente) {

                return cliente;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableBuscarCliente = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarCliente = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableBuscarProducto = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtBuscarProductos = new javax.swing.JTextField();
        comboMarca = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        comboCategoria = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombreRazonsocialCliente = new javax.swing.JTextField();
        txtApellidoContacto = new javax.swing.JTextField();
        txtDNIRUCCLiente = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtLote = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        txtPrecioFinal = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtStockLote = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtCantidadVentaProducto = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDescripcionProducto = new javax.swing.JTextField();
        fsd = new javax.swing.JLabel();
        txtUnidad = new javax.swing.JTextField();
        txtDescuentoProducto = new javax.swing.JTextField();
        fsd1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnEliminarResumenVenta = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableResumenVenta = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        btnCancelarVenta = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblIGV18 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnCobrarPedido = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        lblISubTotal = new javax.swing.JLabel();
        comboMetododePago = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        comboTipoBoletaFactura = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        btnAgregarProductoaResumenVenta = new javax.swing.JButton();

        setBackground(new java.awt.Color(232, 245, 233));

        jPanel2.setBackground(new java.awt.Color(247, 252, 248));

        jPanel1.setBackground(new java.awt.Color(232, 245, 233));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Clientes "));
        jPanel1.setToolTipText("");

        TableBuscarCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI/RUC", "Nombre/Empresa", "Apellido/Contacto", "telefono"
            }
        ));
        TableBuscarCliente.setToolTipText("");
        TableBuscarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableBuscarClienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableBuscarCliente);

        jLabel1.setText("Buscador: ");

        txtBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarClienteActionPerformed(evt);
            }
        });
        txtBuscarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(232, 245, 233));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Productos"));
        jPanel3.setToolTipText("");

        tableBuscarProducto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lote", "Producto", "Categoria", "Unidad", "Marca", "StockLote", "StockProducto", "Precio"
            }
        ));
        tableBuscarProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableBuscarProductoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableBuscarProducto);

        jLabel2.setText("Buscador: ");

        txtBuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductosActionPerformed(evt);
            }
        });
        txtBuscarProductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductosKeyReleased(evt);
            }
        });

        comboMarca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMarcaActionPerformed(evt);
            }
        });

        jLabel17.setText("Marca");

        comboCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCategoriaActionPerformed(evt);
            }
        });

        jLabel19.setText("Categoria");

        btnActualizar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(68, 68, 68)
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 211, Short.MAX_VALUE)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(comboMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(btnActualizar))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(247, 252, 248));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen"));
        jPanel4.setToolTipText("");

        jPanel5.setBackground(new java.awt.Color(247, 252, 248));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos CLiente"));

        jLabel3.setText("DNI/RUC");

        jLabel4.setText("Nombre/Empresa");

        jLabel5.setText("Apellido/Contacto");

        txtNombreRazonsocialCliente.setEditable(false);
        txtNombreRazonsocialCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreRazonsocialClienteActionPerformed(evt);
            }
        });

        txtApellidoContacto.setEditable(false);

        txtDNIRUCCLiente.setEditable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDNIRUCCLiente, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreRazonsocialCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtApellidoContacto, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDNIRUCCLiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtNombreRazonsocialCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(232, 245, 233));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Producto"));

        jLabel7.setText("Lote");

        txtLote.setEditable(false);

        jLabel8.setText("Nombre");

        txtNombreProducto.setEditable(false);

        txtPrecioFinal.setEditable(false);

        jLabel10.setText("Stock");

        txtStockLote.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Precio");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel12.setText("Cantidad");

        jLabel18.setText("Descripcion");

        txtMarca.setEditable(false);
        txtMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarcaActionPerformed(evt);
            }
        });

        jLabel6.setText("Marca");

        fsd.setText("Unidad");

        txtUnidad.setEditable(false);

        txtDescuentoProducto.setEditable(false);

        fsd1.setText("Descuento");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStockLote, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLote, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantidadVentaProducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecioFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fsd1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescuentoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescripcionProducto))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fsd, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(txtUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtLote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(fsd)
                        .addComponent(txtUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtStockLote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(txtCantidadVentaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescuentoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fsd1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(232, 245, 233));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen de Venta"));

        btnEliminarResumenVenta.setText("Eliminar");
        btnEliminarResumenVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarResumenVentaActionPerformed(evt);
            }
        });

        tableResumenVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lote", "producto", "Unidad", "Cantidad", "P. Unitario", "PRECIO TOTAL"
            }
        ));
        jScrollPane3.setViewportView(tableResumenVenta);

        jLabel13.setText("Seleccionar para Eliminar");

        btnCancelarVenta.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        btnCancelarVenta.setText("CANCELAR VENTA");
        btnCancelarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(btnCancelarVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 180, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarResumenVenta)
                .addGap(25, 25, 25))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarResumenVenta)
                    .addComponent(jLabel13)
                    .addComponent(btnCancelarVenta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel14.setText("IGV(18%)");

        jLabel15.setText("IMPORTE TOTAL:");

        lblIGV18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblIGV18.setText("________");

        lblTotal.setBackground(new java.awt.Color(232, 245, 233));
        lblTotal.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblTotal.setText("________");

        btnCobrarPedido.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        btnCobrarPedido.setText("COBRAR PEDIDO");
        btnCobrarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarPedidoActionPerformed(evt);
            }
        });

        jLabel21.setText("Valor de Venta");

        lblISubTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblISubTotal.setText("________");

        comboMetododePago.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        comboMetododePago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Efectivo", "tarjeta", "Transferencia", "Yape" }));
        comboMetododePago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMetododePagoActionPerformed(evt);
            }
        });

        jLabel20.setText("Tipo de pago:");

        comboTipoBoletaFactura.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        comboTipoBoletaFactura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Boleta", "Factura" }));
        comboTipoBoletaFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTipoBoletaFacturaActionPerformed(evt);
            }
        });

        jLabel16.setText("Comprobante");

        btnAgregarProductoaResumenVenta.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnAgregarProductoaResumenVenta.setText("AGREGAR PRODUCTO");
        btnAgregarProductoaResumenVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoaResumenVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAgregarProductoaResumenVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboTipoBoletaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel16)))
                                .addGap(39, 39, 39)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(comboMetododePago, 0, 0, Short.MAX_VALUE))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCobrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblISubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                    .addComponent(lblIGV18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotal)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(lblIGV18)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel21)
                                    .addComponent(lblISubTotal)
                                    .addComponent(lblTotal)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAgregarProductoaResumenVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel20))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCobrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboTipoBoletaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboMetododePago, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TableBuscarClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableBuscarClienteMouseClicked
        cargarClienteSeleccionado();
        // TODO add your handling code here:
    }//GEN-LAST:event_TableBuscarClienteMouseClicked

    private void txtBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarClienteActionPerformed

    private void txtBuscarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyReleased
        buscarClientes();
    }//GEN-LAST:event_txtBuscarClienteKeyReleased

    private void tableBuscarProductoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableBuscarProductoMouseClicked
        cargarProductoSeleccionado();
        // TODO add your handling code here:
    }//GEN-LAST:event_tableBuscarProductoMouseClicked

    private void txtBuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductosActionPerformed

    private void txtBuscarProductosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductosKeyReleased
        filtrarTablaBuscarProducto();        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductosKeyReleased

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarVenta();              // TODO add your handling code here:
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtNombreRazonsocialClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreRazonsocialClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreRazonsocialClienteActionPerformed

    private void btnAgregarProductoaResumenVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoaResumenVentaActionPerformed
        agregarProductoAResumenVenta();        // TODO add your handling code here:
    }//GEN-LAST:event_btnAgregarProductoaResumenVentaActionPerformed

    private void comboTipoBoletaFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTipoBoletaFacturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboTipoBoletaFacturaActionPerformed

    private void txtMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarcaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMarcaActionPerformed

    private void comboMetododePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMetododePagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboMetododePagoActionPerformed

    private void btnEliminarResumenVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarResumenVentaActionPerformed
        eliminarProductoResumenVenta();
    }//GEN-LAST:event_btnEliminarResumenVentaActionPerformed

    private void btnCobrarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarPedidoActionPerformed
        cobrarPedido();
    }//GEN-LAST:event_btnCobrarPedidoActionPerformed

    private void btnCancelarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVentaActionPerformed
        cancelarVenta();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelarVentaActionPerformed

    private void comboMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMarcaActionPerformed
        filtrarTablaBuscarProducto();        // TODO add your handling code here:
    }//GEN-LAST:event_comboMarcaActionPerformed

    private void comboCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCategoriaActionPerformed
        filtrarTablaBuscarProducto();        // TODO add your handling code here:
    }//GEN-LAST:event_comboCategoriaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableBuscarCliente;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregarProductoaResumenVenta;
    private javax.swing.JButton btnCancelarVenta;
    private javax.swing.JButton btnCobrarPedido;
    private javax.swing.JButton btnEliminarResumenVenta;
    private javax.swing.JComboBox<String> comboCategoria;
    private javax.swing.JComboBox<String> comboMarca;
    private javax.swing.JComboBox<String> comboMetododePago;
    private javax.swing.JComboBox<String> comboTipoBoletaFactura;
    private javax.swing.JLabel fsd;
    private javax.swing.JLabel fsd1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblIGV18;
    private javax.swing.JLabel lblISubTotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tableBuscarProducto;
    private javax.swing.JTable tableResumenVenta;
    private javax.swing.JTextField txtApellidoContacto;
    private javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtBuscarProductos;
    private javax.swing.JTextField txtCantidadVentaProducto;
    private javax.swing.JTextField txtDNIRUCCLiente;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtDescuentoProducto;
    private javax.swing.JTextField txtLote;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtNombreRazonsocialCliente;
    private javax.swing.JTextField txtPrecioFinal;
    private javax.swing.JTextField txtStockLote;
    private javax.swing.JTextField txtUnidad;
    // End of variables declaration//GEN-END:variables
}
