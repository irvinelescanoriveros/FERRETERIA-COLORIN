package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean insertar(Usuario usuario) {

        String sql = """
                INSERT INTO Usuario
                (username, nombre, apellidos, correo, password, rol, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getCorreo());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getRol());
            ps.setBoolean(7, usuario.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {

    List<Usuario> lista = new ArrayList<>();

    String sql = "SELECT * FROM Usuario WHERE activo = true AND rol <> 'WEB' ORDER BY id_usuario";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Usuario usuario = new Usuario();

            usuario.setIdUsuario(rs.getInt("id_usuario"));
            usuario.setUsername(rs.getString("username"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setApellidos(rs.getString("apellidos"));
            usuario.setCorreo(rs.getString("correo"));
            usuario.setPassword(rs.getString("password"));
            usuario.setRol(rs.getString("rol"));
            usuario.setActivo(rs.getBoolean("activo"));

            lista.add(usuario);
        }

    } catch (SQLException e) {
        System.out.println("Error al listar usuarios: " + e.getMessage());
    }

    return lista;
}

    public List<Usuario> listarKardex() {

    List<Usuario> lista = new ArrayList<>();

    String sql = "SELECT * FROM Usuario WHERE activo = true ORDER BY id_usuario";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Usuario usuario = new Usuario();

            usuario.setIdUsuario(rs.getInt("id_usuario"));
            usuario.setUsername(rs.getString("username"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setApellidos(rs.getString("apellidos"));
            usuario.setCorreo(rs.getString("correo"));
            usuario.setPassword(rs.getString("password"));
            usuario.setRol(rs.getString("rol"));
            usuario.setActivo(rs.getBoolean("activo"));

            lista.add(usuario);
        }

    } catch (SQLException e) {
        System.out.println("Error al listar usuarios para Kardex: " + e.getMessage());
    }

    return lista;
}
    public boolean actualizar(Usuario usuario) {

        String sql = """
                UPDATE Usuario
                SET username = ?, nombre = ?, apellidos = ?,
                    correo = ?, rol = ?, activo = ?
                WHERE id_usuario = ?
                """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getCorreo());
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());
            ps.setInt(7, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idUsuario) {

        String sql = "UPDATE Usuario SET activo = false WHERE id_usuario = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    public boolean existeUsername(String username) {

    String sql = "SELECT COUNT(*) FROM Usuario WHERE username = ?";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, username);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al verificar usuario: " + e.getMessage());
    }

    return false;
}
    public boolean cambiarPassword(int idUsuario, String nuevaPassword) {

    String sql = "UPDATE Usuario SET password = ? WHERE id_usuario = ?";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, nuevaPassword);
        ps.setInt(2, idUsuario);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("Error al cambiar contraseña: " + e.getMessage());
        return false;
    }
}
    public Usuario autenticar(String username, String password) {

    String sql = """
            SELECT *
            FROM Usuario
            WHERE username = ?
              AND password = ?
              AND activo = true
            """;

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, password);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Usuario usuario = new Usuario();

                usuario.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                usuario.setUsername(
                        rs.getString("username")
                );

                usuario.setNombre(
                        rs.getString("nombre")
                );

                usuario.setApellidos(
                        rs.getString("apellidos")
                );

                usuario.setCorreo(
                        rs.getString("correo")
                );

                usuario.setPassword(
                        rs.getString("password")
                );

                usuario.setRol(
                        rs.getString("rol")
                );

                usuario.setActivo(
                        rs.getBoolean("activo")
                );

                return usuario;
            }
        }

    } catch (SQLException e) {

        System.out.println(
                "Error al autenticar usuario: "
                + e.getMessage()
        );
    }

    return null;
}
    public Usuario buscarPorId(int idUsuario) {

    String sql = """
            SELECT *
            FROM Usuario
            WHERE id_usuario = ?
            """;

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idUsuario);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Usuario usuario = new Usuario();

                usuario.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                usuario.setUsername(
                        rs.getString("username")
                );

                usuario.setNombre(
                        rs.getString("nombre")
                );

                usuario.setApellidos(
                        rs.getString("apellidos")
                );

                usuario.setCorreo(
                        rs.getString("correo")
                );

                usuario.setRol(
                        rs.getString("rol")
                );

                usuario.setActivo(
                        rs.getBoolean("activo")
                );

                return usuario;
            }
        }

    } catch (SQLException e) {

        System.out.println(
                "Error al buscar usuario por ID: "
                + e.getMessage()
        );
    }

    return null;
}
}