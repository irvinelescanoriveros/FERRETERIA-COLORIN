package pe.ferreteria.principal;

import pe.ferreteria.infraestructura.ConexionBD;
import java.sql.Connection;

public class Principal {

    public static void main(String[] args) {

        try (Connection conexion = ConexionBD.conectar()) {

            System.out.println("FERRETERIA-SISTEMA iniciado correctamente.");
            System.out.println("Conexión a MySQL establecida correctamente.");

        } catch (Exception e) {
            System.out.println("Error al conectar con MySQL.");
            e.printStackTrace();
        }
    }
}