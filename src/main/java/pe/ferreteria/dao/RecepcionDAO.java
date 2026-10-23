package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Compra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.modelo.Recepcion;

public class RecepcionDAO {

    public List<Compra> listarComprasPendientesRecepcion() {

        List<Compra> lista = new ArrayList<>();

        String sql = """
            SELECT
                c.id_compra,
                c.id_proveedor,
                c.id_usuario,
                c.fecha_compra,
                c.nro_comprobante,
                c.total,
                c.activo
            FROM Compra c
            WHERE c.activo = true

              AND EXISTS (
                    SELECT 1
                    FROM Detalle_Compra dc
                    WHERE dc.id_compra = c.id_compra
              )

              AND EXISTS (
                    SELECT 1
                    FROM Detalle_Compra dc
                    LEFT JOIN Detalle_Recepcion dr
                        ON dr.id_detalle_compra = dc.id_detalle
                    LEFT JOIN Recepcion r
                        ON r.id_recepcion = dr.id_recepcion
                        AND r.id_compra = c.id_compra
                    WHERE dc.id_compra = c.id_compra
                      AND (
                            dr.id_detalle_recepcion IS NULL
                            OR dr.estado <> 'Completada'
                          )
              )

            ORDER BY c.fecha_compra DESC, c.id_compra DESC
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Compra compra = new Compra();

                compra.setIdCompra(
                        rs.getInt("id_compra")
                );

                compra.setIdProveedor(
                        rs.getInt("id_proveedor")
                );

                compra.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                compra.setFechaCompra(
                        rs.getTimestamp("fecha_compra")
                                .toLocalDateTime()
                );

                compra.setNroComprobante(
                        rs.getString("nro_comprobante")
                );

                compra.setTotal(
                        rs.getBigDecimal("total")
                );

                compra.setActivo(
                        rs.getBoolean("activo")
                );

                lista.add(compra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    public int insertar(Recepcion recepcion) {

    String sql = """
        INSERT INTO Recepcion
        (id_compra, id_usuario, fecha_recepcion, estado, observacion)
        VALUES (?, ?, ?, ?, ?)
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(
                 sql,
                 java.sql.Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, recepcion.getIdCompra());
        ps.setInt(2, recepcion.getIdUsuario());
        ps.setTimestamp(
                3,
                java.sql.Timestamp.valueOf(
                        recepcion.getFechaRecepcion()
                )
        );
        ps.setString(4, recepcion.getEstado());
        ps.setString(5, recepcion.getObservacion());

        int filas = ps.executeUpdate();

        if (filas == 0) {
            return 0;
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0;
}
    public Recepcion buscarUltimaPorCompra(int idCompra) {

    String sql = """
        SELECT
            id_recepcion,
            id_compra,
            id_usuario,
            fecha_recepcion,
            estado,
            observacion
        FROM Recepcion
        WHERE id_compra = ?
        ORDER BY id_recepcion DESC
        LIMIT 1
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Recepcion recepcion = new Recepcion();

                recepcion.setIdRecepcion(
                        rs.getInt("id_recepcion")
                );

                recepcion.setIdCompra(
                        rs.getInt("id_compra")
                );

                recepcion.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                recepcion.setFechaRecepcion(
                        rs.getTimestamp("fecha_recepcion")
                                .toLocalDateTime()
                );

                recepcion.setEstado(
                        rs.getString("estado")
                );

                recepcion.setObservacion(
                        rs.getString("observacion")
                );

                return recepcion;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
    public boolean actualizarEstadoSegunDetalles(int idRecepcion) {

    String sql = """
        UPDATE Recepcion
        SET estado = CASE

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
                  AND estado = 'Observada'
            )
            THEN 'Observada'

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
                  AND estado <> 'Completada'
            )
            THEN 'Pendiente'

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
            )
            THEN 'Completada'

            ELSE 'Pendiente'

        END
        WHERE id_recepcion = ?
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idRecepcion);
        ps.setInt(2, idRecepcion);
        ps.setInt(3, idRecepcion);
        ps.setInt(4, idRecepcion);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean actualizarEstadoSegunDetalles(
        Connection con,
        int idRecepcion) {

    String sql = """
        UPDATE Recepcion
        SET estado = CASE

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
                  AND estado = 'Observada'
            )
            THEN 'Observada'

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
                  AND estado <> 'Completada'
            )
            THEN 'Pendiente'

            WHEN EXISTS (
                SELECT 1
                FROM Detalle_Recepcion
                WHERE id_recepcion = ?
            )
            THEN 'Completada'

            ELSE 'Pendiente'

        END
        WHERE id_recepcion = ?
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(1, idRecepcion);
        ps.setInt(2, idRecepcion);
        ps.setInt(3, idRecepcion);
        ps.setInt(4, idRecepcion);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al actualizar estado de recepción.",
                e
        );
    }
}
}