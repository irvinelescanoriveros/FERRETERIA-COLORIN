
package pe.ferreteria.infraestructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a MySQL. Las credenciales se leen desde Configuracion.
 */
public class ConexionBD {

    private static final java.util.logging.Logger LOG =
            Registro.de(ConexionBD.class);

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/bd_colorin";

    public static Connection conectar() throws SQLException {

        String url = Configuracion.obtener(
                "db.url", "DB_URL", URL_POR_DEFECTO);

        String usuario = Configuracion.obtener(
                "db.username", "DB_USERNAME", "root");

        String password = Configuracion.obtener(
                "db.password", "DB_PASSWORD", null);

        if (password == null) {
            LOG.severe("Falta la contrasena de la base de datos");
            throw new SQLException(
                    "No se configuró la contraseña de la base de datos. "
                    + "Defina la variable de entorno DB_PASSWORD o la clave "
                    + "db.password en ferreteria.properties.");
        }

        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            LOG.severe("No se pudo conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }
}
