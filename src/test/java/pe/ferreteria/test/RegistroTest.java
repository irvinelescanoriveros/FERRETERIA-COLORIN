package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pe.ferreteria.infraestructura.ChequeoSalud;
import pe.ferreteria.infraestructura.Registro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del monitoreo: registro JSON, medición de operaciones y chequeo de salud. */
class RegistroTest {

    @Test
    @DisplayName("Registro: escribe una línea JSON por evento y escapa caracteres peligrosos")
    void escribeLineasJson(@TempDir Path carpeta) throws IOException {

        System.setProperty("log.dir", carpeta.toString());
        try {
            Registro.iniciar();
            Logger log = Registro.de(RegistroTest.class);

            log.info("evento \"con comillas\"\nsegunda linea");
            for (var h : Logger.getLogger("pe.ferreteria").getHandlers()) {
                h.flush();
            }

            try (Stream<Path> archivos = Files.list(carpeta)) {
                Path archivo = archivos
                        .filter(p -> p.getFileName().toString().endsWith(".log"))
                        .findFirst().orElseThrow();
                String contenido = Files.readString(archivo);

                assertTrue(contenido.contains("\"nivel\":\"INFO\""));
                assertTrue(contenido.contains("\\\"con comillas\\\""));
                assertTrue(contenido.contains("\\nsegunda linea"));
                // una sola línea física por evento
                assertEquals(1, contenido.lines().count());
            }
        } finally {
            Registro.detener();
            System.clearProperty("log.dir");
        }
    }

    @Test
    @DisplayName("Medir: devuelve el resultado de la operación")
    void medirDevuelveElResultado() {
        Logger log = Registro.de(RegistroTest.class);

        int resultado = Registro.medir(log, "prueba", () -> 42);

        assertEquals(42, resultado);
    }

    @Test
    @DisplayName("Medir: propaga las excepciones de la operación")
    void medirPropagaExcepciones() {
        Logger log = Registro.de(RegistroTest.class);

        assertThrows(IllegalStateException.class,
                () -> Registro.medir(log, "falla", () -> {
                    throw new IllegalStateException("x");
                }));
    }

    @Test
    @DisplayName("Chequeo de salud: con la base de datos inaccesible devuelve -1")
    void chequeoSinBaseDevuelveMenosUno() {
        // Se fuerza una conexion imposible (puerto 1) para que la prueba
        // no dependa de si hay un MySQL real en el equipo.
        System.setProperty("db.url", "jdbc:mysql://localhost:1/bd_inexistente");
        System.setProperty("db.username", "prueba");
        System.setProperty("db.password", "prueba");
        try {
            assertEquals(-1, ChequeoSalud.medirBaseDeDatos());
        } finally {
            System.clearProperty("db.url");
            System.clearProperty("db.username");
            System.clearProperty("db.password");
        }
    }
}
