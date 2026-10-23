package pe.ferreteria.servicio;

import pe.ferreteria.dao.CompraDAO;
import pe.ferreteria.dao.DetalleCompraDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Compra;
import pe.ferreteria.modelo.DetalleCompra;

import java.sql.Connection;
import java.util.List;

public class CompraService {

    private final CompraDAO compraDAO = new CompraDAO();
    private final DetalleCompraDAO detalleCompraDAO =
            new DetalleCompraDAO();

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