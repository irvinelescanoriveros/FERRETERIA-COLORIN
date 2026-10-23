package pe.ferreteria.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import pe.ferreteria.infraestructura.ConexionBD;
import pe.ferreteria.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import pe.ferreteria.reporte.DatosReporteGerencial;
import pe.ferreteria.reporte.DetalleVentaReporte;
import pe.ferreteria.reporte.EvolucionReporte;
import pe.ferreteria.reporte.MetodoPagoReporte;
import pe.ferreteria.reporte.RendimientoVendedorReporte;

public class VentaDAO {

    public int insertar(Venta venta) {

        String sql = """
            INSERT INTO Venta
            (id_cliente,
             id_usuario,
             fecha_venta,
             subtotal,
             igv,
             total,
             tipo_comprobante,
             serie,
             numero,
             nro_comprobante,
             metodo_pago,
             monto_recibido,
             vuelto,
             estado_comprobante,
             activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, venta.getIdCliente());
            ps.setInt(2, venta.getIdUsuario());

            if (venta.getFechaVenta() != null) {
                ps.setTimestamp(
                        3,
                        java.sql.Timestamp.valueOf(
                                venta.getFechaVenta()
                        )
                );
            } else {
                ps.setTimestamp(
                        3,
                        new java.sql.Timestamp(
                                System.currentTimeMillis()
                        )
                );
            }

            ps.setBigDecimal(4, venta.getSubtotal());
            ps.setBigDecimal(5, venta.getIgv());
            ps.setBigDecimal(6, venta.getTotal());

            ps.setString(7, venta.getTipoComprobante());
            ps.setString(8, venta.getSerie());
            ps.setInt(9, venta.getNumero());
            ps.setString(10, venta.getNroComprobante());
            ps.setString(11, venta.getMetodoPago());

            ps.setBigDecimal(
                    12,
                    venta.getMontoRecibido() == null
                            ? java.math.BigDecimal.ZERO
                            : venta.getMontoRecibido()
            );

            ps.setBigDecimal(
                    13,
                    venta.getVuelto() == null
                            ? java.math.BigDecimal.ZERO
                            : venta.getVuelto()
            );

            ps.setString(
                    14,
                    venta.getEstadoComprobante() == null
                            ? "Pendiente"
                            : venta.getEstadoComprobante()
            );

            ps.setBoolean(15, venta.isActivo());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int insertar(
            Connection con,
            Venta venta) {

        String sql = """
            INSERT INTO Venta
            (id_cliente,
             id_usuario,
             fecha_venta,
             subtotal,
             igv,
             total,
             tipo_comprobante,
             serie,
             numero,
             nro_comprobante,
             metodo_pago,
             monto_recibido,
             vuelto,
             estado_comprobante,
             activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps =
                con.prepareStatement(
                        sql,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, venta.getIdCliente());
            ps.setInt(2, venta.getIdUsuario());

            if (venta.getFechaVenta() != null) {

                ps.setTimestamp(
                        3,
                        java.sql.Timestamp.valueOf(
                                venta.getFechaVenta()
                        )
                );

            } else {

                ps.setTimestamp(
                        3,
                        new java.sql.Timestamp(
                                System.currentTimeMillis()
                        )
                );
            }

            ps.setBigDecimal(4, venta.getSubtotal());
            ps.setBigDecimal(5, venta.getIgv());
            ps.setBigDecimal(6, venta.getTotal());

            ps.setString(7, venta.getTipoComprobante());
            ps.setString(8, venta.getSerie());
            ps.setInt(9, venta.getNumero());
            ps.setString(10, venta.getNroComprobante());
            ps.setString(11, venta.getMetodoPago());

            ps.setBigDecimal(
                    12,
                    venta.getMontoRecibido() == null
                            ? java.math.BigDecimal.ZERO
                            : venta.getMontoRecibido()
            );

            ps.setBigDecimal(
                    13,
                    venta.getVuelto() == null
                            ? java.math.BigDecimal.ZERO
                            : venta.getVuelto()
            );

            ps.setString(
                    14,
                    venta.getEstadoComprobante() == null
                            ? "Pendiente"
                            : venta.getEstadoComprobante()
            );

            ps.setBoolean(15, venta.isActivo());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs =
                    ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al registrar la venta.",
                    e
            );
        }

        return 0;
    }

    public int obtenerSiguienteNumero(
            Connection con,
            String serie) {

        String sql = """
            SELECT COALESCE(MAX(numero), 0) + 1
            FROM Venta
            WHERE serie = ?
            """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setString(1, serie);

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener el siguiente número de comprobante.",
                    e
            );
        }

        return 1;
    }
    
public Venta buscarPorId(int idVenta) {

    String sql = """
        SELECT
            id_venta,
            id_cliente,
            id_usuario,
            fecha_venta,
            subtotal,
            igv,
            total,
            tipo_comprobante,
            serie,
            numero,
            nro_comprobante,
            metodo_pago,
            monto_recibido,
            vuelto,
            estado_comprobante,
            activo
        FROM Venta
        WHERE id_venta = ?
        """;

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps =
                con.prepareStatement(sql)) {

        ps.setInt(1, idVenta);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Venta venta = new Venta();

                venta.setIdVenta(
                        rs.getInt("id_venta")
                );

                venta.setIdCliente(
                        rs.getInt("id_cliente")
                );

                venta.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                Timestamp fecha =
                        rs.getTimestamp("fecha_venta");

                if (fecha != null) {
                    venta.setFechaVenta(
                            fecha.toLocalDateTime()
                    );
                }

                venta.setSubtotal(
                        rs.getBigDecimal("subtotal")
                );

                venta.setIgv(
                        rs.getBigDecimal("igv")
                );

                venta.setTotal(
                        rs.getBigDecimal("total")
                );

                venta.setTipoComprobante(
                        rs.getString("tipo_comprobante")
                );

                venta.setSerie(
                        rs.getString("serie")
                );

                venta.setNumero(
                        rs.getInt("numero")
                );

                venta.setNroComprobante(
                        rs.getString("nro_comprobante")
                );

                venta.setMetodoPago(
                        rs.getString("metodo_pago")
                );

                venta.setMontoRecibido(
                        rs.getBigDecimal("monto_recibido")
                );

                venta.setVuelto(
                        rs.getBigDecimal("vuelto")
                );

                venta.setEstadoComprobante(
                        rs.getString("estado_comprobante")
                );

                venta.setActivo(
                        rs.getBoolean("activo")
                );

                return venta;
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al buscar la venta por ID.",
                e
        );
    }

    return null;
}
public java.util.List<Venta> listarParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    java.util.List<Venta> lista = new java.util.ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            id_venta,
            id_cliente,
            id_usuario,
            fecha_venta,
            subtotal,
            igv,
            total,
            tipo_comprobante,
            serie,
            numero,
            nro_comprobante,
            metodo_pago,
            monto_recibido,
            vuelto,
            estado_comprobante,
            activo
        FROM Venta
        WHERE activo = true
          AND YEAR(fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND id_usuario = ? ");
    }

    sql.append("""
        ORDER BY fecha_venta ASC, id_venta ASC
        """);

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps =
                 con.prepareStatement(sql.toString())) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Venta venta = new Venta();

                venta.setIdVenta(
                        rs.getInt("id_venta")
                );

                venta.setIdCliente(
                        rs.getInt("id_cliente")
                );

                venta.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                Timestamp fecha =
                        rs.getTimestamp("fecha_venta");

                if (fecha != null) {
                    venta.setFechaVenta(
                            fecha.toLocalDateTime()
                    );
                }

                venta.setSubtotal(
                        rs.getBigDecimal("subtotal")
                );

                venta.setIgv(
                        rs.getBigDecimal("igv")
                );

                venta.setTotal(
                        rs.getBigDecimal("total")
                );

                venta.setTipoComprobante(
                        rs.getString("tipo_comprobante")
                );

                venta.setSerie(
                        rs.getString("serie")
                );

                venta.setNumero(
                        rs.getInt("numero")
                );

                venta.setNroComprobante(
                        rs.getString("nro_comprobante")
                );

                venta.setMetodoPago(
                        rs.getString("metodo_pago")
                );

                venta.setMontoRecibido(
                        rs.getBigDecimal("monto_recibido")
                );

                venta.setVuelto(
                        rs.getBigDecimal("vuelto")
                );

                venta.setEstadoComprobante(
                        rs.getString("estado_comprobante")
                );

                venta.setActivo(
                        rs.getBoolean("activo")
                );

                lista.add(venta);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar las ventas para el reporte.",
                e
        );
    }

    return lista;
}
public DatosReporteGerencial obtenerResumenParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    DatosReporteGerencial resumen =
            new DatosReporteGerencial();

    StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(SUM(v.total), 0) AS total_vendido,
            COUNT(v.id_venta) AS ventas_realizadas,
            COALESCE(SUM(v.igv), 0) AS igv_generado,
            COALESCE(
                SUM(
                    (
                        SELECT COALESCE(
                            SUM(
                                dv.importe_total
                                - (dv.cantidad * i.precio_compra)
                            ),
                            0
                        )
                        FROM Detalle_Venta dv
                        INNER JOIN Inventario i
                            ON i.id_inventario = dv.id_inventario
                        WHERE dv.id_venta = v.id_venta
                    )
                ),
                0
            ) AS ganancia_bruta
        FROM Venta v
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(v.fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(v.fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND v.id_usuario = ? ");
    }

    try (
            Connection con = ConexionBD.conectar();
            PreparedStatement ps =
                    con.prepareStatement(sql.toString())
    ) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                BigDecimal totalVendido =
                        rs.getBigDecimal("total_vendido");

                int ventasRealizadas =
                        rs.getInt("ventas_realizadas");

                BigDecimal igvGenerado =
                        rs.getBigDecimal("igv_generado");

                BigDecimal gananciaBruta =
                        rs.getBigDecimal("ganancia_bruta");

                resumen.setTotalVendido(totalVendido);
                resumen.setVentasRealizadas(ventasRealizadas);
                resumen.setIgvGenerado(igvGenerado);
                resumen.setGananciaBruta(gananciaBruta);

                BigDecimal ticketPromedio =
                        ventasRealizadas > 0
                                ? totalVendido.divide(
                                        BigDecimal.valueOf(
                                                ventasRealizadas
                                        ),
                                        2,
                                        RoundingMode.HALF_UP
                                )
                                : BigDecimal.ZERO;

                resumen.setTicketPromedio(ticketPromedio);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al obtener el resumen "
                + "del reporte.",
                e
        );
    }

    return resumen;
}

public List<RendimientoVendedorReporte> listarRendimientoVendedoresParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<RendimientoVendedorReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            u.id_usuario,
            CONCAT_WS(' ', u.nombre, u.apellidos) AS vendedor,
            COUNT(DISTINCT v.id_venta) AS ventas_realizadas,
            COALESCE(SUM(dv.importe_total), 0) AS total_vendido,
            COALESCE(
                SUM(
                    dv.importe_total
                    - (dv.cantidad * i.precio_compra)
                ),
                0
            ) AS ganancia_bruta
        FROM Venta v
        INNER JOIN Usuario u
            ON u.id_usuario = v.id_usuario
        INNER JOIN Detalle_Venta dv
            ON dv.id_venta = v.id_venta
        INNER JOIN Inventario i
            ON i.id_inventario = dv.id_inventario
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(v.fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(v.fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND v.id_usuario = ? ");
    }

    sql.append("""
        GROUP BY
            u.id_usuario,
            u.nombre,
            u.apellidos
        ORDER BY total_vendido DESC
        """);

    try (
            Connection con = ConexionBD.conectar();
            PreparedStatement ps =
                    con.prepareStatement(sql.toString())
    ) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                RendimientoVendedorReporte reporte =
                        new RendimientoVendedorReporte();

                reporte.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                reporte.setVendedor(
                        rs.getString("vendedor")
                );

                reporte.setVentasRealizadas(
                        rs.getInt("ventas_realizadas")
                );

                reporte.setTotalVendido(
                        rs.getBigDecimal("total_vendido")
                );

                reporte.setGananciaBruta(
                        rs.getBigDecimal("ganancia_bruta")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar el rendimiento "
                + "de vendedores para el reporte.",
                e
        );
    }

    return lista;
}
public List<MetodoPagoReporte> listarMetodosPagoParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<MetodoPagoReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            v.metodo_pago,
            COUNT(v.id_venta) AS operaciones,
            COALESCE(SUM(v.total), 0) AS total
        FROM Venta v
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(" AND MONTH(v.fecha_venta) = ? ");
    }

    if (dia != null) {
        sql.append(" AND DAY(v.fecha_venta) = ? ");
    }

    if (idUsuario != null) {
        sql.append(" AND v.id_usuario = ? ");
    }

    sql.append("""
        GROUP BY v.metodo_pago
        ORDER BY total DESC
        """);

    try (
            Connection con = ConexionBD.conectar();
            PreparedStatement ps =
                    con.prepareStatement(sql.toString())
    ) {

        int parametro = 1;

        ps.setInt(parametro++, anio);

        if (mes != null) {
            ps.setInt(parametro++, mes);
        }

        if (dia != null) {
            ps.setInt(parametro++, dia);
        }

        if (idUsuario != null) {
            ps.setInt(parametro++, idUsuario);
        }

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                MetodoPagoReporte reporte =
                        new MetodoPagoReporte();

                reporte.setMetodoPago(
                        rs.getString("metodo_pago")
                );

                reporte.setOperaciones(
                        rs.getInt("operaciones")
                );

                reporte.setTotal(
                        rs.getBigDecimal("total")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar los métodos de pago "
                + "para el reporte.",
                e
        );
    }

    return lista;
}
public List<EvolucionReporte> listarEvolucionParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<EvolucionReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder();

    if (mes != null) {

        // =========================
        // EVOLUCIÓN MENSUAL
        // POR SEMANAS
        // =========================

        sql.append("""
            SELECT
                CONCAT(
                    'Semana ',
                    x.semana
                ) AS periodo,
                COUNT(*) AS ventas_realizadas,
                COALESCE(
                    SUM(x.total_venta),
                    0
                ) AS total_vendido,
                COALESCE(
                    SUM(x.ganancia_bruta),
                    0
                ) AS ganancia_bruta
            FROM (
                SELECT
                    v.id_venta,
                    FLOOR(
                        (DAY(v.fecha_venta) - 1) / 7
                    ) + 1 AS semana,
                    v.total AS total_venta,
                    COALESCE(
                        SUM(
                            dv.importe_total
                            - (
                                dv.cantidad
                                * i.precio_compra
                            )
                        ),
                        0
                    ) AS ganancia_bruta
                FROM Venta v
                INNER JOIN Detalle_Venta dv
                    ON dv.id_venta = v.id_venta
                INNER JOIN Inventario i
                    ON i.id_inventario = dv.id_inventario
                WHERE v.activo = true
                  AND YEAR(v.fecha_venta) = ?
                  AND MONTH(v.fecha_venta) = ?
                """);

        if (idUsuario != null) {

            sql.append(
                    " AND v.id_usuario = ? "
            );
        }

        sql.append("""
                GROUP BY
                    v.id_venta,
                    v.fecha_venta,
                    v.total
            ) x
            GROUP BY
                x.semana
            ORDER BY
                x.semana
            """);

    } else {

        // =========================
        // EVOLUCIÓN ANUAL
        // POR MESES
        // =========================

        sql.append("""
            SELECT
                MONTH(x.fecha_venta) AS numero_periodo,
                MONTHNAME(x.fecha_venta) AS periodo,
                COUNT(*) AS ventas_realizadas,
                COALESCE(
                    SUM(x.total_venta),
                    0
                ) AS total_vendido,
                COALESCE(
                    SUM(x.ganancia_bruta),
                    0
                ) AS ganancia_bruta
            FROM (
                SELECT
                    v.id_venta,
                    v.fecha_venta,
                    v.total AS total_venta,
                    COALESCE(
                        SUM(
                            dv.importe_total
                            - (
                                dv.cantidad
                                * i.precio_compra
                            )
                        ),
                        0
                    ) AS ganancia_bruta
                FROM Venta v
                INNER JOIN Detalle_Venta dv
                    ON dv.id_venta = v.id_venta
                INNER JOIN Inventario i
                    ON i.id_inventario = dv.id_inventario
                WHERE v.activo = true
                  AND YEAR(v.fecha_venta) = ?
                """);

        if (idUsuario != null) {

            sql.append(
                    " AND v.id_usuario = ? "
            );
        }

        sql.append("""
                GROUP BY
                    v.id_venta,
                    v.fecha_venta,
                    v.total
            ) x
            GROUP BY
                MONTH(x.fecha_venta),
                MONTHNAME(x.fecha_venta)
            ORDER BY
                MONTH(x.fecha_venta)
            """);
    }

    try (
            Connection cn =
                    ConexionBD.conectar();

            PreparedStatement ps =
                    cn.prepareStatement(
                            sql.toString()
                    )
    ) {

        int parametro = 1;

        ps.setInt(
                parametro++,
                anio
        );

        if (mes != null) {

            ps.setInt(
                    parametro++,
                    mes
            );
        }

        if (idUsuario != null) {

            ps.setInt(
                    parametro++,
                    idUsuario
            );
        }

        try (ResultSet rs =
                ps.executeQuery()) {

            while (rs.next()) {

                EvolucionReporte reporte =
                        new EvolucionReporte();

                reporte.setPeriodo(
                        rs.getString("periodo")
                );

                reporte.setVentasRealizadas(
                        rs.getInt("ventas_realizadas")
                );

                reporte.setTotalVendido(
                        rs.getBigDecimal("total_vendido")
                );

                reporte.setGananciaBruta(
                        rs.getBigDecimal("ganancia_bruta")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar la evolución "
                + "de ventas para el reporte.",
                e
        );
    }

    return lista;
}

public List<DetalleVentaReporte> listarDetalleVentasParaReporte(
        Integer dia,
        Integer mes,
        int anio,
        Integer idUsuario) {

    List<DetalleVentaReporte> lista =
            new ArrayList<>();

    StringBuilder sql = new StringBuilder("""
        SELECT
            v.fecha_venta,
            v.nro_comprobante AS comprobante,
            CONCAT_WS(
                ' ',
                u.nombre,
                u.apellidos
            ) AS vendedor,
            CASE
                WHEN c.tipo_cliente = 'Natural'
                    THEN CONCAT_WS(
                        ' ',
                        c.nombre,
                        c.apellidos
                    )
                WHEN c.tipo_cliente = 'Juridica'
                    THEN c.razon_social
                ELSE 'Cliente general'
            END AS cliente,
            v.total,
            v.metodo_pago
        FROM Venta v
        INNER JOIN Usuario u
            ON u.id_usuario = v.id_usuario
        INNER JOIN Cliente c
            ON c.id_cliente = v.id_cliente
        WHERE v.activo = true
          AND YEAR(v.fecha_venta) = ?
        """);

    if (mes != null) {
        sql.append(
                " AND MONTH(v.fecha_venta) = ? "
        );
    }

    if (dia != null) {
        sql.append(
                " AND DAY(v.fecha_venta) = ? "
        );
    }

    if (idUsuario != null) {
        sql.append(
                " AND v.id_usuario = ? "
        );
    }

    sql.append("""
        ORDER BY
            v.fecha_venta ASC,
            v.id_venta ASC
        """);

    try (
            Connection con =
                    ConexionBD.conectar();

            PreparedStatement ps =
                    con.prepareStatement(
                            sql.toString()
                    )
    ) {

        int parametro = 1;

        ps.setInt(
                parametro++,
                anio
        );

        if (mes != null) {
            ps.setInt(
                    parametro++,
                    mes
            );
        }

        if (dia != null) {
            ps.setInt(
                    parametro++,
                    dia
            );
        }

        if (idUsuario != null) {
            ps.setInt(
                    parametro++,
                    idUsuario
            );
        }

        try (ResultSet rs =
                ps.executeQuery()) {

            while (rs.next()) {

                DetalleVentaReporte reporte =
                        new DetalleVentaReporte();

                Timestamp fecha =
                        rs.getTimestamp("fecha_venta");

                if (fecha != null) {
                    reporte.setFechaVenta(
                            fecha.toLocalDateTime()
                    );
                }

                reporte.setComprobante(
                        rs.getString("comprobante")
                );

                reporte.setVendedor(
                        rs.getString("vendedor")
                );

                reporte.setCliente(
                        rs.getString("cliente")
                );

                reporte.setTotal(
                        rs.getBigDecimal("total")
                );

                reporte.setMetodoPago(
                        rs.getString("metodo_pago")
                );

                lista.add(reporte);
            }
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "Error al listar el detalle "
                + "de ventas para el reporte.",
                e
        );
    }

    return lista;
}
}