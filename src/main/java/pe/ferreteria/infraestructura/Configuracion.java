
package pe.ferreteria.infraestructura;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lee la configuración del sistema sin dejar credenciales en el código.
 *
 * Orden de búsqueda:
 * 1. Propiedad de la JVM.
 * 2. Variable de entorno.
 * 3. Archivo ferreteria.properties.
 * 4. Valor por defecto.
 */
public final class Configuracion {

    private static final String[] ARCHIVOS = {
        "ferreteria.properties",
        "config/ferreteria.properties"
    };

    private static final Properties ARCHIVO = cargarArchivo();

    private Configuracion() {
    }

    public static String obtener(
            String clave, String variableEntorno, String porDefecto) {

        String valor = limpiar(System.getProperty(clave));

        if (valor == null) {
            valor = limpiar(System.getenv(variableEntorno));
        }

        if (valor == null) {
            valor = limpiar(ARCHIVO.getProperty(clave));
        }

        return valor != null ? valor : porDefecto;
    }

    private static String limpiar(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private static Properties cargarArchivo() {

        Properties propiedades = new Properties();

        for (String nombre : ARCHIVOS) {

            Path ruta = Path.of(nombre);

            if (!Files.isRegularFile(ruta)) {
                continue;
            }

            try (InputStream in = Files.newInputStream(ruta)) {
                propiedades.load(in);
                break;

            } catch (IOException e) {
                System.err.println(
                        "No se pudo leer " + nombre + ": " + e.getMessage());
            }
        }

        return propiedades;
    }
}
