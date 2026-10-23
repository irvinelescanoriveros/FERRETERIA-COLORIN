package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean insertar(Cliente cliente) {

        String sql = """
                INSERT INTO Cliente
                (tipo_cliente, dni, ruc, nombre, apellidos,
                 razon_social, contacto, telefono, direccion, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getTipoCliente());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getRuc());
            ps.setString(4, cliente.getNombre());
            ps.setString(5, cliente.getApellidos());
            ps.setString(6, cliente.getRazonSocial());
            ps.setString(7, cliente.getContacto());
            ps.setString(8, cliente.getTelefono());
            ps.setString(9, cliente.getDireccion());
            ps.setBoolean(10, cliente.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> listar() {

        List<Cliente> lista = new ArrayList<>();

        String sql = """
                SELECT * FROM Cliente
                WHERE activo = true
                ORDER BY id_cliente
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente();

                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setTipoCliente(rs.getString("tipo_cliente"));
                cliente.setDni(rs.getString("dni"));
                cliente.setRuc(rs.getString("ruc"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellidos(rs.getString("apellidos"));
                cliente.setRazonSocial(rs.getString("razon_social"));
                cliente.setContacto(rs.getString("contacto"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));

                lista.add(cliente);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Cliente cliente) {

        String sql = """
                UPDATE Cliente
                SET tipo_cliente = ?, dni = ?, ruc = ?,
                    nombre = ?, apellidos = ?, razon_social = ?,
                    contacto = ?, telefono = ?, direccion = ?
                WHERE id_cliente = ?
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getTipoCliente());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getRuc());
            ps.setString(4, cliente.getNombre());
            ps.setString(5, cliente.getApellidos());
            ps.setString(6, cliente.getRazonSocial());
            ps.setString(7, cliente.getContacto());
            ps.setString(8, cliente.getTelefono());
            ps.setString(9, cliente.getDireccion());
            ps.setInt(10, cliente.getIdCliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idCliente) {

        String sql = """
                UPDATE Cliente
                SET activo = false
                WHERE id_cliente = ?
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean existeDni(String dni, int idCliente) {

        String sql = """
                SELECT COUNT(*) FROM Cliente
                WHERE dni = ?
                AND id_cliente <> ?
                AND activo = true
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setInt(2, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar DNI: " + e.getMessage());
        }

        return false;
    }

    public boolean existeRuc(String ruc, int idCliente) {

        String sql = """
                SELECT COUNT(*) FROM Cliente
                WHERE ruc = ?
                AND id_cliente <> ?
                AND activo = true
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, ruc);
            ps.setInt(2, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar RUC: " + e.getMessage());
        }

        return false;
    }
    public Cliente buscarPorId(int idCliente) {

    String sql = """
            SELECT * FROM Cliente
            WHERE id_cliente = ?
            """;

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Cliente cliente = new Cliente();

                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setTipoCliente(rs.getString("tipo_cliente"));
                cliente.setDni(rs.getString("dni"));
                cliente.setRuc(rs.getString("ruc"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellidos(rs.getString("apellidos"));
                cliente.setRazonSocial(rs.getString("razon_social"));
                cliente.setContacto(rs.getString("contacto"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));

                return cliente;
            }
        }

    } catch (SQLException e) {
        System.out.println(
                "Error al buscar cliente por ID: "
                + e.getMessage()
        );
    }

    return null;
}
}