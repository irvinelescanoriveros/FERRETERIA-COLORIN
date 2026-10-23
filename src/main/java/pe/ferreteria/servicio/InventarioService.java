package pe.ferreteria.servicio;

import pe.ferreteria.dao.InventarioDAO;
import pe.ferreteria.modelo.Inventario;
import java.sql.Connection;
import java.math.BigDecimal;
import java.util.List;
import pe.ferreteria.reporte.RotacionProductoReporte;

public class InventarioService {

    private final InventarioDAO inventarioDAO;

    public InventarioService() {
        inventarioDAO = new InventarioDAO();
    }

    public List<Inventario> listar() {
        return inventarioDAO.listar();
    }

    public int registrarInventario(
            pe.ferreteria.modelo.DetalleCompra detalleCompra,
            pe.ferreteria.modelo.Compra compra,
            pe.ferreteria.modelo.Producto producto,
            int cantidadRecibida) {

        if (detalleCompra == null
                || compra == null
                || producto == null
                || cantidadRecibida <= 0) {

            return 0;
        }

        BigDecimal precioCompra =
                detalleCompra.getPrecioCompra();

        BigDecimal precioVenta =
                precioCompra.multiply(
                        new BigDecimal("1.20")
                );

        Inventario inventario =
                new Inventario();

        inventario.setIdProducto(
                detalleCompra.getIdProducto()
        );

        inventario.setIdProveedor(
                compra.getIdProveedor()
        );

        inventario.setLote(
                detalleCompra.getLote()
        );

        inventario.setStock(
                cantidadRecibida
        );

        /*
         * Todo lote nuevo comienza sin productos
         * defectuosos.
         */
        inventario.setStockDefectuoso(0);

        inventario.setPrecioCompra(
                precioCompra
        );

        inventario.setPrecioVenta(
                precioVenta
        );

        /*
         * Todo lote nuevo comienza con el
         * descuento desactivado.
         */
        inventario.setDescuentoActivo(false);

        inventario.setDescuentoPorcentaje(
                BigDecimal.ZERO
        );

        inventario.setFechaVencimiento(
                detalleCompra.getFechaVencimiento()
        );

        inventario.setEstante(
                producto.getEstante()
        );

        inventario.setNivel(
                producto.getNivel()
        );

        inventario.setActivo(true);

        return inventarioDAO.insertar(
                inventario
        );
    }
public int registrarInventario(
        Connection con,
        pe.ferreteria.modelo.DetalleCompra detalleCompra,
        pe.ferreteria.modelo.Compra compra,
        pe.ferreteria.modelo.Producto producto,
        int cantidadRecibida) {

    if (detalleCompra == null
            || compra == null
            || producto == null
            || cantidadRecibida <= 0) {

        return 0;
    }

    BigDecimal precioCompra =
            detalleCompra.getPrecioCompra();

    if (precioCompra == null) {
        precioCompra = BigDecimal.ZERO;
    }

    BigDecimal precioVenta =
            precioCompra.multiply(
                    new BigDecimal("1.20")
            );

    Inventario inventario =
            new Inventario();

    inventario.setIdProducto(
            detalleCompra.getIdProducto()
    );

    inventario.setIdProveedor(
            compra.getIdProveedor()
    );

    inventario.setLote(
            detalleCompra.getLote()
    );

    inventario.setStock(
            cantidadRecibida
    );

    inventario.setStockDefectuoso(0);

    inventario.setPrecioCompra(
            precioCompra
    );

    inventario.setPrecioVenta(
            precioVenta
    );

    inventario.setDescuentoActivo(false);

    inventario.setDescuentoPorcentaje(
            BigDecimal.ZERO
    );

    inventario.setFechaVencimiento(
            detalleCompra.getFechaVencimiento()
    );

    inventario.setEstante(
            producto.getEstante()
    );

    inventario.setNivel(
            producto.getNivel()
    );

    inventario.setActivo(true);

    return inventarioDAO.insertar(
            con,
            inventario
    );
}
    public boolean actualizarUbicacion(
            int idInventario,
            String estante,
            String nivel) {

        if (idInventario <= 0) {
            return false;
        }

        if (estante == null
                || estante.trim().isEmpty()) {

            return false;
        }

        if (nivel == null
                || nivel.trim().isEmpty()) {

            return false;
        }

        return inventarioDAO.actualizarUbicacion(
                idInventario,
                estante.trim(),
                nivel.trim()
        );
    }

    public boolean actualizarStock(
            int idInventario,
            int nuevoStock) {

        if (idInventario <= 0) {
            return false;
        }

        if (nuevoStock < 0) {
            return false;
        }

        if (pe.ferreteria.infraestructura.SesionUsuario
                .getUsuarioActual() == null) {

            return false;
        }

        return inventarioDAO.actualizarStock(
                idInventario,
                nuevoStock
        );
    }

    public boolean activarDescuento(
            int idInventario,
            BigDecimal porcentaje) {

        if (idInventario <= 0) {
            return false;
        }

        if (pe.ferreteria.infraestructura.SesionUsuario
                .getUsuarioActual() == null) {

            return false;
        }

        if (porcentaje == null) {
            return false;
        }

        porcentaje =
                porcentaje.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        if (porcentaje.compareTo(
                BigDecimal.ZERO) <= 0) {

            return false;
        }

        if (porcentaje.compareTo(
                new BigDecimal("100")) > 0) {

            return false;
        }

        return inventarioDAO.actualizarDescuento(
                idInventario,
                true,
                porcentaje
        );
    }

    public boolean desactivarDescuento(
            int idInventario) {

        if (idInventario <= 0) {
            return false;
        }

        if (pe.ferreteria.infraestructura.SesionUsuario
                .getUsuarioActual() == null) {

            return false;
        }

        return inventarioDAO.actualizarDescuento(
                idInventario,
                false,
                BigDecimal.ZERO
        );
    }
    public boolean retirarStockDefectuoso(
        int idInventario,
        int cantidad) {

    if (idInventario <= 0) {
        return false;
    }

    if (cantidad <= 0) {
        return false;
    }

    if (pe.ferreteria.infraestructura.SesionUsuario
            .getUsuarioActual() == null) {
        return false;
    }

    Inventario inventarioEncontrado = null;

    for (Inventario inventario : inventarioDAO.listar()) {

        if (inventario.getIdInventario()
                == idInventario) {

            inventarioEncontrado = inventario;
            break;
        }
    }

    if (inventarioEncontrado == null) {
        return false;
    }

    if (cantidad >
            inventarioEncontrado
                    .getStockDefectuoso()) {

        return false;
    }

    return inventarioDAO.actualizarStockDefectuoso(
            idInventario,
            inventarioEncontrado
                    .getStockDefectuoso()
                    - cantidad
    );
}
    public Inventario buscarParaDashboard(int idInventario) {

    if (idInventario <= 0) {
        return null;
    }

    return inventarioDAO.buscarParaDashboard(
            idInventario
    );
}
    public List<RotacionProductoReporte> listarRotacionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    return inventarioDAO.listarRotacionParaReporte(
            dia,
            mes,
            anio,
            idUsuario
    );
}
    public int obtenerStockDefectuosoActual() {
    return inventarioDAO.obtenerStockDefectuosoActual();
}
}