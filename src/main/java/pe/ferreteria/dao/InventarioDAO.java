package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Inventario;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.reporte.RotacionProductoReporte;

public class InventarioDAO {

    public List<Inventario> listar() {

        List<Inventario> lista = new ArrayList<>();

        String sql = """
            SELECT
                id_inventario,
                id_producto,
                id_proveedor,
                lote,
                stock,
                stock_defectuoso,
                precio_compra,
                precio_venta,
                descuento_activo,
                descuento_porcentaje,
                fecha_ingreso,
                fecha_vencimiento,
                estante,
                nivel,
                activo
            FROM Inventario
            WHERE activo = 1
            ORDER BY id_inventario
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Inventario inventario =
                        new Inventario();

                inventario.setIdInventario(
                        rs.getInt("id_inventario")
                );

                inventario.setIdProducto(
                        rs.getInt("id_producto")
                );

                inventario.setIdProveedor(
                        rs.getInt("id_proveedor")
                );

                inventario.setLote(
                        rs.getString("lote")
                );

                inventario.setStock(
                        rs.getInt("stock")
                );

                inventario.setStockDefectuoso(
                        rs.getInt("stock_defectuoso")
                );

                inventario.setPrecioCompra(
                        rs.getBigDecimal("precio_compra")
                );

                inventario.setPrecioVenta(
                        rs.getBigDecimal("precio_venta")
                );

                inventario.setDescuentoActivo(
                        rs.getBoolean("descuento_activo")
                );

                inventario.setDescuentoPorcentaje(
                        rs.getBigDecimal(
                                "descuento_porcentaje"
                        )
                );

                if (rs.getTimestamp(
                        "fecha_ingreso") != null) {

                    inventario.setFechaIngreso(
                            rs.getTimestamp(
                                    "fecha_ingreso"
                            ).toLocalDateTime()
                    );
                }

                if (rs.getDate(
                        "fecha_vencimiento") != null) {

                    inventario.setFechaVencimiento(
                            rs.getDate(
                                    "fecha_vencimiento"
                            ).toLocalDate()
                    );
                }

                inventario.setEstante(
                        rs.getString("estante")
                );

                inventario.setNivel(
                        rs.getString("nivel")
                );

                inventario.setActivo(
                        rs.getBoolean("activo")
                );

                lista.add(inventario);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return lista;
    }
public int insertar(Inventario inventario) {

    try (Connection con = ConexionBD.conectar()) {

        return insertar(
                con,
                inventario
        );

    } catch (SQLException e) {

        e.printStackTrace();
        return 0;
    }
}
public int insertar(
        Connection con,
        Inventario inventario) {

    String sql = """
        INSERT INTO Inventario
        (id_producto,
         id_proveedor,
         lote,
         stock,
         stock_defectuoso,
         precio_compra,
         precio_venta,
         descuento_activo,
         descuento_porcentaje,
         fecha_vencimiento,
         estante,
         nivel)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps =
            con.prepareStatement(
                    sql,
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(
                1,
                inventario.getIdProducto()
        );

        ps.setInt(
                2,
                inventario.getIdProveedor()
        );

        ps.setString(
                3,
                inventario.getLote()
        );

        ps.setInt(
                4,
                inventario.getStock()
        );

        ps.setInt(
                5,
                inventario.getStockDefectuoso()
        );

        ps.setBigDecimal(
                6,
                inventario.getPrecioCompra()
        );

        ps.setBigDecimal(
                7,
                inventario.getPrecioVenta()
        );

        ps.setBoolean(
                8,
                inventario.isDescuentoActivo()
        );

        ps.setBigDecimal(
                9,
                inventario.getDescuentoPorcentaje()
        );

        if (inventario.getFechaVencimiento() != null) {

            ps.setDate(
                    10,
                    java.sql.Date.valueOf(
                            inventario.getFechaVencimiento()
                    )
            );

        } else {

            ps.setNull(
                    10,
                    java.sql.Types.DATE
            );
        }

        ps.setString(
                11,
                inventario.getEstante()
        );

        ps.setString(
                12,
                inventario.getNivel()
        );

        int filas =
                ps.executeUpdate();

        if (filas == 0) {
            return 0;
        }

        try (ResultSet rs =
                ps.getGeneratedKeys()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al registrar el inventario.",
                e
        );
    }

    return 0;
}


    public boolean actualizarUbicacion(
            int idInventario,
            String estante,
            String nivel) {

        String sql = """
            UPDATE Inventario
            SET estante = ?,
                nivel = ?
            WHERE id_inventario = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    estante
            );

            ps.setString(
                    2,
                    nivel
            );

            ps.setInt(
                    3,
                    idInventario
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarStock(
            int idInventario,
            int nuevoStock) {

        String sql = """
            UPDATE Inventario
            SET stock = ?
            WHERE id_inventario = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    nuevoStock
            );

            ps.setInt(
                    2,
                    idInventario
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizarStock(
        Connection con,
        int idInventario,
        int nuevoStock) {

    String sql = """
        UPDATE Inventario
        SET stock = ?
        WHERE id_inventario = ?
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                nuevoStock
        );

        ps.setInt(
                2,
                idInventario
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al actualizar el stock del inventario.",
                e
        );
    }
}

    public boolean actualizarDescuento(
            int idInventario,
            boolean descuentoActivo,
            BigDecimal descuentoPorcentaje) {

        String sql = """
            UPDATE Inventario
            SET descuento_activo = ?,
                descuento_porcentaje = ?
            WHERE id_inventario = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setBoolean(
                    1,
                    descuentoActivo
            );

            ps.setBigDecimal(
                    2,
                    descuentoPorcentaje
            );

            ps.setInt(
                    3,
                    idInventario
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarStockDefectuoso(
            int idInventario,
            int nuevoStockDefectuoso) {

        String sql = """
            UPDATE Inventario
            SET stock_defectuoso = ?
            WHERE id_inventario = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    nuevoStockDefectuoso
            );

            ps.setInt(
                    2,
                    idInventario
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizarStockDefectuoso(
        Connection con,
        int idInventario,
        int nuevoStockDefectuoso) {

    String sql = """
        UPDATE Inventario
        SET stock_defectuoso = ?
        WHERE id_inventario = ?
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                nuevoStockDefectuoso
        );

        ps.setInt(
                2,
                idInventario
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al actualizar el stock defectuoso.",
                e
        );
    }
}

    public boolean reducirStock(
            Connection con,
            int idInventario,
            int cantidad) {

        String sql = """
            UPDATE Inventario
            SET stock = stock - ?
            WHERE id_inventario = ?
              AND stock >= ?
              AND activo = 1
            """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    cantidad
            );

            ps.setInt(
                    2,
                    idInventario
            );

            ps.setInt(
                    3,
                    cantidad
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al reducir el stock del inventario.",
                    e
            );
        }
    }
    public Inventario buscarPorId(
        Connection con,
        int idInventario) {

    String sql = """
        SELECT
            id_inventario,
            id_producto,
            id_proveedor,
            lote,
            stock,
            stock_defectuoso,
            precio_compra,
            precio_venta,
            descuento_activo,
            descuento_porcentaje,
            fecha_ingreso,
            fecha_vencimiento,
            estante,
            nivel,
            activo
        FROM Inventario
        WHERE id_inventario = ?
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(1, idInventario);

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {

                Inventario inventario =
                        new Inventario();

                inventario.setIdInventario(
                        rs.getInt("id_inventario")
                );

                inventario.setIdProducto(
                        rs.getInt("id_producto")
                );

                inventario.setIdProveedor(
                        rs.getInt("id_proveedor")
                );

                inventario.setLote(
                        rs.getString("lote")
                );

                inventario.setStock(
                        rs.getInt("stock")
                );

                inventario.setStockDefectuoso(
                        rs.getInt("stock_defectuoso")
                );

                inventario.setPrecioCompra(
                        rs.getBigDecimal("precio_compra")
                );

                inventario.setPrecioVenta(
                        rs.getBigDecimal("precio_venta")
                );

                inventario.setDescuentoActivo(
                        rs.getBoolean("descuento_activo")
                );

                inventario.setDescuentoPorcentaje(
                        rs.getBigDecimal(
                                "descuento_porcentaje"
                        )
                );

                if (rs.getTimestamp(
                        "fecha_ingreso") != null) {

                    inventario.setFechaIngreso(
                            rs.getTimestamp(
                                    "fecha_ingreso"
                            ).toLocalDateTime()
                    );
                }

                if (rs.getDate(
                        "fecha_vencimiento") != null) {

                    inventario.setFechaVencimiento(
                            rs.getDate(
                                    "fecha_vencimiento"
                            ).toLocalDate()
                    );
                }

                inventario.setEstante(
                        rs.getString("estante")
                );

                inventario.setNivel(
                        rs.getString("nivel")
                );

                inventario.setActivo(
                        rs.getBoolean("activo")
                );

                return inventario;
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar el inventario.",
                e
        );
    }

    return null;
}
    public boolean aumentarStock(
        Connection con,
        int idInventario,
        int cantidad) {

    String sql = """
        UPDATE Inventario
        SET stock = stock + ?
        WHERE id_inventario = ?
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                cantidad
        );

        ps.setInt(
                2,
                idInventario
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al aumentar el stock del inventario.",
                e
        );
    }
}
    public Inventario buscarParaDashboard(int idInventario) {

    String sql = """
        SELECT
            id_inventario,
            id_producto,
            id_proveedor,
            lote,
            stock,
            stock_defectuoso,
            precio_compra,
            precio_venta,
            descuento_activo,
            descuento_porcentaje,
            fecha_ingreso,
            fecha_vencimiento,
            estante,
            nivel,
            activo
        FROM Inventario
        WHERE id_inventario = ?
          AND activo = 1
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setInt(1, idInventario);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Inventario inventario = new Inventario();

                inventario.setIdInventario(
                        rs.getInt("id_inventario")
                );

                inventario.setIdProducto(
                        rs.getInt("id_producto")
                );

                inventario.setIdProveedor(
                        rs.getInt("id_proveedor")
                );

                inventario.setLote(
                        rs.getString("lote")
                );

                inventario.setStock(
                        rs.getInt("stock")
                );

                inventario.setStockDefectuoso(
                        rs.getInt("stock_defectuoso")
                );

                inventario.setPrecioCompra(
                        rs.getBigDecimal("precio_compra")
                );

                inventario.setPrecioVenta(
                        rs.getBigDecimal("precio_venta")
                );

                inventario.setDescuentoActivo(
                        rs.getBoolean("descuento_activo")
                );

                inventario.setDescuentoPorcentaje(
                        rs.getBigDecimal(
                                "descuento_porcentaje"
                        )
                );

                if (rs.getTimestamp(
                        "fecha_ingreso") != null) {

                    inventario.setFechaIngreso(
                            rs.getTimestamp(
                                    "fecha_ingreso"
                            ).toLocalDateTime()
                    );
                }

                if (rs.getDate(
                        "fecha_vencimiento") != null) {

                    inventario.setFechaVencimiento(
                            rs.getDate(
                                    "fecha_vencimiento"
                            ).toLocalDate()
                    );
                }

                inventario.setEstante(
                        rs.getString("estante")
                );

                inventario.setNivel(
                        rs.getString("nivel")
                );

                inventario.setActivo(
                        rs.getBoolean("activo")
                );

                return inventario;
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar el inventario por ID.",
                e
        );
    }

    return null;
}
    public List<RotacionProductoReporte> listarRotacionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<RotacionProductoReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            i.id_inventario,
            i.id_producto,
            p.nombre AS producto,
            m.nombre AS marca,
            i.lote,
            DATEDIFF(
                CURRENT_DATE,
                DATE(i.fecha_ingreso)
            ) AS dias_en_inventario,
            i.stock,
            COALESCE(
                SUM(
                    CASE
                        WHEN dv.id_detalle IS NOT NULL
                        THEN dv.cantidad
                        ELSE 0
                    END
                ),
                0
            ) AS vendido
        FROM Inventario i
        INNER JOIN Producto p
            ON p.id_producto = i.id_producto
        INNER JOIN Marca m
            ON m.id_marca = p.id_marca
        LEFT JOIN Detalle_Venta dv
            ON dv.id_inventario = i.id_inventario
        LEFT JOIN Venta v
            ON v.id_venta = dv.id_venta
           AND v.activo = 1
        WHERE i.activo = 1
        """);

    /*
     * El período se aplica sobre las ventas.
     * Si no hubo ventas, el producto sigue apareciendo.
     */

    if (mes != null) {
        sql.append("""
            AND (
                v.id_venta IS NULL
                OR MONTH(v.fecha_venta) = ?
            )
            """);
    }

    if (dia != null) {
        sql.append("""
            AND (
                v.id_venta IS NULL
                OR DAY(v.fecha_venta) = ?
            )
            """);
    }

    sql.append("""
        AND (
            v.id_venta IS NULL
            OR YEAR(v.fecha_venta) = ?
        )
        """);

    if (idUsuario != null) {
        sql.append("""
            AND (
                v.id_venta IS NULL
                OR v.id_usuario = ?
            )
            """);
    }

    sql.append("""
        GROUP BY
            i.id_inventario,
            i.id_producto,
            p.nombre,
            m.nombre,
            i.lote,
            i.fecha_ingreso,
            i.stock
        HAVING vendido <= 2
        ORDER BY vendido ASC,
                 dias_en_inventario DESC,
                 p.nombre ASC
        """);

    try (
            Connection con = ConexionBD.conectar();
            PreparedStatement ps =
                    con.prepareStatement(sql.toString())
    ) {

        int parametro = 1;

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        ps.setInt(parametro++, anio);

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                RotacionProductoReporte reporte =
                        new RotacionProductoReporte();

                reporte.setIdInventario(
                        rs.getInt("id_inventario")
                );

                reporte.setIdProducto(
                        rs.getInt("id_producto")
                );

                reporte.setProducto(
                        rs.getString("producto")
                );

                reporte.setMarca(
                        rs.getString("marca")
                );

                reporte.setLote(
                        rs.getString("lote")
                );

                reporte.setDiasEnInventario(
                        rs.getLong("dias_en_inventario")
                );

                reporte.setStock(
                        rs.getInt("stock")
                );

                int vendido =
                        rs.getInt("vendido");

                reporte.setVendido(vendido);

                reporte.setSituacion(
                        vendido == 0
                                ? "Sin movimiento"
                                : "Bajo movimiento"
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar la rotación "
                + "de productos para el reporte.",
                e
        );
    }

    return lista;
}
    public int obtenerStockDefectuosoActual() {

    String sql = """
        SELECT COALESCE(
            SUM(stock_defectuoso),
            0
        )
        FROM Inventario
        WHERE activo = 1
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al obtener el stock defectuoso actual.",
                e
        );
    }

    return 0;
}
}