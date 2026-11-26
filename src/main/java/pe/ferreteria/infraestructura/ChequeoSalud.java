package pe.ferreteria.infraestructura;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Chequeo de salud para monitoreo (cron, Nagios, Zabbix, etc.):
 *   java -cp ferreteria-colorin.jar pe.ferreteria.infraestructura.ChequeoSalud
 * Imprime una línea y termina con código 0 (OK) o 1 (falla).
 * Verifica la conexión a MySQL y el tiempo de respuesta.
 */
public final class ChequeoSalud {

    private ChequeoSalud() {
    }

    /** Resultado del chequeo: milisegundos de respuesta o -1 si falló. */
    public static long medirBaseDeDatos() {

        long inicio = System.nanoTime();

        try (Connection cn = ConexionBD.conectar();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {

            return rs.next()
                    ? (System.nanoTime() - inicio) / 1_000_000
                    : -1;

        } catch (Exception e) {
            return -1;
        }
    }

    public static void main(String[] args) {

        long ms = medirBaseDeDatos();

        if (ms < 0) {
            System.out.println("ESTADO=FALLA base_de_datos=inaccesible");
            System.exit(1);
        }

        String estado = ms > 1000 ? "LENTO" : "OK";
        System.out.println("ESTADO=" + estado + " base_de_datos_ms=" + ms);
        System.exit(ms > 1000 ? 2 : 0);
    }
}
