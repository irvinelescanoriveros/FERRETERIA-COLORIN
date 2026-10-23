package pe.ferreteria.dao;

import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto producto) {

        String sql = """
            INSERT INTO Producto
            (codigo, nombre, id_categoria, id_marca, unidad,
             estante, nivel, descripcion, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getIdCategoria());
            ps.setInt(4, producto.getIdMarca());
            ps.setString(5, producto.getUnidad());
            ps.setString(6, producto.getEstante());
            ps.setString(7, producto.getNivel());
            ps.setString(8, producto.getDescripcion());
            ps.setBoolean(9, producto.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Producto> listar() {

        List<Producto> lista = new ArrayList<>();

        String sql = """
            SELECT id_producto, codigo, nombre,
                   id_categoria, id_marca, unidad,
                   estante, nivel, descripcion, activo
            FROM Producto
            WHERE activo = true
            ORDER BY id_producto
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(rs.getInt("id_producto"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setIdCategoria(rs.getInt("id_categoria"));
                producto.setIdMarca(rs.getInt("id_marca"));
                producto.setUnidad(rs.getString("unidad"));
                producto.setEstante(rs.getString("estante"));
                producto.setNivel(rs.getString("nivel"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setActivo(rs.getBoolean("activo"));

                lista.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean actualizar(Producto producto) {

        String sql = """
            UPDATE Producto
            SET codigo = ?,
                nombre = ?,
                id_categoria = ?,
                id_marca = ?,
                unidad = ?,
                estante = ?,
                nivel = ?,
                descripcion = ?
            WHERE id_producto = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getIdCategoria());
            ps.setInt(4, producto.getIdMarca());
            ps.setString(5, producto.getUnidad());
            ps.setString(6, producto.getEstante());
            ps.setString(7, producto.getNivel());
            ps.setString(8, producto.getDescripcion());
            ps.setInt(9, producto.getIdProducto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminar(int idProducto) {

        String sql = """
            UPDATE Producto
            SET activo = false
            WHERE id_producto = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existeCodigo(String codigo, int idProducto) {

        String sql = """
            SELECT COUNT(*)
            FROM Producto
            WHERE codigo = ?
              AND activo = true
              AND id_producto <> ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setInt(2, idProducto);

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
    public Producto buscarParaDashboard(int idProducto) {

    String sql = """
        SELECT
            id_producto,
            nombre,
            unidad
        FROM Producto
        WHERE id_producto = ?
          AND activo = true
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps =
                 con.prepareStatement(sql)) {

        ps.setInt(1, idProducto);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(
                        rs.getInt("id_producto")
                );

                producto.setNombre(
                        rs.getString("nombre")
                );

                producto.setUnidad(
                        rs.getString("unidad")
                );

                return producto;
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar producto para Dashboard.",
                e
        );
    }

    return null;
}
}