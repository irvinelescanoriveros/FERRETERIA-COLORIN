package pe.ferreteria.infraestructura;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Registro (logs) del sistema de escritorio, sin dependencias externas.
 * Escribe una línea JSON por evento en logs/ferreteria-N.log (rotación de
 * 1 MB, 5 archivos), fácil de leer con jq/grep o de enviar a un
 * recolector de logs. Nunca se deben registrar contraseñas ni hashes.
 */
public final class Registro {

    private static final String RAIZ = "pe.ferreteria";
    private static final long UMBRAL_LENTO_MS = 1000;
    private static boolean iniciado = false;

    private Registro() {
    }

    /** Configura el registro una sola vez (carpeta logs/ junto a la aplicación). */
    public static synchronized void iniciar() {

        if (iniciado) {
            return;
        }
        iniciado = true;

        Logger raiz = Logger.getLogger(RAIZ);
        raiz.setUseParentHandlers(true);
        raiz.setLevel(Level.INFO);

        try {
            String carpeta = Configuracion.obtener(
                    "log.dir", "LOG_DIR", "logs");
            new File(carpeta).mkdirs();

            FileHandler archivo = new FileHandler(
                    carpeta + File.separator + "ferreteria-%g.log",
                    1_000_000, 5, true);
            archivo.setEncoding("UTF-8");
            archivo.setFormatter(new FormatoJson());
            raiz.addHandler(archivo);

        } catch (Exception e) {
            raiz.log(Level.WARNING,
                    "No se pudo crear el archivo de log: " + e.getMessage());
        }
    }

    /** Cierra los archivos de log y permite volver a llamar a iniciar() (usado en pruebas y al cerrar la aplicación). */
    public static synchronized void detener() {

        Logger raiz = Logger.getLogger(RAIZ);

        for (java.util.logging.Handler h : raiz.getHandlers()) {
            if (h instanceof FileHandler) {
                h.close();
                raiz.removeHandler(h);
            }
        }
        iniciado = false;
    }

    public static Logger de(Class<?> clase) {
        return Logger.getLogger(clase.getName());
    }

    /**
     * Ejecuta una operación y registra su duración. Si supera 1 segundo
     * se registra como advertencia (métrica de rendimiento).
     */
    public static <T> T medir(Logger log, String operacion, Supplier<T> tarea) {

        long inicio = System.nanoTime();
        try {
            return tarea.get();
        } finally {
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            Level nivel = ms > UMBRAL_LENTO_MS ? Level.WARNING : Level.INFO;
            log.log(nivel, "operacion=" + operacion + " duracion_ms=" + ms);
        }
    }

    /** Formato: una línea JSON por evento. */
    static final class FormatoJson extends Formatter {

        @Override
        public String format(LogRecord r) {

            StringBuilder sb = new StringBuilder(160);
            sb.append("{\"ts\":\"").append(Instant.ofEpochMilli(r.getMillis()))
              .append("\",\"nivel\":\"").append(r.getLevel().getName())
              .append("\",\"origen\":\"").append(esc(r.getLoggerName()))
              .append("\",\"mensaje\":\"").append(esc(formatMessage(r)))
              .append('"');

            if (r.getThrown() != null) {
                StringWriter sw = new StringWriter();
                r.getThrown().printStackTrace(new PrintWriter(sw));
                sb.append(",\"error\":\"").append(esc(sw.toString())).append('"');
            }

            return sb.append("}\n").toString();
        }

        private static String esc(String s) {
            if (s == null) {
                return "";
            }
            StringBuilder o = new StringBuilder(s.length() + 8);
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '"' -> o.append("\\\"");
                    case '\\' -> o.append("\\\\");
                    case '\n' -> o.append("\\n");
                    case '\r' -> o.append("\\r");
                    case '\t' -> o.append("\\t");
                    default -> o.append(c < 0x20 ? ' ' : c);
                }
            }
            return o.toString();
        }
    }
}
