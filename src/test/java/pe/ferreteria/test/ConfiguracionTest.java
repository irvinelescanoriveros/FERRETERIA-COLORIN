package pe.ferreteria.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import java.nio.file.Files;
import java.nio.file.Path;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.infraestructura.Configuracion;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la configuración externa: las credenciales de la base de
 * datos ya no están escritas en el código (OWASP A05).
 */
class ConfiguracionTest {

    private static final String CLAVE = "prueba.configuracion";
    private static final String VARIABLE_INEXISTENTE = "VARIABLE_QUE_NO_EXISTE_X";

    @AfterEach
    void limpiar() {
        System.clearProperty(CLAVE);
        System.clearProperty("db.password");
    }

    @Test
    @DisplayName("Configuración: se lee una propiedad del sistema")
    void leePropiedadDelSistema() {
        System.setProperty(CLAVE, "valor");

        assertEquals("valor",
                Configuracion.obtener(CLAVE, VARIABLE_INEXISTENTE, null));
    }

    @Test
    @DisplayName("Configuración: se eliminan los espacios sobrantes")
    void recortaEspacios() {
        System.setProperty(CLAVE, "  valor  ");

        assertEquals("valor",
                Configuracion.obtener(CLAVE, VARIABLE_INEXISTENTE, null));
    }

    @Test
    @DisplayName("Configuración: un valor en blanco se considera ausente")
    void valorEnBlancoSeConsideraAusente() {
        System.setProperty(CLAVE, "   ");

        assertEquals("porDefecto",
                Configuracion.obtener(CLAVE, VARIABLE_INEXISTENTE, "porDefecto"));
    }

    @Test
    @DisplayName("Configuración: sin valor devuelve el valor por defecto o null")
    void sinValorDevuelveElPorDefecto() {
        assertEquals("porDefecto",
                Configuracion.obtener(CLAVE, VARIABLE_INEXISTENTE, "porDefecto"));
        assertNull(
                Configuracion.obtener(CLAVE, VARIABLE_INEXISTENTE, null));
    }

    @Test
@DisplayName("Conexión: sin contraseña configurada falla con un mensaje claro")
void conectarSinContrasenaExplicaComoConfigurarla() {

    boolean hayConfiguracionLocal =
            (System.getProperty("db.password") != null
            && !System.getProperty("db.password").isBlank())
            || (System.getenv("DB_PASSWORD") != null
            && !System.getenv("DB_PASSWORD").isBlank())
            || Files.isRegularFile(Path.of("ferreteria.properties"))
            || Files.isRegularFile(Path.of("config/ferreteria.properties"));

    Assumptions.assumeFalse(
            hayConfiguracionLocal,
            "Se omite porque existen credenciales locales configuradas."
    );

    SQLException error = assertThrows(
            SQLException.class, ConexionBD::conectar);

    assertTrue(error.getMessage().contains("DB_PASSWORD"));
}
}
