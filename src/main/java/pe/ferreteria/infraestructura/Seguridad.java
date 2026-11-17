
package pe.ferreteria.infraestructura;

import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Seguridad {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2";
    private static final int ITERACIONES = 210000;
    private static final int BITS_HASH = 256;
    private static final int BYTES_SALT = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private Seguridad() {
    }

    public static String hashear(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía.");
        }

        byte[] salt = new byte[BYTES_SALT];
        RANDOM.nextBytes(salt);

        byte[] hash = derivar(password, salt, ITERACIONES, BITS_HASH);

        return PREFIJO + "$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String password, String hashGuardado) {

        if (password == null || password.isEmpty()
                || hashGuardado == null || hashGuardado.isEmpty()) {
            return false;
        }

        if (!esHash(hashGuardado)) {
            return MessageDigest.isEqual(
                    password.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    hashGuardado.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        try {
            String[] partes = hashGuardado.split("\\$");

            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            byte[] hashCalculado = derivar(
                    password, salt, iteraciones, hashEsperado.length * 8);

            return MessageDigest.isEqual(hashEsperado, hashCalculado);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    
public static boolean requiereActualizacion(String hashGuardado) {

    if (hashGuardado == null || hashGuardado.isEmpty()) {
        return false;
    }

    if (!esHash(hashGuardado)) {
        return true;
    }

    try {
        String[] partes = hashGuardado.split("\\$");
        return Integer.parseInt(partes[1]) < ITERACIONES;

    } catch (IllegalArgumentException e) {
        return true;
    }
}


    public static boolean esHash(String valor) {

        if (valor == null) {
            return false;
        }

        String[] partes = valor.split("\\$");

        return partes.length == 4
                && PREFIJO.equals(partes[0]);
    }

    public static void simularVerificacion(String password) {

        if (password == null || password.isEmpty()) {
            return;
        }

        byte[] salt = new byte[BYTES_SALT];
        derivar(password, salt, ITERACIONES, BITS_HASH);
    }

    private static byte[] derivar(
            String password, byte[] salt, int iteraciones, int bits) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, iteraciones, bits);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITMO);

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo procesar la contraseña.", e);

        } finally {
            spec.clearPassword();
        }
    }
}
