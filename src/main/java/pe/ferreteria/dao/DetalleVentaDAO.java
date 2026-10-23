package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.reporte.ProductoMasVendidoReporte;

public class DetalleVentaDAO {

    public int insertar(DetalleVenta detalleVenta) {

        String sql = """
            INSERT INTO Detalle_Venta
            (id_venta,
             id_inventario,
             cantidad,
             precio_unitario,
             descuento)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(
                sql,
                java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, detalleVenta.getIdVenta());
            ps.setInt(2, detalleVenta.getIdInventario());
            ps.setInt(3, detalleVenta.getCantidad());

            ps.setBigDecimal(
                    4,
                    detalleVenta.getPrecioUnitario()
            );

            ps.setBigDecimal(
                    5,
                    detalleVenta.getDescuento() == null
                    ? java.math.BigDecimal.ZERO
                    : detalleVenta.getDescuento()
            );

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs
                    = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int insertar(
            Connection con,
            DetalleVenta detalleVenta) {

        String sql = """
            INSERT INTO Detalle_Venta
            (id_venta,
             id_inventario,
             cantidad,
             precio_unitario,
             descuento)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps
                = con.prepareStatement(
                        sql,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, detalleVenta.getIdVenta());
            ps.setInt(2, detalleVenta.getIdInventario());
            ps.setInt(3, detalleVenta.getCantidad());

            ps.setBigDecimal(
                    4,
                    detalleVenta.getPrecioUnitario()
            );

            ps.setBigDecimal(
                    5,
                    detalleVenta.getDescuento() == null
                    ? java.math.BigDecimal.ZERO
                    : detalleVenta.getDescuento()
            );

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs
                    = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al registrar el detalle de venta.",
                    e
            );
        }

        return 0;
    }

    public DetalleVenta buscarPorVentaEInventario(
            int idVenta,
            int idInventario) {

        String sql = """
        SELECT
            id_detalle,
            id_venta,
            id_inventario,
            cantidad,
            precio_unitario,
            descuento
        FROM Detalle_Venta
        WHERE id_venta = ?
          AND id_inventario = ?
        LIMIT 1
        """;

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ps.setInt(2, idInventario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    DetalleVenta detalle
                            = new DetalleVenta();

                    detalle.setIdDetalle(
                            rs.getInt("id_detalle")
                    );

                    detalle.setIdVenta(
                            rs.getInt("id_venta")
                    );

                    detalle.setIdInventario(
                            rs.getInt("id_inventario")
                    );

                    detalle.setCantidad(
                            rs.getInt("cantidad")
                    );

                    detalle.setPrecioUnitario(
                            rs.getBigDecimal(
                                    "precio_unitario"
                            )
                    );

                    detalle.setDescuento(
                            rs.getBigDecimal(
                                    "descuento"
                            )
                    );

                    return detalle;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al consultar el detalle de la venta.",
                    e
            );
        }

        return null;
    }

    public List<DetalleVenta> listarPorVenta(int idVenta) {

        List<DetalleVenta> lista = new ArrayList<>();

        String sql = """
        SELECT
            id_detalle,
            id_venta,
            id_inventario,
            cantidad,
            precio_unitario,
            subtotal,
            descuento,
            importe_total
        FROM Detalle_Venta
        WHERE id_venta = ?
        ORDER BY id_detalle
        """;

        try (Connection con = ConexionBD.conectar(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    DetalleVenta detalle
                            = new DetalleVenta();

                    detalle.setIdDetalle(
                            rs.getInt("id_detalle")
                    );

                    detalle.setIdVenta(
                            rs.getInt("id_venta")
                    );

                    detalle.setIdInventario(
                            rs.getInt("id_inventario")
                    );

                    detalle.setCantidad(
                            rs.getInt("cantidad")
                    );

                    detalle.setPrecioUnitario(
                            rs.getBigDecimal("precio_unitario")
                    );

                    detalle.setSubtotal(
                            rs.getBigDecimal("subtotal")
                    );

                    detalle.setDescuento(
                            rs.getBigDecimal("descuento")
                    );

                    detalle.setImporteTotal(
                            rs.getBigDecimal("importe_total")
                    );

                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar los detalles de la venta.",
                    e
            );
        }

        return lista;
    }
public List<DetalleVenta> listarParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<DetalleVenta> lista = new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            dv.id_detalle,
            dv.id_venta,
            dv.id_inventario,
            dv.cantidad,
            dv.precio_unitario,
            dv.subtotal,
            dv.descuento,
            dv.importe_total
        FROM Detalle_Venta dv
        INNER JOIN Venta v
            ON v.id_venta = dv.id_venta
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(v.fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(v.fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND v.id_usuario = ? ");
    }

    sql.append("""
        ORDER BY v.fecha_venta ASC,
                 v.id_venta ASC,
                 dv.id_detalle ASC
        """);

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps =
                 con.prepareStatement(sql.toString())) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                DetalleVenta detalle =
                        new DetalleVenta();

                detalle.setIdDetalle(
                        rs.getInt("id_detalle")
                );

                detalle.setIdVenta(
                        rs.getInt("id_venta")
                );

                detalle.setIdInventario(
                        rs.getInt("id_inventario")
                );

                detalle.setCantidad(
                        rs.getInt("cantidad")
                );

                detalle.setPrecioUnitario(
                        rs.getBigDecimal(
                                "precio_unitario"
                        )
                );

                detalle.setSubtotal(
                        rs.getBigDecimal(
                                "subtotal"
                        )
                );

                detalle.setDescuento(
                        rs.getBigDecimal(
                                "descuento"
                        )
                );

                detalle.setImporteTotal(
                        rs.getBigDecimal(
                                "importe_total"
                        )
                );

                lista.add(detalle);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar los detalles "
                + "de venta para el reporte.",
                e
        );
    }

    return lista;
}
public List<ProductoMasVendidoReporte> listarProductosMasVendidosParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<ProductoMasVendidoReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            p.id_producto,
            p.nombre AS producto,
            m.nombre AS marca,
            SUM(dv.cantidad) AS cantidad_vendida,
            SUM(dv.importe_total) AS total_vendido,
            SUM(
                dv.importe_total
                - (dv.cantidad * i.precio_compra)
            ) AS ganancia_bruta
        FROM Detalle_Venta dv
        INNER JOIN Venta v
            ON v.id_venta = dv.id_venta
        INNER JOIN Inventario i
            ON i.id_inventario = dv.id_inventario
        INNER JOIN Producto p
            ON p.id_producto = i.id_producto
        INNER JOIN Marca m
            ON m.id_marca = p.id_marca
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(v.fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(v.fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND v.id_usuario = ? ");
    }

    sql.append("""
        GROUP BY
            p.id_producto,
            p.nombre,
            m.nombre
        ORDER BY cantidad_vendida DESC,
                 total_vendido DESC
        """);

    try (
            Connection con = ConexionBD.conectar();
            PreparedStatement ps =
                    con.prepareStatement(sql.toString())
    ) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ProductoMasVendidoReporte reporte =
                        new ProductoMasVendidoReporte();

                reporte.setIdProducto(
                        rs.getInt("id_producto")
                );

                reporte.setProducto(
                        rs.getString("producto")
                );

                reporte.setMarca(
                        rs.getString("marca")
                );

                reporte.setCantidadVendida(
                        rs.getInt("cantidad_vendida")
                );

                reporte.setTotalVendido(
                        rs.getBigDecimal("total_vendido")
                );

                reporte.setGananciaBruta(
                        rs.getBigDecimal("ganancia_bruta")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar los productos "
                + "más vendidos para el reporte.",
                e
        );
    }

    return lista;
}
}
