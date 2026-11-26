package pe.ferreteria.servicio;

import pe.ferreteria.dao.CompraDAO;
import pe.ferreteria.dao.DetalleCompraDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;

import java.sql.Connection;
import java.util.List;

/**
 * Capa de servicio de Compra: aplica las reglas de negocio y coordina uno o más DAO.
 * Admite inyección de los DAO por constructor para poder probarlo con Mockito.
 */
public class CompraService {

    private final CompraDAO compraDAO;
private final DetalleCompraDAO detalleCompraDAO;

public CompraService() {
    this(new CompraDAO(), new DetalleCompraDAO());
}

/*
 * Constructor con inyección de dependencias.
 * Permite probar el servicio con DAO simulados (Mockito).
 */
public CompraService(
        CompraDAO compraDAO,
        DetalleCompraDAO detalleCompraDAO) {

    this.compraDAO = compraDAO;
    this.detalleCompraDAO = detalleCompraDAO;
}

    public boolean registrarCompra(
            Compra compra,
            List<DetalleCompra> detalles) {

        try (Connection conexion = ConexionBD.conectar()) {

            try {

                conexion.setAutoCommit(false);

                int idCompra =
                        compraDAO.insertar(compra, conexion);

                for (DetalleCompra detalle : detalles) {

                    detalle.setIdCompra(idCompra);

                    detalleCompraDAO.insertar(
                            detalle,
                            conexion,
                            compra.getFechaCompra().toLocalDate()
                    );
                }

                conexion.commit();

                return true;

            } catch (Exception e) {

                conexion.rollback();
                e.printStackTrace();

                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
    public boolean existeNroComprobante(String nroComprobante) {

    return compraDAO.existeNroComprobante(
            nroComprobante
    );
}
}