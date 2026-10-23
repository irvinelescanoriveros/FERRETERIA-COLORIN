package pe.ferreteria.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pe.ferreteria.modelo.Cliente;
import pe.ferreteria.modelo.DetalleVenta;
import pe.ferreteria.modelo.Inventario;
import pe.ferreteria.modelo.Producto;
import pe.ferreteria.modelo.Venta;

public class GeneradorComprobante {

    private GeneradorComprobante() {
    }

    /*
     * Prepara todos los datos necesarios para generar
     * posteriormente el PDF del comprobante.
     */
    public static DatosComprobante preparar(
            Venta venta,
            Cliente cliente,
            List<DetalleVenta> detalles,
            List<Inventario> inventarios,
            List<Producto> productos,
            String usuario) {

        if (venta == null) {
            throw new IllegalArgumentException(
                    "La venta no puede ser nula."
            );
        }

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        if (detalles == null
                || detalles.isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta no tiene detalles."
            );
        }

        List<ItemComprobante> items =
                new ArrayList<>();

        for (DetalleVenta detalle : detalles) {

            if (detalle == null) {
                continue;
            }

            Inventario inventarioEncontrado =
                    buscarInventario(
                            detalle.getIdInventario(),
                            inventarios
                    );

            if (inventarioEncontrado == null) {
                continue;
            }

            Producto productoEncontrado =
                    buscarProducto(
                            inventarioEncontrado.getIdProducto(),
                            productos
                    );

            if (productoEncontrado == null) {
                continue;
            }

            BigDecimal precioUnitario =
                    detalle.getPrecioUnitario();

            if (precioUnitario == null) {
                precioUnitario = BigDecimal.ZERO;
            }

            BigDecimal descuento =
                    detalle.getDescuento();

            if (descuento == null) {
                descuento = BigDecimal.ZERO;
            }

            BigDecimal importeTotal =
                    detalle.getImporteTotal();

            /*
             * Si por alguna razón todavía no existe
             * importeTotal en memoria, lo calculamos.
             */
            if (importeTotal == null) {

                BigDecimal subtotal =
                        precioUnitario.multiply(
                                BigDecimal.valueOf(
                                        detalle.getCantidad()
                                )
                        );

                importeTotal =
                        subtotal.subtract(
                                descuento
                        );
            }

            items.add(
                    new ItemComprobante(
                            inventarioEncontrado.getLote(),
                            productoEncontrado.getNombre(),
                            productoEncontrado.getUnidad(),
                            detalle.getCantidad(),
                            precioUnitario,
                            descuento,
                            importeTotal
                    )
            );
        }

        /*
         * Datos del cliente.
         *
         * Persona Natural:
         * Cliente + DNI
         *
         * Persona Jurídica:
         * Cliente + Contacto + RUC
         */
        String nombreCliente;
        String contactoCliente;
        String identificacionCliente;

        if ("Natural".equalsIgnoreCase(
                cliente.getTipoCliente())) {

            nombreCliente =
                    unir(
                            cliente.getNombre(),
                            cliente.getApellidos()
                    );

            contactoCliente = "";

            identificacionCliente =
                    safe(
                            cliente.getDni()
                    );

        } else {

            nombreCliente =
                    safe(
                            cliente.getRazonSocial()
                    );

            contactoCliente =
                    safe(
                            cliente.getContacto()
                    );

            identificacionCliente =
                    safe(
                            cliente.getRuc()
                    );
        }

        return new DatosComprobante(
                venta.getTipoComprobante(),
                venta.getNroComprobante(),
                venta.getFechaVenta(),
                venta.getMetodoPago(),
                safe(usuario),
                nombreCliente,
                contactoCliente,
                identificacionCliente,
                items,
                valor(
                        venta.getSubtotal()
                ),
                valor(
                        venta.getIgv()
                ),
                valor(
                        venta.getTotal()
                ),
                valor(
                        venta.getMontoRecibido()
                ),
                valor(
                        venta.getVuelto()
                )
        );
    }

    private static Inventario buscarInventario(
            int idInventario,
            List<Inventario> inventarios) {

        if (inventarios == null) {
            return null;
        }

        for (Inventario inventario : inventarios) {

            if (inventario != null
                    && inventario.getIdInventario()
                            == idInventario) {

                return inventario;
            }
        }

        return null;
    }

    private static Producto buscarProducto(
            int idProducto,
            List<Producto> productos) {

        if (productos == null) {
            return null;
        }

        for (Producto producto : productos) {

            if (producto != null
                    && producto.getIdProducto()
                            == idProducto) {

                return producto;
            }
        }

        return null;
    }

    private static String unir(
            String primero,
            String segundo) {

        String resultado =
                safe(primero).trim();

        String parteDos =
                safe(segundo).trim();

        if (!parteDos.isEmpty()) {

            if (!resultado.isEmpty()) {
                resultado += " ";
            }

            resultado += parteDos;
        }

        return resultado;
    }

    private static String safe(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    private static BigDecimal valor(
            BigDecimal numero) {

        return numero == null
                ? BigDecimal.ZERO
                : numero;
    }

    public static class DatosComprobante {

        private final String tipoComprobante;
        private final String nroComprobante;
        private final java.time.LocalDateTime fechaVenta;
        private final String metodoPago;
        private final String usuario;
        private final String cliente;
        private final String contactoCliente;
        private final String identificacionCliente;

        private final List<ItemComprobante> items;

        private final BigDecimal subtotal;
        private final BigDecimal igv;
        private final BigDecimal total;
        private final BigDecimal montoRecibido;
        private final BigDecimal vuelto;

        public DatosComprobante(
                String tipoComprobante,
                String nroComprobante,
                java.time.LocalDateTime fechaVenta,
                String metodoPago,
                String usuario,
                String cliente,
                String contactoCliente,
                String identificacionCliente,
                List<ItemComprobante> items,
                BigDecimal subtotal,
                BigDecimal igv,
                BigDecimal total,
                BigDecimal montoRecibido,
                BigDecimal vuelto) {

            this.tipoComprobante = tipoComprobante;
            this.nroComprobante = nroComprobante;
            this.fechaVenta = fechaVenta;
            this.metodoPago = metodoPago;
            this.usuario = usuario;
            this.cliente = cliente;
            this.contactoCliente = contactoCliente;
            this.identificacionCliente =
                    identificacionCliente;
            this.items = items;
            this.subtotal = subtotal;
            this.igv = igv;
            this.total = total;
            this.montoRecibido = montoRecibido;
            this.vuelto = vuelto;
        }

        public String getTipoComprobante() {
            return tipoComprobante;
        }

        public String getNroComprobante() {
            return nroComprobante;
        }

        public java.time.LocalDateTime getFechaVenta() {
            return fechaVenta;
        }

        public String getMetodoPago() {
            return metodoPago;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getCliente() {
            return cliente;
        }

        public String getContactoCliente() {
            return contactoCliente;
        }

        public String getIdentificacionCliente() {
            return identificacionCliente;
        }

        public List<ItemComprobante> getItems() {
            return items;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public BigDecimal getIgv() {
            return igv;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public BigDecimal getMontoRecibido() {
            return montoRecibido;
        }

        public BigDecimal getVuelto() {
            return vuelto;
        }
    }

    public static class ItemComprobante {

        private final String lote;
        private final String producto;
        private final String unidad;
        private final int cantidad;
        private final BigDecimal precioUnitario;
        private final BigDecimal descuento;
        private final BigDecimal importeTotal;

        public ItemComprobante(
                String lote,
                String producto,
                String unidad,
                int cantidad,
                BigDecimal precioUnitario,
                BigDecimal descuento,
                BigDecimal importeTotal) {

            this.lote = lote;
            this.producto = producto;
            this.unidad = unidad;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.descuento = descuento;
            this.importeTotal = importeTotal;
        }

        public String getLote() {
            return lote;
        }

        public String getProducto() {
            return producto;
        }

        public String getUnidad() {
            return unidad;
        }

        public int getCantidad() {
            return cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public BigDecimal getImporteTotal() {
            return importeTotal;
        }
    }
}