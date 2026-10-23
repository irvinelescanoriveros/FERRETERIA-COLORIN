package pe.ferreteria.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GeneradorLote {

    public static String generarNuevoLote(
            Connection conexion,
            int idProducto,
            LocalDate fecha) throws SQLException {

        String fechaLote =
                fecha.format(
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                );

        String sql = """
            SELECT lote
            FROM Detalle_Compra
            WHERE lote LIKE ?
            ORDER BY id_detalle DESC
            LIMIT 1
            """;

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "P%-" + fechaLote + "-%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                int correlativo = 1;

                if (rs.next()) {

                    String ultimoLote =
                            rs.getString("lote");

                    int ultimaPosicion =
                            ultimoLote.lastIndexOf("-");

                    if (ultimaPosicion != -1) {

                        String numero =
                                ultimoLote.substring(
                                        ultimaPosicion + 1
                                );

                        correlativo =
                                Integer.parseInt(numero) + 1;
                    }
                }

                return String.format(
                        "P%d-%s-%03d",
                        idProducto,
                        fechaLote,
                        correlativo
                );
            }
        }
    }
}