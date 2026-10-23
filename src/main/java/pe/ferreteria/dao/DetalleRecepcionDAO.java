package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.DetalleRecepcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecepcionDAO {

    public List<DetalleRecepcion> listarPorRecepcion(int idRecepcion) {

        List<DetalleRecepcion> lista = new ArrayList<>();

        String sql = """
            SELECT
                id_detalle_recepcion,
                id_recepcion,
                id_detalle_compra,
                estado,
                fecha_confirmacion,
                cantidad_recibida,
                observacion
            FROM Detalle_Recepcion
            WHERE id_recepcion = ?
            ORDER BY id_detalle_recepcion
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRecepcion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    DetalleRecepcion detalle =
                            new DetalleRecepcion();

                    detalle.setIdDetalleRecepcion(
                            rs.getInt("id_detalle_recepcion")
                    );

                    detalle.setIdRecepcion(
                            rs.getInt("id_recepcion")
                    );

                    detalle.setIdDetalleCompra(
                            rs.getInt("id_detalle_compra")
                    );

                    detalle.setEstado(
                            rs.getString("estado")
                    );

                    if (rs.getTimestamp("fecha_confirmacion") != null) {
                        detalle.setFechaConfirmacion(
                                rs.getTimestamp("fecha_confirmacion")
                                        .toLocalDateTime()
                        );
                    }

                    int cantidad = rs.getInt("cantidad_recibida");

                    if (rs.wasNull()) {
                        detalle.setCantidadRecibida(null);
                    } else {
                        detalle.setCantidadRecibida(cantidad);
                    }

                    detalle.setObservacion(
                            rs.getString("observacion")
                    );

                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    public boolean insertar(DetalleRecepcion detalle) {

    String sql = """
        INSERT INTO Detalle_Recepcion
        (id_recepcion, id_detalle_compra, estado,
         fecha_confirmacion, cantidad_recibida, observacion)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, detalle.getIdRecepcion());
        ps.setInt(2, detalle.getIdDetalleCompra());
        ps.setString(3, detalle.getEstado());

        if (detalle.getFechaConfirmacion() != null) {
            ps.setTimestamp(
                    4,
                    java.sql.Timestamp.valueOf(
                            detalle.getFechaConfirmacion()
                    )
            );
        } else {
            ps.setNull(4, java.sql.Types.TIMESTAMP);
        }

        if (detalle.getCantidadRecibida() != null) {
            ps.setInt(5, detalle.getCantidadRecibida());
        } else {
            ps.setNull(5, java.sql.Types.INTEGER);
        }

        ps.setString(6, detalle.getObservacion());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean insertar(
        Connection con,
        DetalleRecepcion detalle) {

    String sql = """
        INSERT INTO Detalle_Recepcion
        (id_recepcion, id_detalle_compra, estado,
         fecha_confirmacion, cantidad_recibida, observacion)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setInt(
                1,
                detalle.getIdRecepcion()
        );

        ps.setInt(
                2,
                detalle.getIdDetalleCompra()
        );

        ps.setString(
                3,
                detalle.getEstado()
        );

        if (detalle.getFechaConfirmacion() != null) {

            ps.setTimestamp(
                    4,
                    java.sql.Timestamp.valueOf(
                            detalle.getFechaConfirmacion()
                    )
            );

        } else {

            ps.setNull(
                    4,
                    java.sql.Types.TIMESTAMP
            );
        }

        if (detalle.getCantidadRecibida() != null) {

            ps.setInt(
                    5,
                    detalle.getCantidadRecibida()
            );

        } else {

            ps.setNull(
                    5,
                    java.sql.Types.INTEGER
            );
        }

        ps.setString(
                6,
                detalle.getObservacion()
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al registrar detalle de recepción.",
                e
        );
    }
}
    
    public DetalleRecepcion buscarPorRecepcionYDetalleCompra(
        int idRecepcion,
        int idDetalleCompra) {

    String sql = """
        SELECT
            id_detalle_recepcion,
            id_recepcion,
            id_detalle_compra,
            estado,
            fecha_confirmacion,
            cantidad_recibida,
            observacion
        FROM Detalle_Recepcion
        WHERE id_recepcion = ?
          AND id_detalle_compra = ?
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idRecepcion);
        ps.setInt(2, idDetalleCompra);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                DetalleRecepcion detalle =
                        new DetalleRecepcion();

                detalle.setIdDetalleRecepcion(
                        rs.getInt("id_detalle_recepcion")
                );

                detalle.setIdRecepcion(
                        rs.getInt("id_recepcion")
                );

                detalle.setIdDetalleCompra(
                        rs.getInt("id_detalle_compra")
                );

                detalle.setEstado(
                        rs.getString("estado")
                );

                if (rs.getTimestamp("fecha_confirmacion") != null) {
                    detalle.setFechaConfirmacion(
                            rs.getTimestamp("fecha_confirmacion")
                                    .toLocalDateTime()
                    );
                } else {
                    detalle.setFechaConfirmacion(null);
                }

                int cantidad =
                        rs.getInt("cantidad_recibida");

                if (rs.wasNull()) {
                    detalle.setCantidadRecibida(null);
                } else {
                    detalle.setCantidadRecibida(cantidad);
                }

                detalle.setObservacion(
                        rs.getString("observacion")
                );

                return detalle;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
    public boolean actualizar(DetalleRecepcion detalle) {

    String sql = """
        UPDATE Detalle_Recepcion
        SET estado = ?,
            fecha_confirmacion = ?,
            cantidad_recibida = ?,
            observacion = ?
        WHERE id_detalle_recepcion = ?
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, detalle.getEstado());

        if (detalle.getFechaConfirmacion() != null) {
            ps.setTimestamp(
                    2,
                    java.sql.Timestamp.valueOf(
                            detalle.getFechaConfirmacion()
                    )
            );
        } else {
            ps.setNull(2, java.sql.Types.TIMESTAMP);
        }

        if (detalle.getCantidadRecibida() != null) {
            ps.setInt(3, detalle.getCantidadRecibida());
        } else {
            ps.setNull(3, java.sql.Types.INTEGER);
        }

        ps.setString(4, detalle.getObservacion());
        ps.setInt(5, detalle.getIdDetalleRecepcion());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean actualizar(
        Connection con,
        DetalleRecepcion detalle) {

    String sql = """
        UPDATE Detalle_Recepcion
        SET estado = ?,
            fecha_confirmacion = ?,
            cantidad_recibida = ?,
            observacion = ?
        WHERE id_detalle_recepcion = ?
        """;

    try (PreparedStatement ps =
            con.prepareStatement(sql)) {

        ps.setString(
                1,
                detalle.getEstado()
        );

        if (detalle.getFechaConfirmacion() != null) {

            ps.setTimestamp(
                    2,
                    java.sql.Timestamp.valueOf(
                            detalle.getFechaConfirmacion()
                    )
            );

        } else {

            ps.setNull(
                    2,
                    java.sql.Types.TIMESTAMP
            );
        }

        if (detalle.getCantidadRecibida() != null) {

            ps.setInt(
                    3,
                    detalle.getCantidadRecibida()
            );

        } else {

            ps.setNull(
                    3,
                    java.sql.Types.INTEGER
            );
        }

        ps.setString(
                4,
                detalle.getObservacion()
        );

        ps.setInt(
                5,
                detalle.getIdDetalleRecepcion()
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al actualizar detalle de recepción.",
                e
        );
    }
}
}


