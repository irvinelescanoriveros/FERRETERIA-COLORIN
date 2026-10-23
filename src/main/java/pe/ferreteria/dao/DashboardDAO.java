package pe.ferreteria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Dashboard;

public class DashboardDAO {

    /*
     * =========================================================
     * LISTAR DATOS GLOBALES DEL DASHBOARD
     * =========================================================
     *
     * Una fila = una línea de venta.
     *
     * Toda la información queda disponible para que
     * DashboardPanel pueda trabajar posteriormente
     * en memoria sin realizar nuevas consultas.
     */

    public List<Dashboard> listar() {

        List<Dashboard> lista = new ArrayList<>();

        String sql = """
            SELECT
                v.id_venta,
                dv.id_detalle,
                v.fecha_venta,
                v.nro_comprobante,

                u.id_usuario,

                CONCAT_WS(
                    ' ',
                    u.nombre,
                    u.apellidos
                ) AS usuario,

                p.id_producto,
                p.nombre AS producto,

                dv.cantidad,

                (
                    dv.cantidad
                    * i.precio_compra
                ) AS costo,

                dv.importe_total AS venta,

                (
                    dv.importe_total
                    -
                    (
                        dv.cantidad
                        * i.precio_compra
                    )
                ) AS ganancia

            FROM Venta v

            INNER JOIN Usuario u
                ON u.id_usuario = v.id_usuario

            INNER JOIN Detalle_Venta dv
                ON dv.id_venta = v.id_venta

            INNER JOIN Inventario i
                ON i.id_inventario = dv.id_inventario

            INNER JOIN Producto p
                ON p.id_producto = i.id_producto

            WHERE v.activo = true

            ORDER BY
                v.fecha_venta DESC,
                v.id_venta DESC,
                dv.id_detalle ASC
            """;

        try (
                Connection cn = ConexionBD.conectar();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Dashboard dashboard = new Dashboard();

                dashboard.setIdVenta(
                        rs.getInt("id_venta")
                );

                dashboard.setIdDetalle(
                        rs.getInt("id_detalle")
                );

                Timestamp fecha =
                        rs.getTimestamp("fecha_venta");

                if (fecha != null) {

                    dashboard.setFechaVenta(
                            fecha.toLocalDateTime()
                    );
                }

                dashboard.setComprobante(
                        rs.getString("nro_comprobante")
                );

                dashboard.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                dashboard.setUsuario(
                        rs.getString("usuario")
                );

                dashboard.setIdProducto(
                        rs.getInt("id_producto")
                );

                dashboard.setProducto(
                        rs.getString("producto")
                );

                dashboard.setCantidad(
                        rs.getInt("cantidad")
                );

                dashboard.setCosto(
                        rs.getBigDecimal("costo")
                );

                dashboard.setVenta(
                        rs.getBigDecimal("venta")
                );

                dashboard.setGanancia(
                        rs.getBigDecimal("ganancia")
                );

                lista.add(dashboard);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al cargar los datos "
                    + "del Dashboard.",
                    e
            );
        }

        return lista;
    }
}