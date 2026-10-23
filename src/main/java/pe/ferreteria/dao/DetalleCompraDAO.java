package pe.ferreteria.dao;

import pe.ferreteria.modelo.DetalleCompra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetalleCompraDAO {

    public void insertar(
        DetalleCompra detalle,
        Connection conexion,
        java.time.LocalDate fechaCompra) throws SQLException {

    if (detalle.getLote() == null
            || detalle.getLote().trim().isEmpty()) {

        String lote = pe.ferreteria.util.GeneradorLote.generarNuevoLote(
                conexion,
                detalle.getIdProducto(),
                fechaCompra
        );

        detalle.setLote(lote);
    }

    String sql = """
        INSERT INTO Detalle_Compra
        (id_compra, id_producto, lote, cantidad,
         precio_compra, fecha_vencimiento)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement ps = conexion.prepareStatement(sql)) {

        ps.setInt(1, detalle.getIdCompra());
        ps.setInt(2, detalle.getIdProducto());
        ps.setString(3, detalle.getLote());
        ps.setInt(4, detalle.getCantidad());
        ps.setBigDecimal(5, detalle.getPrecioCompra());

        if (detalle.getFechaVencimiento() != null) {
            ps.setDate(
                    6,
                    java.sql.Date.valueOf(
                            detalle.getFechaVencimiento()
                    )
            );
        } else {
            ps.setNull(6, java.sql.Types.DATE);
        }

        ps.executeUpdate();
    }
}
    public java.util.List<DetalleCompra> listarPorCompra(int idCompra) {

    java.util.List<DetalleCompra> lista =
            new java.util.ArrayList<>();

    String sql = """
        SELECT
            id_detalle,
            id_compra,
            id_producto,
            lote,
            cantidad,
            precio_compra,
            fecha_vencimiento
        FROM Detalle_Compra
        WHERE id_compra = ?
        ORDER BY id_detalle
        """;

    try (Connection con = pe.ferreteria.infraestructura.ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                DetalleCompra detalle = new DetalleCompra();

                detalle.setIdDetalle(
                        rs.getInt("id_detalle")
                );

                detalle.setIdCompra(
                        rs.getInt("id_compra")
                );

                detalle.setIdProducto(
                        rs.getInt("id_producto")
                );

                detalle.setLote(
                        rs.getString("lote")
                );

                detalle.setCantidad(
                        rs.getInt("cantidad")
                );

                detalle.setPrecioCompra(
                        rs.getBigDecimal("precio_compra")
                );

                if (rs.getDate("fecha_vencimiento") != null) {
                    detalle.setFechaVencimiento(
                            rs.getDate("fecha_vencimiento")
                                    .toLocalDate()
                    );
                } else {
                    detalle.setFechaVencimiento(null);
                }

                lista.add(detalle);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}
}