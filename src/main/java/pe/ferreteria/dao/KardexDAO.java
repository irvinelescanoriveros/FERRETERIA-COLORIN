package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Kardex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.reporte.MermaReporte;

public class KardexDAO {

    /*
     * ========================================
     * INSERTAR
     * ========================================
     */

    public boolean insertar(Kardex kardex) {

        try (Connection cn =
                ConexionBD.conectar()) {

            return insertar(
                    cn,
                    kardex
            );

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar movimiento Kardex: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean insertar(
            Connection cn,
            Kardex kardex) {

        String sql = """
                INSERT INTO Kardex (
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?
                )
                """;

        try (PreparedStatement ps =
                cn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    kardex.getIdProducto()
            );

            ps.setInt(
                    2,
                    kardex.getIdInventario()
            );

            ps.setInt(
                    3,
                    kardex.getIdUsuario()
            );

            if (kardex.getFechaMovimiento()
                    != null) {

                ps.setTimestamp(
                        4,
                        Timestamp.valueOf(
                                kardex.getFechaMovimiento()
                        )
                );

            } else {

                ps.setTimestamp(
                        4,
                        new Timestamp(
                                System.currentTimeMillis()
                        )
                );
            }

            ps.setString(
                    5,
                    kardex.getMovimiento()
            );

            ps.setString(
                    6,
                    kardex.getMotivoMovimiento()
            );

            ps.setInt(
                    7,
                    kardex.getCantidad()
            );

            ps.setInt(
                    8,
                    kardex.getStockAntes()
            );

            ps.setInt(
                    9,
                    kardex.getStockDespues()
            );

            ps.setInt(
                    10,
                    kardex.getStockDefectuosoAntes()
            );

            ps.setInt(
                    11,
                    kardex.getStockDefectuosoDespues()
            );

            ps.setBigDecimal(
                    12,
                    kardex.getPrecioUnitario()
            );

            ps.setString(
                    13,
                    kardex.getTipoReferencia()
            );

            if (kardex.getIdReferencia() != null
        && kardex.getIdReferencia() > 0) {

    ps.setInt(
            14,
            kardex.getIdReferencia()
    );

} else {

    ps.setNull(
            14,
            java.sql.Types.INTEGER
    );
}

            if (kardex.getNroComprobante()
                    != null
                    && !kardex.getNroComprobante()
                            .isBlank()) {

                ps.setString(
                        15,
                        kardex.getNroComprobante()
                );

            } else {

                ps.setNull(
                        15,
                        java.sql.Types.VARCHAR
                );
            }

            if (kardex.getMotivo()
                    != null
                    && !kardex.getMotivo()
                            .isBlank()) {

                ps.setString(
                        16,
                        kardex.getMotivo()
                );

            } else {

                ps.setNull(
                        16,
                        java.sql.Types.VARCHAR
                );
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al registrar movimiento Kardex.",
                    e
            );
        }
    }

    /*
     * ========================================
     * LISTAR TODOS
     * ========================================
     */

    public List<Kardex> listar() {

        List<Kardex> lista =
                new ArrayList<>();

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                ORDER BY fecha_movimiento DESC,
                         id_kardex DESC
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        convertirResultado(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar Kardex: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    /*
     * ========================================
     * BUSCAR POR ID
     * ========================================
     */

    public Kardex buscarPorId(
            int idKardex) {

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                WHERE id_kardex = ?
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idKardex
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return convertirResultado(
                            rs
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar movimiento Kardex.",
                    e
            );
        }

        return null;
    }

    /*
     * ========================================
     * LISTAR POR INVENTARIO / LOTE
     * ========================================
     */

    public List<Kardex> listarPorInventario(
            int idInventario) {

        List<Kardex> lista =
                new ArrayList<>();

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                WHERE id_inventario = ?
                ORDER BY fecha_movimiento DESC,
                         id_kardex DESC
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idInventario
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    lista.add(
                            convertirResultado(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar Kardex del lote.",
                    e
            );
        }

        return lista;
    }

    /*
     * ========================================
     * LISTAR POR PRODUCTO
     * ========================================
     */

    public List<Kardex> listarPorProducto(
            int idProducto) {

        List<Kardex> lista =
                new ArrayList<>();

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                WHERE id_producto = ?
                ORDER BY fecha_movimiento DESC,
                         id_kardex DESC
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProducto
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    lista.add(
                            convertirResultado(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar Kardex del producto.",
                    e
            );
        }

        return lista;
    }

    /*
     * ========================================
     * CANTIDAD YA INGRESADA A DEFECTUOSOS
     * PARA UNA VENTA Y UN LOTE
     * ========================================
     *
     * Se utiliza para controlar devoluciones
     * parciales del cliente.
     *
     * Ejemplo:
     *
     * Venta: 4 unidades
     * Primera devolución: 2
     * Segunda devolución: máximo 2
     */

    public int obtenerCantidadDefectuosaPorVenta(
            int idVenta,
            int idInventario) {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(
                            stock_defectuoso_despues
                            -
                            stock_defectuoso_antes
                        ),
                        0
                    )
                FROM Kardex
                WHERE tipo_referencia = 'Venta'
                  AND id_referencia = ?
                  AND id_inventario = ?
                  AND movimiento = 'Ajuste'
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idVenta
            );

            ps.setInt(
                    2,
                    idInventario
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al consultar cantidad "
                    + "defectuosa de la venta.",
                    e
            );
        }

        return 0;
    }
public int obtenerCantidadReingresadaDefectuoso(
        Connection con,
        int idKardexDefectuoso) {

    String sql = """
            SELECT COALESCE(
                SUM(cantidad),
                0
            )
            FROM Kardex
            WHERE movimiento = 'Entrada'
              AND tipo_referencia = 'Ajuste'
              AND id_referencia = ?
              AND stock_defectuoso_despues
                    < stock_defectuoso_antes
            """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                idKardexDefectuoso
        );

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al consultar la cantidad "
                + "reingresada del producto defectuoso.",
                e
        );
    }

    return 0;
}
    /*
     * ========================================
     * CONVERTIR RESULTSET → KARDEX
     * ========================================
     */

    private Kardex convertirResultado(
            ResultSet rs)
            throws SQLException {

        Kardex kardex =
                new Kardex();

        kardex.setIdKardex(
                rs.getInt(
                        "id_kardex"
                )
        );

        kardex.setIdProducto(
                rs.getInt(
                        "id_producto"
                )
        );

        kardex.setIdInventario(
                rs.getInt(
                        "id_inventario"
                )
        );

        kardex.setIdUsuario(
                rs.getInt(
                        "id_usuario"
                )
        );

        Timestamp fecha =
                rs.getTimestamp(
                        "fecha_movimiento"
                );

        if (fecha != null) {

            kardex.setFechaMovimiento(
                    fecha.toLocalDateTime()
            );
        }

        kardex.setMovimiento(
                rs.getString(
                        "movimiento"
                )
        );

        kardex.setMotivoMovimiento(
                rs.getString(
                        "motivo_movimiento"
                )
        );

        kardex.setCantidad(
                rs.getInt(
                        "cantidad"
                )
        );

        kardex.setStockAntes(
                rs.getInt(
                        "stock_antes"
                )
        );

        kardex.setStockDespues(
                rs.getInt(
                        "stock_despues"
                )
        );

        kardex.setStockDefectuosoAntes(
                rs.getInt(
                        "stock_defectuoso_antes"
                )
        );

        kardex.setStockDefectuosoDespues(
                rs.getInt(
                        "stock_defectuoso_despues"
                )
        );

        kardex.setPrecioUnitario(
                rs.getBigDecimal(
                        "precio_unitario"
                )
        );

        kardex.setMontoTotal(
                rs.getBigDecimal(
                        "monto_total"
                )
        );

        kardex.setTipoReferencia(
                rs.getString(
                        "tipo_referencia"
                )
        );

        int idReferencia =
                rs.getInt(
                        "id_referencia"
                );

        if (!rs.wasNull()) {

            kardex.setIdReferencia(
                    idReferencia
            );

        } else {

            kardex.setIdReferencia(
                    0
            );
        }

        kardex.setNroComprobante(
                rs.getString(
                        "nro_comprobante"
                )
        );

        kardex.setMotivo(
                rs.getString(
                        "motivo"
                )
        );

        return kardex;
    }

    /*
     * ========================================
     * BUSCAR POR ID - CONEXIÓN EXISTENTE
     * ========================================
     */

    public Kardex buscarPorId(
            Connection con,
            int idKardex) {

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                WHERE id_kardex = ?
                """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idKardex
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {

                    return convertirResultado(
                            rs
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar movimiento Kardex.",
                    e
            );
        }

        return null;
    }

    /*
     * ========================================
     * COMPROBAR REVERSION
     * ========================================
     */

    public boolean existeReversion(
            Connection con,
            int idVenta,
            int idInventario) {

        String sql = """
                SELECT COUNT(*)
                FROM Kardex
                WHERE movimiento = 'Reversion'
                  AND tipo_referencia = 'Venta'
                  AND id_referencia = ?
                  AND id_inventario = ?
                """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idVenta
            );

            ps.setInt(
                    2,
                    idInventario
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al comprobar la reversión.",
                    e
            );
        }

        return false;
    }

    /*
     * ========================================
     * CANTIDAD RETIRADA DE DEFECTUOSOS
     * ========================================
     */

    public int obtenerCantidadRetiradaDefectuoso(
            Connection con,
            int idKardexDefectuoso) {

        String sql = """
                SELECT COALESCE(
                    SUM(
                        stock_defectuoso_antes
                        -
                        stock_defectuoso_despues
                    ),
                    0
                )
                FROM Kardex
                WHERE movimiento = 'Ajuste'
                  AND tipo_referencia = 'Ajuste'
                  AND id_referencia = ?
                  AND stock_defectuoso_despues
                        < stock_defectuoso_antes
                """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idKardexDefectuoso
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al consultar el stock defectuoso retirado.",
                    e
            );
        }

        return 0;
    }
/*
 * ========================================
 * CANTIDAD YA REPUESTA
 * DE UNA DEVOLUCIÓN DEFECTUOSA
 * ========================================
 */

public int obtenerCantidadRepuestaDefectuoso(
        Connection con,
        int idKardexDefectuoso) {

    String sql = """
            SELECT COALESCE(
                SUM(cantidad),
                0
            )
            FROM Kardex
            WHERE movimiento = 'Salida'
              AND motivo_movimiento = 'Reposicion p. client.'
              AND tipo_referencia = 'Ajuste'
              AND id_referencia = ?
            """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                idKardexDefectuoso
        );

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {

                return rs.getInt(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al consultar la cantidad "
                + "ya repuesta del producto defectuoso.",
                e
        );
    }

    return 0;
}
    /*
     * ========================================
     * LISTAR INGRESOS DEFECTUOSOS
     * POR INVENTARIO
     * ========================================
     */

    public List<Kardex> listarIngresosDefectuososPorInventario(
            int idInventario) {

        List<Kardex> lista =
                new ArrayList<>();

        String sql = """
                SELECT
                    id_kardex,
                    id_producto,
                    id_inventario,
                    id_usuario,
                    fecha_movimiento,
                    movimiento,
                    motivo_movimiento,
                    cantidad,
                    stock_antes,
                    stock_despues,
                    stock_defectuoso_antes,
                    stock_defectuoso_despues,
                    precio_unitario,
                    monto_total,
                    tipo_referencia,
                    id_referencia,
                    nro_comprobante,
                    motivo
                FROM Kardex
                WHERE id_inventario = ?
                  AND movimiento = 'Ajuste'
                  AND tipo_referencia = 'Venta'
                  AND stock_defectuoso_despues
                        > stock_defectuoso_antes
                ORDER BY fecha_movimiento ASC,
                         id_kardex ASC
                """;

        try (
                Connection cn =
                        ConexionBD.conectar();

                PreparedStatement ps =
                        cn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idInventario
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    lista.add(
                            convertirResultado(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar ingresos a stock defectuoso.",
                    e
            );
        }

        return lista;
    }
    /*
 * ========================================
 * LISTAR MERMAS PARA REPORTE
 * ========================================
 *
 * Merma:
 * - Dar de baja
 * - Retiro Defectuoso
 *
 * No incluye:
 * - Correccion
 * - Producto Defectuoso
 */

public List<Kardex> listarMermasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<Kardex> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
            SELECT
                k.id_kardex,
                k.id_producto,
                k.id_inventario,
                k.id_usuario,
                k.fecha_movimiento,
                k.movimiento,
                k.motivo_movimiento,
                k.cantidad,
                k.stock_antes,
                k.stock_despues,
                k.stock_defectuoso_antes,
                k.stock_defectuoso_despues,
                k.precio_unitario,
                k.monto_total,
                k.tipo_referencia,
                k.id_referencia,
                k.nro_comprobante,
                k.motivo
            FROM Kardex k
            WHERE k.movimiento = 'Ajuste'
              AND k.motivo_movimiento IN (
                    'Dar de baja',
                    'Retiro Defectuoso'
              )
              AND YEAR(k.fecha_movimiento) = ?
            """);

    if (mes != null) {
        sql.append(
                " AND MONTH(k.fecha_movimiento) = ? "
        );
    }

    if (dia != null) {
        sql.append(
                " AND DAY(k.fecha_movimiento) = ? "
        );
    }

    if (idUsuario != null) {
        sql.append(
                " AND k.id_usuario = ? "
        );
    }

    sql.append("""
            ORDER BY k.fecha_movimiento ASC,
                     k.id_kardex ASC
            """);

    try (
            Connection cn =
                    ConexionBD.conectar();

            PreparedStatement ps =
                    cn.prepareStatement(
                            sql.toString()
                    )
    ) {

        int parametro = 1;

        ps.setInt(
                parametro++,
                anio
        );

        if (mes != null) {

            ps.setInt(
                    parametro++,
                    mes
            );
        }

        if (dia != null) {

            ps.setInt(
                    parametro++,
                    dia
            );
        }

        if (idUsuario != null) {

            ps.setInt(
                    parametro++,
                    idUsuario
            );
        }

        try (
                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        convertirResultado(rs)
                );
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar las mermas para el reporte.",
                e
        );
    }

    return lista;
}
public List<MermaReporte> listarMermaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<MermaReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
            SELECT
                k.id_kardex,
                k.id_producto,
                p.nombre AS producto,
                m.nombre AS marca,
                k.motivo_movimiento,
                k.cantidad,
                k.monto_total AS costo_merma
            FROM Kardex k
            INNER JOIN Producto p
                ON p.id_producto = k.id_producto
            INNER JOIN Marca m
                ON m.id_marca = p.id_marca
            WHERE k.movimiento = 'Ajuste'
              AND k.motivo_movimiento IN (
                    'Dar de baja',
                    'Retiro Defectuoso'
              )
              AND YEAR(k.fecha_movimiento) = ?
            """);

    if (mes != null) {
        sql.append(
                " AND MONTH(k.fecha_movimiento) = ? "
        );
    }

    if (dia != null) {
        sql.append(
                " AND DAY(k.fecha_movimiento) = ? "
        );
    }

    if (idUsuario != null) {
        sql.append(
                " AND k.id_usuario = ? "
        );
    }

    sql.append("""
            ORDER BY k.fecha_movimiento ASC,
                     k.id_kardex ASC
            """);

    try (
            Connection cn =
                    ConexionBD.conectar();

            PreparedStatement ps =
                    cn.prepareStatement(
                            sql.toString()
                    )
    ) {

        int parametro = 1;

        ps.setInt(
                parametro++,
                anio
        );

        if (mes != null) {

            ps.setInt(
                    parametro++,
                    mes
            );
        }

        if (dia != null) {

            ps.setInt(
                    parametro++,
                    dia
            );
        }

        if (idUsuario != null) {

            ps.setInt(
                    parametro++,
                    idUsuario
            );
        }

        try (ResultSet rs =
                ps.executeQuery()) {

            while (rs.next()) {

                MermaReporte reporte =
                        new MermaReporte();

                reporte.setIdKardex(
                        rs.getInt("id_kardex")
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

                reporte.setMotivo(
                        rs.getString("motivo_movimiento")
                );

                reporte.setCantidad(
                        rs.getInt("cantidad")
                );

                reporte.setCostoMerma(
                        rs.getBigDecimal("costo_merma")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar las mermas "
                + "para el reporte gerencial.",
                e
        );
    }

    return lista;
}
}