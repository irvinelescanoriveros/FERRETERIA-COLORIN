package pe.ferreteria.dao;

import java.math.BigDecimal;
import pe.ferreteria.modelo.MovimientoCaja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoCajaDAO {

    /*
     * Registrar movimiento de Caja.
     */
    public boolean insertar(
            Connection con,
            MovimientoCaja movimiento) {

        String sql = """
            INSERT INTO Movimiento_Caja (
                id_caja,
                id_usuario,
                fecha_movimiento,
                tipo_movimiento,
                concepto,
                monto,
                metodo_pago,
                tipo_referencia,
                id_referencia,
                id_venta,
                id_compra,
                nro_comprobante,
                descripcion,
                activo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    movimiento.getIdCaja()
            );

            ps.setInt(
                    2,
                    movimiento.getIdUsuario()
            );

            if (movimiento.getFechaMovimiento() != null) {

                ps.setTimestamp(
                        3,
                        java.sql.Timestamp.valueOf(
                                movimiento.getFechaMovimiento()
                        )
                );

            } else {

                ps.setTimestamp(
                        3,
                        new java.sql.Timestamp(
                                System.currentTimeMillis()
                        )
                );
            }

            ps.setString(
                    4,
                    movimiento.getTipoMovimiento()
            );

            ps.setString(
                    5,
                    movimiento.getConcepto()
            );

            ps.setBigDecimal(
                    6,
                    movimiento.getMonto()
            );

            ps.setString(
                    7,
                    movimiento.getMetodoPago()
            );

            ps.setString(
                    8,
                    movimiento.getTipoReferencia()
            );

            if (movimiento.getIdReferencia() != null) {

                ps.setInt(
                        9,
                        movimiento.getIdReferencia()
                );

            } else {

                ps.setNull(
                        9,
                        java.sql.Types.INTEGER
                );
            }

            if (movimiento.getIdVenta() != null) {

                ps.setInt(
                        10,
                        movimiento.getIdVenta()
                );

            } else {

                ps.setNull(
                        10,
                        java.sql.Types.INTEGER
                );
            }

            if (movimiento.getIdCompra() != null) {

                ps.setInt(
                        11,
                        movimiento.getIdCompra()
                );

            } else {

                ps.setNull(
                        11,
                        java.sql.Types.INTEGER
                );
            }

            ps.setString(
                    12,
                    movimiento.getNroComprobante()
            );

            ps.setString(
                    13,
                    movimiento.getDescripcion()
            );

            ps.setBoolean(
                    14,
                    movimiento.isActivo()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al registrar el movimiento de Caja.",
                    e
            );
        }
    }

    /*
     * Listar movimientos de una Caja.
     */
    public List<MovimientoCaja> listarPorCaja(
            int idCaja) {

        List<MovimientoCaja> lista =
                new ArrayList<>();

        String sql = """
            SELECT
                id_movimiento_caja,
                id_caja,
                id_usuario,
                fecha_movimiento,
                tipo_movimiento,
                concepto,
                monto,
                metodo_pago,
                tipo_referencia,
                id_referencia,
                id_venta,
                id_compra,
                nro_comprobante,
                descripcion,
                activo
            FROM Movimiento_Caja
            WHERE id_caja = ?
              AND activo = 1
            ORDER BY fecha_movimiento DESC,
                     id_movimiento_caja DESC
            """;

        try (Connection con =
                pe.ferreteria.infraestructura.ConexionBD
                        .conectar();
             PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idCaja
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(
                            convertirResultado(rs)
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return lista;
    }

    /*
     * Convertir resultado SQL a objeto.
     */
    private MovimientoCaja convertirResultado(
            ResultSet rs) throws SQLException {

        MovimientoCaja movimiento =
                new MovimientoCaja();

        movimiento.setIdMovimientoCaja(
                rs.getInt("id_movimiento_caja")
        );

        movimiento.setIdCaja(
                rs.getInt("id_caja")
        );

        movimiento.setIdUsuario(
                rs.getInt("id_usuario")
        );

        if (rs.getTimestamp(
                "fecha_movimiento") != null) {

            movimiento.setFechaMovimiento(
                    rs.getTimestamp(
                            "fecha_movimiento"
                    ).toLocalDateTime()
            );
        }

        movimiento.setTipoMovimiento(
                rs.getString("tipo_movimiento")
        );

        movimiento.setConcepto(
                rs.getString("concepto")
        );

        movimiento.setMonto(
                rs.getBigDecimal("monto")
        );

        movimiento.setMetodoPago(
                rs.getString("metodo_pago")
        );

        movimiento.setTipoReferencia(
                rs.getString("tipo_referencia")
        );

        int idReferencia =
                rs.getInt("id_referencia");

        if (!rs.wasNull()) {

            movimiento.setIdReferencia(
                    idReferencia
            );
        }

        int idVenta =
                rs.getInt("id_venta");

        if (!rs.wasNull()) {

            movimiento.setIdVenta(
                    idVenta
            );
        }

        int idCompra =
                rs.getInt("id_compra");

        if (!rs.wasNull()) {

            movimiento.setIdCompra(
                    idCompra
            );
        }

        movimiento.setNroComprobante(
                rs.getString("nro_comprobante")
        );

        movimiento.setDescripcion(
                rs.getString("descripcion")
        );

        movimiento.setActivo(
                rs.getBoolean("activo")
        );

        return movimiento;
    }
    /*
 * Obtener total de ingresos de una Caja.
 */
public BigDecimal obtenerTotalIngresos(
        Connection con,
        int idCaja) {

    String sql = """
        SELECT COALESCE(SUM(monto), 0)
        FROM Movimiento_Caja
        WHERE id_caja = ?
          AND tipo_movimiento = 'Ingreso'
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(1, idCaja);

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al obtener los ingresos de la Caja.",
                e
        );
    }

    return BigDecimal.ZERO;
}

/*
 * Obtener total de salidas de una Caja.
 */
public BigDecimal obtenerTotalSalidas(
        Connection con,
        int idCaja) {

    String sql = """
        SELECT COALESCE(SUM(monto), 0)
        FROM Movimiento_Caja
        WHERE id_caja = ?
          AND tipo_movimiento = 'Salida'
          AND activo = 1
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(1, idCaja);

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al obtener las salidas de la Caja.",
                e
        );
    }

    return BigDecimal.ZERO;
}
/*
 * ========================================
 * TOTAL DEVUELTO POR KARDEX DEFECTUOSO
 * ========================================
 */

public BigDecimal obtenerTotalDevolucionDefectuoso(
        Connection con,
        int idKardexDefectuoso) {

    String sql = """
            SELECT COALESCE(
                SUM(monto),
                0
            )
            FROM Movimiento_Caja
            WHERE tipo_movimiento = 'Salida'
              AND concepto = 'Devolucion'
              AND tipo_referencia = 'Kardex'
              AND id_referencia = ?
              AND activo = 1
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

                return rs.getBigDecimal(1);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al consultar el total "
                + "devuelto por producto defectuoso.",
                e
        );
    }

    return BigDecimal.ZERO;
}
}