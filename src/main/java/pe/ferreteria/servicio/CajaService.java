package pe.ferreteria.servicio;

import pe.ferreteria.dao.CajaDAO;
import pe.ferreteria.dao.MovimientoCajaDAO;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Caja;
import pe.ferreteria.modelo.MovimientoCaja;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class CajaService {

    private final CajaDAO cajaDAO;
    private final MovimientoCajaDAO movimientoCajaDAO;

    public CajaService() {
        this.cajaDAO = new CajaDAO();
        this.movimientoCajaDAO = new MovimientoCajaDAO();
    }

    /*
     * Registrar apertura de Caja.
     */
    public boolean registrarApertura(Caja caja) {

        if (caja == null) {
            return false;
        }

        if (caja.getIdUsuarioApertura() <= 0) {
            return false;
        }

        if (caja.getMontoApertura() == null) {
            return false;
        }

        if (caja.getMontoApertura()
                .compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        /*
         * Valores iniciales de una Caja nueva.
         */
        if (caja.getTotalIngresos() == null) {
            caja.setTotalIngresos(BigDecimal.ZERO);
        }

        if (caja.getTotalSalidas() == null) {
            caja.setTotalSalidas(BigDecimal.ZERO);
        }

        if (caja.getMontoEsperado() == null) {
            caja.setMontoEsperado(
                    caja.getMontoApertura()
            );
        }

        if (caja.getEstado() == null
                || caja.getEstado().isBlank()) {
            caja.setEstado("Abierta");
        }

        Connection con = null;

        try {

            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            boolean registrado =
                    cajaDAO.insertar(con, caja);

            if (!registrado) {
                throw new RuntimeException(
                        "No se pudo registrar la apertura de Caja."
                );
            }

            con.commit();

            return true;

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();
            return false;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Buscar la Caja actualmente abierta.
     */
    public Caja buscarCajaAbierta() {
        return cajaDAO.buscarCajaAbierta();
    }

    /*
     * Buscar una Caja por su ID.
     */
    
    /*
 * Buscar la Caja abierta del usuario actual.
 */
public Caja buscarCajaAbiertaPorUsuario(
        int idUsuario) {

    if (idUsuario <= 0) {
        return null;
    }

    return cajaDAO.buscarCajaAbiertaPorUsuario(
            idUsuario
    );
}
    public Caja buscarPorId(int idCaja) {
        return cajaDAO.buscarPorId(idCaja);
    }

    /*
     * Listar todas las Cajas.
     */
    public List<Caja> listar() {
        return cajaDAO.listar();
    }

    /*
     * Registrar un movimiento de Caja.
     */
    public boolean registrarMovimiento(
            MovimientoCaja movimiento) {

        if (movimiento == null) {
            return false;
        }

        if (movimiento.getIdCaja() <= 0) {
            return false;
        }

        if (movimiento.getIdUsuario() <= 0) {
            return false;
        }

        if (movimiento.getMonto() == null
                || movimiento.getMonto()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        Connection con = null;

        try {

            con = ConexionBD.conectar();
            con.setAutoCommit(false);

            boolean registrado =
                    movimientoCajaDAO.insertar(
                            con,
                            movimiento
                    );

            if (!registrado) {
                throw new RuntimeException(
                        "No se pudo registrar el movimiento de Caja."
                );
            }

            con.commit();

            return true;

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();
            return false;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Listar movimientos de una Caja.
     */
    public List<MovimientoCaja> listarMovimientosPorCaja(
            int idCaja) {

        return movimientoCajaDAO.listarPorCaja(idCaja);
    }
    public boolean cerrarCaja(
        int idCaja,
        int idUsuarioCierre,
        BigDecimal montoContado,
        BigDecimal diferencia,
        String observacion) {

    if (idCaja <= 0) return false;
    if (idUsuarioCierre <= 0) return false;
    if (montoContado == null) return false;
    if (montoContado.compareTo(BigDecimal.ZERO) < 0) return false;

    Connection con = null;

    try {
        con = ConexionBD.conectar();
        con.setAutoCommit(false);

        boolean cerrado = cajaDAO.cerrarCaja(
                con,
                idCaja,
                idUsuarioCierre,
                montoContado,
                diferencia,
                observacion
        );

        if (!cerrado) {
            throw new RuntimeException(
                    "No se pudo cerrar la Caja."
            );
        }

        con.commit();
        return true;

    } catch (Exception e) {

        if (con != null) {
            try {
                con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        e.printStackTrace();
        return false;

    } finally {

        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
}