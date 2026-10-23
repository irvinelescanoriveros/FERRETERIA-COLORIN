package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    public boolean insertar(Marca marca) {

        String sql = """
            INSERT INTO Marca
            (nombre, descripcion, activo)
            VALUES (?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca.getNombre());
            ps.setString(2, marca.getDescripcion());
            ps.setBoolean(3, marca.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Marca> listar() {

        List<Marca> lista = new ArrayList<>();

        String sql = """
            SELECT id_marca, nombre, descripcion, activo
            FROM Marca
            WHERE activo = true
            ORDER BY nombre
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Marca marca = new Marca();

                marca.setIdMarca(rs.getInt("id_marca"));
                marca.setNombre(rs.getString("nombre"));
                marca.setDescripcion(rs.getString("descripcion"));
                marca.setActivo(rs.getBoolean("activo"));

                lista.add(marca);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean eliminar(int idMarca) {

        String sql = """
            UPDATE Marca
            SET activo = false
            WHERE id_marca = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMarca);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existeNombre(String nombre, int idMarca) {

        String sql = """
            SELECT COUNT(*)
            FROM Marca
            WHERE nombre = ?
              AND activo = true
              AND id_marca <> ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idMarca);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            return false;
        }

        return false;
    }
}