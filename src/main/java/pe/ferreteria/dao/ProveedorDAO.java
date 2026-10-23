package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public boolean insertar(Proveedor proveedor) {

        String sql = """
            INSERT INTO Proveedor
            (empresa, ruc, contacto, telefono, correo, direccion, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proveedor.getEmpresa());
            ps.setString(2, proveedor.getRuc());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getTelefono());
            ps.setString(5, proveedor.getCorreo());
            ps.setString(6, proveedor.getDireccion());
            ps.setBoolean(7, proveedor.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Proveedor> listar() {

        List<Proveedor> lista = new ArrayList<>();

        String sql = """
            SELECT id_proveedor, empresa, ruc, contacto,
                   telefono, correo, direccion, activo
            FROM Proveedor
            WHERE activo = true
            ORDER BY id_proveedor
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Proveedor proveedor = new Proveedor();

                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setEmpresa(rs.getString("empresa"));
                proveedor.setRuc(rs.getString("ruc"));
                proveedor.setContacto(rs.getString("contacto"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setCorreo(rs.getString("correo"));
                proveedor.setDireccion(rs.getString("direccion"));
                proveedor.setActivo(rs.getBoolean("activo"));

                lista.add(proveedor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean actualizar(Proveedor proveedor) {

        String sql = """
            UPDATE Proveedor
            SET empresa = ?,
                ruc = ?,
                contacto = ?,
                telefono = ?,
                correo = ?,
                direccion = ?
            WHERE id_proveedor = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proveedor.getEmpresa());
            ps.setString(2, proveedor.getRuc());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getTelefono());
            ps.setString(5, proveedor.getCorreo());
            ps.setString(6, proveedor.getDireccion());
            ps.setInt(7, proveedor.getIdProveedor());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminar(int idProveedor) {

        String sql = """
            UPDATE Proveedor
            SET activo = false
            WHERE id_proveedor = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProveedor);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existeRuc(String ruc, int idProveedor) {

        String sql = """
            SELECT COUNT(*)
            FROM Proveedor
            WHERE ruc = ?
              AND activo = true
              AND id_proveedor <> ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ruc);
            ps.setInt(2, idProveedor);

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