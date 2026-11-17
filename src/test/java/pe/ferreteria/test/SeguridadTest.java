package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pe.ferreteria.infraestructura.Seguridad;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de seguridad sobre el manejo de contraseñas (RNF-03).
 * Corresponden a los controles A02 (fallas criptográficas) y
 * A07 (fallas de autenticación) de OWASP Top 10.
 */
class SeguridadTest {

    @Test
    @DisplayName("Hash: tiene el formato pbkdf2$iteraciones$sal$hash")
    void hashTieneElFormatoEsperado() {
        String hash = Seguridad.hashear("Clave#2026");

        String[] partes = hash.split("\\$", -1);

        assertEquals(4, partes.length);
        assertEquals("pbkdf2", partes[0]);
        assertEquals("210000", partes[1]);
        assertTrue(Seguridad.esHash(hash));
    }

    @Test
    @DisplayName("Hash: nunca contiene la contraseña original")
    void hashNoContieneLaContrasena() {
        assertFalse(Seguridad.hashear("Clave#2026").contains("Clave#2026"));
    }

    @Test
    @DisplayName("Hash: dos hashes de la misma contraseña son distintos (sal aleatoria)")
    void cadaHashUsaUnaSalDistinta() {
        String hash1 = Seguridad.hashear("Clave#2026");
        String hash2 = Seguridad.hashear("Clave#2026");

        assertNotEquals(hash1, hash2);
        assertTrue(Seguridad.verificar("Clave#2026", hash1));
        assertTrue(Seguridad.verificar("Clave#2026", hash2));
    }

    @Test
    @DisplayName("Hash: una contraseña nula o vacía se rechaza")
    void hashearContrasenaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> Seguridad.hashear(null));
        assertThrows(IllegalArgumentException.class,
                () -> Seguridad.hashear(""));
    }

    @Test
    @DisplayName("Verificar: la contraseña correcta es aceptada")
    void verificaLaContrasenaCorrecta() {
        String hash = Seguridad.hashear("Clave#2026");

        assertTrue(Seguridad.verificar("Clave#2026", hash));
    }

    @Test
    @DisplayName("Verificar: una contraseña incorrecta es rechazada")
    void rechazaUnaContrasenaIncorrecta() {
        String hash = Seguridad.hashear("Clave#2026");

        assertFalse(Seguridad.verificar("clave#2026", hash));
        assertFalse(Seguridad.verificar("Clave#2026 ", hash));
        assertFalse(Seguridad.verificar("otra", hash));
    }

    @Test
    @DisplayName("Verificar: valores nulos o vacíos se rechazan")
    void rechazaValoresNulosOVacios() {
        String hash = Seguridad.hashear("Clave#2026");

        assertFalse(Seguridad.verificar(null, hash));
        assertFalse(Seguridad.verificar("", hash));
        assertFalse(Seguridad.verificar("Clave#2026", null));
    }

    @Test
    @DisplayName("Verificar: un hash corrupto se rechaza sin lanzar excepción")
    void hashCorruptoSeRechazaSinExcepcion() {
        assertFalse(Seguridad.verificar("x", "pbkdf2$abc$!!$??"));
        assertFalse(Seguridad.verificar("x", "pbkdf2$0$AA==$AA=="));
        assertFalse(Seguridad.verificar("x", "pbkdf2$1000$$"));
    }

    @Test
    @DisplayName("Compatibilidad: una contraseña antigua en texto plano se acepta una vez")
    void aceptaContrasenaAntiguaEnTextoPlano() {
        assertTrue(Seguridad.verificar("123", "123"));
        assertFalse(Seguridad.verificar("124", "123"));
    }

    @Test
    @DisplayName("Migración: el texto plano requiere actualización y un hash vigente no")
    void soloElTextoPlanoRequiereActualizacion() {
        assertTrue(Seguridad.requiereActualizacion("123"));
        assertFalse(Seguridad.requiereActualizacion(
                Seguridad.hashear("Clave#2026")));
        assertFalse(Seguridad.requiereActualizacion(null));
        assertFalse(Seguridad.requiereActualizacion(""));
    }

    @Test
    @DisplayName("Migración: un hash con pocas iteraciones es válido pero requiere actualización")
    void hashConPocasIteracionesRequiereActualizacion() {
        // PBKDF2-HMAC-SHA256, 1000 iteraciones, contraseña "abc"
        String hashDebil = "pbkdf2$1000$AAECAwQFBgcICQoLDA0ODw==$"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

        assertTrue(Seguridad.esHash(hashDebil));
        assertTrue(Seguridad.requiereActualizacion(hashDebil));
    }

    @Test
    @DisplayName("Formato: esHash distingue hashes de texto plano")
    void esHashDistingueHashesDeTextoPlano() {
        assertTrue(Seguridad.esHash(Seguridad.hashear("abc")));
        assertFalse(Seguridad.esHash("123"));
        assertFalse(Seguridad.esHash("pbkdf2$1$2"));
        assertFalse(Seguridad.esHash("md5$1$2$3"));
        assertFalse(Seguridad.esHash(null));
    }

    @Test
    @DisplayName("Simular verificación: no lanza excepción con ningún valor")
    void simularVerificacionNoLanzaExcepcion() {
        assertDoesNotThrow(() -> Seguridad.simularVerificacion("abc"));
        assertDoesNotThrow(() -> Seguridad.simularVerificacion(null));
        assertDoesNotThrow(() -> Seguridad.simularVerificacion(""));
    }
}
