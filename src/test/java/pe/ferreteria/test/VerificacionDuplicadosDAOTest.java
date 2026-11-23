package pe.ferreteria.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import pe.ferreteria.dao.*;
import pe.ferreteria.infraestructura.ConexionBD;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

/**
 * DEF-03: ante un error de base de datos, las verificaciones de duplicado
 * deben fallar de forma segura (asumir que existe) y no devolver "no existe".
 */
class VerificacionDuplicadosDAOTest {

    private MockedStatic<ConexionBD> sinConexion() {
        MockedStatic<ConexionBD> mock = mockStatic(ConexionBD.class);
        mock.when(ConexionBD::conectar).thenThrow(new SQLException("sin conexion"));
        return mock;
    }

    @Test
    @DisplayName("Duplicados: existeDni con error SQL devuelve true (falla segura)")
    void clienteDni() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new ClienteDAO().existeDni("12345678", 0));
        }
    }

    @Test
    @DisplayName("Duplicados: existeRuc de cliente con error SQL devuelve true")
    void clienteRuc() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new ClienteDAO().existeRuc("20123456789", 0));
        }
    }

    @Test
    @DisplayName("Duplicados: existeRuc de proveedor con error SQL devuelve true")
    void proveedorRuc() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new ProveedorDAO().existeRuc("20123456789", 0));
        }
    }

    @Test
    @DisplayName("Duplicados: existeNroComprobante con error SQL devuelve true")
    void compraComprobante() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new CompraDAO().existeNroComprobante("F001-1"));
        }
    }

    @Test
    @DisplayName("Duplicados: existeCodigo de producto con error SQL devuelve true")
    void productoCodigo() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new ProductoDAO().existeCodigo("P-001", 0));
        }
    }

    @Test
    @DisplayName("Duplicados: existeNombre de marca y categoría con error SQL devuelve true")
    void marcaYCategoria() {
        try (MockedStatic<ConexionBD> m = sinConexion()) {
            assertTrue(new MarcaDAO().existeNombre("Bosch", 0));
            assertTrue(new CategoriaDAO().existeNombre("Herramientas", 0));
        }
    }
}
