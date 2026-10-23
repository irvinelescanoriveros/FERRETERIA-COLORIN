package pe.ferreteria.dao;

import java.math.BigDecimal;
import pe.ferreteria.modelo.Caja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CajaDAO {

    /*
     * Registrar apertura de Caja.
     */
    public boolean insertar(
            Connection con,
            Caja caja) {

        String sql = """
            INSERT INTO Caja (
                id_usuario_apertura,
                id_usuario_cierre,
                fecha_apertura,
                fecha_cierre,
                monto_apertura,
                total_ingresos,
                total_salidas,
                monto_esperado,
                monto_contado,
                diferencia,
                estado,
                observacion
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    caja.getIdUsuarioApertura()
            );

            if (caja.getIdUsuarioCierre() != null) {
                ps.setInt(
                        2,
                        caja.getIdUsuarioCierre()
                );
            } else {
                ps.setNull(
                        2,
                        java.sql.Types.INTEGER
                );
            }

            if (caja.getFechaApertura() != null) {

                ps.setTimestamp(
                        3,
                        java.sql.Timestamp.valueOf(
                                caja.getFechaApertura()
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

            if (caja.getFechaCierre() != null) {

                ps.setTimestamp(
                        4,
                        java.sql.Timestamp.valueOf(
                                caja.getFechaCierre()
                        )
                );

            } else {

                ps.setNull(
                        4,
                        java.sql.Types.TIMESTAMP
                );
            }

            ps.setBigDecimal(
                    5,
                    caja.getMontoApertura()
            );

            ps.setBigDecimal(
                    6,
                    caja.getTotalIngresos()
            );

            ps.setBigDecimal(
                    7,
                    caja.getTotalSalidas()
            );

            ps.setBigDecimal(
                    8,
                    caja.getMontoEsperado()
            );

            if (caja.getMontoContado() != null) {

                ps.setBigDecimal(
                        9,
                        caja.getMontoContado()
                );

            } else {

                ps.setNull(
                        9,
                        java.sql.Types.DECIMAL
                );
            }

            if (caja.getDiferencia() != null) {

                ps.setBigDecimal(
                        10,
                        caja.getDiferencia()
                );

            } else {

                ps.setNull(
                        10,
                        java.sql.Types.DECIMAL
                );
            }

            ps.setString(
                    11,
                    caja.getEstado()
            );

            ps.setString(
                    12,
                    caja.getObservacion()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al registrar la Caja.",
                    e
            );
        }
    }

    /*
     * Buscar Caja por ID.
     */
    public Caja buscarPorId(
            int idCaja) {

        String sql = """
            SELECT
                id_caja,
                id_usuario_apertura,
                id_usuario_cierre,
                fecha_apertura,
                fecha_cierre,
                monto_apertura,
                total_ingresos,
                total_salidas,
                monto_esperado,
                monto_contado,
                diferencia,
                estado,
                observacion
            FROM Caja
            WHERE id_caja = ?
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

                if (rs.next()) {

                    return convertirResultado(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    /*
     * Buscar Caja actualmente abierta.
     */
    public Caja buscarCajaAbierta() {

        String sql = """
            SELECT
                id_caja,
                id_usuario_apertura,
                id_usuario_cierre,
                fecha_apertura,
                fecha_cierre,
                monto_apertura,
                total_ingresos,
                total_salidas,
                monto_esperado,
                monto_contado,
                diferencia,
                estado,
                observacion
            FROM Caja
            WHERE estado = 'Abierta'
            ORDER BY id_caja DESC
            LIMIT 1
            """;

        try (Connection con =
                pe.ferreteria.infraestructura.ConexionBD
                        .conectar();
             PreparedStatement ps =
                con.prepareStatement(sql);
             ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {

                return convertirResultado(rs);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }
    /*
 * Buscar la Caja abierta usando una conexión existente.
 */
public Caja buscarCajaAbierta(Connection con) {

    String sql = """
        SELECT
            id_caja,
            id_usuario_apertura,
            id_usuario_cierre,
            fecha_apertura,
            fecha_cierre,
            monto_apertura,
            total_ingresos,
            total_salidas,
            monto_esperado,
            monto_contado,
            diferencia,
            estado,
            observacion
        FROM Caja
        WHERE estado = 'Abierta'
        ORDER BY id_caja DESC
        LIMIT 1
        """;

    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            return convertirResultado(rs);
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar la Caja abierta.",
                e
        );
    }

    return null;
}

    /*
     * Listar todas las Cajas.
     */
    public List<Caja> listar() {

        List<Caja> lista =
                new ArrayList<>();

        String sql = """
            SELECT
                id_caja,
                id_usuario_apertura,
                id_usuario_cierre,
                fecha_apertura,
                fecha_cierre,
                monto_apertura,
                total_ingresos,
                total_salidas,
                monto_esperado,
                monto_contado,
                diferencia,
                estado,
                observacion
            FROM Caja
            ORDER BY id_caja DESC
            """;

        try (Connection con =
                pe.ferreteria.infraestructura.ConexionBD
                        .conectar();
             PreparedStatement ps =
                con.prepareStatement(sql);
             ResultSet rs =
                ps.executeQuery()) {

            while (rs.next()) {

                lista.add(
                        convertirResultado(rs)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return lista;
    }

    /*
     * Convertir resultado SQL a objeto.
     */
    private Caja convertirResultado(
            ResultSet rs) throws SQLException {

        Caja caja =
                new Caja();

        caja.setIdCaja(
                rs.getInt("id_caja")
        );

        caja.setIdUsuarioApertura(
                rs.getInt("id_usuario_apertura")
        );

        int idUsuarioCierre =
                rs.getInt("id_usuario_cierre");

        if (!rs.wasNull()) {

            caja.setIdUsuarioCierre(
                    idUsuarioCierre
            );
        }

        if (rs.getTimestamp(
                "fecha_apertura") != null) {

            caja.setFechaApertura(
                    rs.getTimestamp(
                            "fecha_apertura"
                    ).toLocalDateTime()
            );
        }

        if (rs.getTimestamp(
                "fecha_cierre") != null) {

            caja.setFechaCierre(
                    rs.getTimestamp(
                            "fecha_cierre"
                    ).toLocalDateTime()
            );
        }

        caja.setMontoApertura(
                rs.getBigDecimal(
                        "monto_apertura"
                )
        );

        caja.setTotalIngresos(
                rs.getBigDecimal(
                        "total_ingresos"
                )
        );

        caja.setTotalSalidas(
                rs.getBigDecimal(
                        "total_salidas"
                )
        );

        caja.setMontoEsperado(
                rs.getBigDecimal(
                        "monto_esperado"
                )
        );

        caja.setMontoContado(
                rs.getBigDecimal(
                        "monto_contado"
                )
        );

        caja.setDiferencia(
                rs.getBigDecimal(
                        "diferencia"
                )
        );

        caja.setEstado(
                rs.getString("estado")
        );

        caja.setObservacion(
                rs.getString("observacion")
        );

        return caja;
    }
    /*
 * Actualizar totales de una Caja.
 */
public boolean actualizarTotales(
        Connection con,
        int idCaja,
        BigDecimal totalIngresos,
        BigDecimal totalSalidas,
        BigDecimal montoEsperado) {

    String sql = """
        UPDATE Caja
        SET
            total_ingresos = ?,
            total_salidas = ?,
            monto_esperado = ?
        WHERE id_caja = ?
          AND estado = 'Abierta'
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setBigDecimal(1, totalIngresos);
        ps.setBigDecimal(2, totalSalidas);
        ps.setBigDecimal(3, montoEsperado);
        ps.setInt(4, idCaja);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al actualizar los totales de la Caja.",
                e
        );
    }
}
public boolean cerrarCaja(
        Connection con,
        int idCaja,
        int idUsuarioCierre,
        BigDecimal montoContado,
        BigDecimal diferencia,
        String observacion) {

    String sql = """
        UPDATE Caja
        SET
            id_usuario_cierre = ?,
            fecha_cierre = CURRENT_TIMESTAMP,
            monto_contado = ?,
            diferencia = ?,
            observacion = ?,
            estado = 'Cerrada'
        WHERE id_caja = ?
          AND estado = 'Abierta'
        """;

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idUsuarioCierre);
        ps.setBigDecimal(2, montoContado);
        ps.setBigDecimal(3, diferencia);
        ps.setString(4, observacion);
        ps.setInt(5, idCaja);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException(
                "Error al cerrar la Caja.", e);
    }
}
/*
 * Buscar la Caja abierta de un usuario.
 */
public Caja buscarCajaAbiertaPorUsuario(
        int idUsuario) {

    String sql = """
        SELECT
            id_caja,
            id_usuario_apertura,
            id_usuario_cierre,
            fecha_apertura,
            fecha_cierre,
            monto_apertura,
            total_ingresos,
            total_salidas,
            monto_esperado,
            monto_contado,
            diferencia,
            estado,
            observacion
        FROM Caja
        WHERE estado = 'Abierta'
          AND id_usuario_apertura = ?
        ORDER BY id_caja DESC
        LIMIT 1
        """;

    try (Connection con =
            pe.ferreteria.infraestructura.ConexionBD
                    .conectar();
         PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(1, idUsuario);

        try (ResultSet rs =
                ps.executeQuery()) {

            if (rs.next()) {
                return convertirResultado(rs);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar la Caja abierta del usuario.",
                e
        );
    }

    return null;
}
}