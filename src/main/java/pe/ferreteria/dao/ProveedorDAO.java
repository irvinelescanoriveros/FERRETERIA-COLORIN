package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de Proveedor: concentra las consultas JDBC (con PreparedStatement)
 * sobre las tablas relacionadas. No contiene reglas de negocio.
 */
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

    public List<Proveedor> listarTodos() {

        List<Proveedor> lista = new ArrayList<>();

        String sql = """
            SELECT id_proveedor, empresa, ruc, contacto,
                   telefono, correo, direccion, activo
            FROM Proveedor
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

    public boolean cambiarEstado(int idProveedor, boolean activo) {

        String sql = """
            UPDATE Proveedor
            SET activo = ?
            WHERE id_proveedor = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idProveedor);

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
        System.out.println("Error al verificar duplicado (" + e.getMessage() + "); se asume que existe.");
        return true; // falla segura: evita registrar sin poder verificar
    }

    return false;
}
}
