
package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Compra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CompraDAO {

    public int insertar(Compra compra, Connection conexion) throws SQLException {

        String sql = """
            INSERT INTO Compra
            (id_proveedor, id_usuario, fecha_compra, nro_comprobante, total, activo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, compra.getIdProveedor());
            ps.setInt(2, compra.getIdUsuario());
            ps.setTimestamp(3,
                    java.sql.Timestamp.valueOf(compra.getFechaCompra()));
            ps.setString(4, compra.getNroComprobante());
            ps.setBigDecimal(5, compra.getTotal());
            ps.setBoolean(6, compra.isActivo());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new SQLException(
                        "No se pudo registrar la compra."
                );
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new SQLException(
                        "No se pudo obtener el ID de la compra."
                );
            }
        }
    }
    public boolean existeNroComprobante(String nroComprobante) {

    String sql = """
        SELECT COUNT(*)
        FROM Compra
        WHERE nro_comprobante = ?
          AND activo = true
        """;

    try (java.sql.Connection conexion =
                 pe.ferreteria.infraestructura.ConexionBD.conectar();
         java.sql.PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

        ps.setString(1, nroComprobante);

        try (java.sql.ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

    } catch (java.sql.SQLException e) {

        System.out.println(
                "Error al verificar comprobante: "
                + e.getMessage()
        );
    }

    return false;
}
}