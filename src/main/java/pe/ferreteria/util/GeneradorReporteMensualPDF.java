package pe.ferreteria.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import pe.ferreteria.reporte.DatosReporteGerencial;
import pe.ferreteria.reporte.EvolucionReporte;

public class GeneradorReporteMensualPDF {

    public Path generar(
            DatosReporteGerencial datos,
            int mes,
            int anio,
            String usuario) throws IOException {

        LocalDate fecha = LocalDate.of(
                anio,
                mes,
                1
        );

        Path carpeta = Path.of(
                "reportes",
                String.valueOf(anio)
        );

        java.nio.file.Files.createDirectories(carpeta);

        String nombreUsuario =
                usuario == null
                ? "Todos"
                : usuario.replaceAll("\\s+", "_");

        Path ruta = carpeta.resolve(
                "Reporte_Gerencial_Mensual_"
                + fecha.format(
                        DateTimeFormatter.ofPattern("yyyy-MM")
                )
                + "_"
                + nombreUsuario
                + ".pdf"
        );

        Document documento =
                new Document(PageSize.A4);

        try {

            PdfWriter.getInstance(
                    documento,
                    java.nio.file.Files.newOutputStream(ruta)
            );

            documento.open();

            // =========================
            // ENCABEZADO
            // =========================

            Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    16
            );

            Paragraph encabezado =
                    new Paragraph(
                            "FERRETERÍA COLORÍN",
                            titulo
                    );

            encabezado.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(encabezado);

            Paragraph subtitulo =
                    new Paragraph(
                            "REPORTE GERENCIAL MENSUAL"
                    );

            subtitulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(subtitulo);

            String nombreMes =
                    fecha.getMonth()
                            .getDisplayName(
                                    java.time.format.TextStyle.FULL,
                                    new Locale("es", "ES")
                            )
                            .toUpperCase();

            documento.add(
                    new Paragraph(
                            "Periodo: "
                            + nombreMes
                            + " "
                            + anio
                    )
            );

            documento.add(
                    new Paragraph(
                            usuario == null
                            ? "Usuario: Todos"
                            : "Vendedor: " + usuario
                    )
            );

            documento.add(
                    new Paragraph(
                            "Fecha de generación: "
                            + LocalDate.now().format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy"
                                    )
                            )
                    )
            );

            // =========================
            // 1. RESUMEN EJECUTIVO
            // =========================

            documento.add(
                    new Paragraph(
                            "1. RESUMEN EJECUTIVO",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            PdfPTable tablaResumen =
                    new PdfPTable(2);

            tablaResumen.setWidthPercentage(100);

            tablaResumen.addCell("Indicador");
            tablaResumen.addCell("Resultado");

            tablaResumen.addCell("Total vendido");
            tablaResumen.addCell(
                    "S/ " + datos.getTotalVendido()
            );

            tablaResumen.addCell("Ganancia bruta");
            tablaResumen.addCell(
                    "S/ " + datos.getGananciaBruta()
            );

            tablaResumen.addCell("Ventas realizadas");
            tablaResumen.addCell(
                    String.valueOf(
                            datos.getVentasRealizadas()
                    )
            );

            tablaResumen.addCell("Ticket promedio");
            tablaResumen.addCell(
                    "S/ " + datos.getTicketPromedio()
            );

            tablaResumen.addCell("IGV generado");
            tablaResumen.addCell(
                    "S/ " + datos.getIgvGenerado()
            );

            documento.add(tablaResumen);

            // =========================
            // 2. EVOLUCIÓN DE VENTAS
            // =========================

            documento.add(
                    new Paragraph(
                            "2. EVOLUCIÓN DE VENTAS DEL MES",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            PdfPTable tablaEvolucion =
                    new PdfPTable(4);

            tablaEvolucion.setWidthPercentage(100);

            String[] encabezadosEvolucion = {
                "Semana",
                "Ventas",
                "Total vendido",
                "Ganancia"
            };

            for (String tituloColumna :
                    encabezadosEvolucion) {

                tablaEvolucion.addCell(
                        new PdfPCell(
                                new Phrase(tituloColumna)
                        )
                );
            }

            int totalVentasEvolucion = 0;

            BigDecimal totalVendidoEvolucion =
                    BigDecimal.ZERO;

            BigDecimal totalGananciaEvolucion =
                    BigDecimal.ZERO;

            for (EvolucionReporte evolucion :
                    datos.getEvolucion()) {

                tablaEvolucion.addCell(
                        evolucion.getPeriodo()
                );

                tablaEvolucion.addCell(
                        String.valueOf(
                                evolucion.getVentasRealizadas()
                        )
                );

                tablaEvolucion.addCell(
                        "S/ "
                        + evolucion.getTotalVendido()
                );

                tablaEvolucion.addCell(
                        "S/ "
                        + evolucion.getGananciaBruta()
                );

                totalVentasEvolucion +=
                        evolucion.getVentasRealizadas();

                totalVendidoEvolucion =
                        totalVendidoEvolucion.add(
                                evolucion.getTotalVendido()
                        );

                totalGananciaEvolucion =
                        totalGananciaEvolucion.add(
                                evolucion.getGananciaBruta()
                        );
            }

            tablaEvolucion.addCell("TOTAL");

            tablaEvolucion.addCell(
                    String.valueOf(
                            totalVentasEvolucion
                    )
            );

            tablaEvolucion.addCell(
                    "S/ "
                    + totalVendidoEvolucion
            );

            tablaEvolucion.addCell(
                    "S/ "
                    + totalGananciaEvolucion
            );

            documento.add(tablaEvolucion);

            // =========================
            // 3. PRODUCTOS MÁS VENDIDOS
            // =========================

            documento.add(
                    new Paragraph(
                            "3. PRODUCTOS MÁS VENDIDOS",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            PdfPTable tablaProductos =
                    new PdfPTable(4);

            tablaProductos.setWidthPercentage(100);

            String[] encabezadosProductos = {
                "Producto",
                "Cantidad",
                "Venta",
                "Ganancia"
            };

            for (String tituloColumna :
                    encabezadosProductos) {

                tablaProductos.addCell(
                        new PdfPCell(
                                new Phrase(tituloColumna)
                        )
                );
            }

            BigDecimal totalVentaProductos =
                    BigDecimal.ZERO;

            BigDecimal totalGananciaProductos =
                    BigDecimal.ZERO;

            int totalCantidadProductos = 0;

            for (var producto :
                    datos.getProductosMasVendidos()) {

                tablaProductos.addCell(
                        producto.getProducto()
                );

                tablaProductos.addCell(
                        String.valueOf(
                                producto.getCantidadVendida()
                        )
                );

                tablaProductos.addCell(
                        "S/ "
                        + producto.getTotalVendido()
                );

                tablaProductos.addCell(
                        "S/ "
                        + producto.getGananciaBruta()
                );

                totalCantidadProductos +=
                        producto.getCantidadVendida();

                totalVentaProductos =
                        totalVentaProductos.add(
                                producto.getTotalVendido()
                        );

                totalGananciaProductos =
                        totalGananciaProductos.add(
                                producto.getGananciaBruta()
                        );
            }

            tablaProductos.addCell("TOTAL");

            tablaProductos.addCell(
                    String.valueOf(
                            totalCantidadProductos
                    )
            );

            tablaProductos.addCell(
                    "S/ " + totalVentaProductos
            );

            tablaProductos.addCell(
                    "S/ " + totalGananciaProductos
            );

            documento.add(tablaProductos);

            // =========================
            // 4. RENDIMIENTO VENDEDORES
            // =========================

            if (usuario == null) {

                documento.add(
                        new Paragraph(
                                "4. RENDIMIENTO DE VENDEDORES",
                                FontFactory.getFont(
                                        FontFactory.HELVETICA_BOLD,
                                        12
                                )
                        )
                );

                PdfPTable tablaVendedores =
                        new PdfPTable(4);

                tablaVendedores.setWidthPercentage(100);

                String[] encabezadosVendedores = {
                    "Vendedor",
                    "Ventas",
                    "Total vendido",
                    "Ganancia"
                };

                for (String tituloColumna :
                        encabezadosVendedores) {

                    tablaVendedores.addCell(
                            new PdfPCell(
                                    new Phrase(
                                            tituloColumna
                                    )
                            )
                    );
                }

                int totalVentasVendedores = 0;

                BigDecimal totalVendidoVendedores =
                        BigDecimal.ZERO;

                BigDecimal totalGananciaVendedores =
                        BigDecimal.ZERO;

                for (var vendedor :
                        datos.getRendimientoVendedores()) {

                    tablaVendedores.addCell(
                            vendedor.getVendedor()
                    );

                    tablaVendedores.addCell(
                            String.valueOf(
                                    vendedor
                                    .getVentasRealizadas()
                            )
                    );

                    tablaVendedores.addCell(
                            "S/ "
                            + vendedor.getTotalVendido()
                    );

                    tablaVendedores.addCell(
                            "S/ "
                            + vendedor.getGananciaBruta()
                    );

                    totalVentasVendedores +=
                            vendedor
                            .getVentasRealizadas();

                    totalVendidoVendedores =
                            totalVendidoVendedores.add(
                                    vendedor
                                    .getTotalVendido()
                            );

                    totalGananciaVendedores =
                            totalGananciaVendedores.add(
                                    vendedor
                                    .getGananciaBruta()
                            );
                }

                tablaVendedores.addCell("TOTAL");

                tablaVendedores.addCell(
                        String.valueOf(
                                totalVentasVendedores
                        )
                );

                tablaVendedores.addCell(
                        "S/ "
                        + totalVendidoVendedores
                );

                tablaVendedores.addCell(
                        "S/ "
                        + totalGananciaVendedores
                );

                documento.add(tablaVendedores);
            }

            // =========================
            // 5. MÉTODOS DE PAGO
            // =========================

            documento.add(
                    new Paragraph(
                            "5. MÉTODOS DE PAGO",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            PdfPTable tablaPagos =
                    new PdfPTable(3);

            tablaPagos.setWidthPercentage(100);

            String[] encabezadosPagos = {
                "Método de pago",
                "Operaciones",
                "Monto"
            };

            for (String tituloColumna :
                    encabezadosPagos) {

                tablaPagos.addCell(
                        new PdfPCell(
                                new Phrase(
                                        tituloColumna
                                )
                        )
                );
            }

            int totalOperacionesPago = 0;

            BigDecimal totalMontoPagos =
                    BigDecimal.ZERO;

            for (var pago :
                    datos.getMetodosPago()) {

                tablaPagos.addCell(
                        pago.getMetodoPago()
                );

                tablaPagos.addCell(
                        String.valueOf(
                                pago.getOperaciones()
                        )
                );

                tablaPagos.addCell(
                        "S/ " + pago.getTotal()
                );

                totalOperacionesPago +=
                        pago.getOperaciones();

                totalMontoPagos =
                        totalMontoPagos.add(
                                pago.getTotal()
                        );
            }

            tablaPagos.addCell("TOTAL");

            tablaPagos.addCell(
                    String.valueOf(
                            totalOperacionesPago
                    )
            );

            tablaPagos.addCell(
                    "S/ " + totalMontoPagos
            );

            documento.add(tablaPagos);

            // =========================
            // 6. MERMA
            // =========================

            documento.add(
                    new Paragraph(
                            "6. MERMA",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            documento.add(
                    new Paragraph(
                            "Merma registrada durante el periodo"
                    )
            );

            PdfPTable tablaMerma =
                    new PdfPTable(5);

            tablaMerma.setWidthPercentage(100);

            String[] encabezadosMerma = {
                "Producto",
                "Marca",
                "Motivo",
                "Cantidad",
                "Costo de merma"
            };

            for (String tituloColumna :
                    encabezadosMerma) {

                tablaMerma.addCell(
                        new PdfPCell(
                                new Phrase(
                                        tituloColumna
                                )
                        )
                );
            }

            int totalMermaCantidad = 0;

            BigDecimal totalCostoMerma =
                    BigDecimal.ZERO;

            for (var merma :
                    datos.getMermas()) {

                tablaMerma.addCell(
                        merma.getProducto()
                );

                tablaMerma.addCell(
                        merma.getMarca()
                );

                tablaMerma.addCell(
                        merma.getMotivo()
                );

                tablaMerma.addCell(
                        String.valueOf(
                                merma.getCantidad()
                        )
                );

                tablaMerma.addCell(
                        "S/ "
                        + merma.getCostoMerma()
                );

                totalMermaCantidad +=
                        merma.getCantidad();

                totalCostoMerma =
                        totalCostoMerma.add(
                                merma.getCostoMerma()
                        );
            }

            tablaMerma.addCell("TOTAL");
            tablaMerma.addCell("");
            tablaMerma.addCell("");

            tablaMerma.addCell(
                    String.valueOf(
                            totalMermaCantidad
                    )
            );

            tablaMerma.addCell(
                    "S/ " + totalCostoMerma
            );

            documento.add(tablaMerma);

            documento.add(
                    new Paragraph(
                            "Productos actualmente en stock defectuoso: "
                            + datos.getStockDefectuosoActual()
                            + " unidades"
                    )
            );

            // =========================
            // 7. ROTACIÓN
            // =========================

            documento.add(
                    new Paragraph(
                            "7. PRODUCTOS CON BAJA O NULA ROTACIÓN",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            documento.add(
                    new Paragraph(
                            "Periodo: "
                            + nombreMes
                            + " "
                            + anio
                    )
            );

            PdfPTable tablaRotacion =
                    new PdfPTable(6);

            tablaRotacion.setWidthPercentage(100);

            String[] encabezadosRotacion = {
                "Producto",
                "Marca",
                "Lote",
                "Días en inventario",
                "Stock",
                "Vendido"
            };

            for (String tituloColumna :
                    encabezadosRotacion) {

                tablaRotacion.addCell(
                        new PdfPCell(
                                new Phrase(
                                        tituloColumna
                                )
                        )
                );
            }

            for (var producto :
                    datos.getRotacionProductos()) {

                tablaRotacion.addCell(
                        producto.getProducto()
                );

                tablaRotacion.addCell(
                        producto.getMarca()
                );

                tablaRotacion.addCell(
                        producto.getLote()
                );

                tablaRotacion.addCell(
                        String.valueOf(
                                producto
                                .getDiasEnInventario()
                        )
                );

                tablaRotacion.addCell(
                        String.valueOf(
                                producto.getStock()
                        )
                );

                tablaRotacion.addCell(
                        String.valueOf(
                                producto.getVendido()
                        )
                );
            }

            documento.add(tablaRotacion);

            // =========================
            // 8. INDICADORES FINALES
            // =========================

            documento.add(
                    new Paragraph(
                            "8. INDICADORES FINALES",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    12
                            )
                    )
            );

            PdfPTable tablaIndicadores =
                    new PdfPTable(2);

            tablaIndicadores.setWidthPercentage(100);

            tablaIndicadores.addCell("Indicador");
            tablaIndicadores.addCell("Resultado");

            tablaIndicadores.addCell(
                    "Ventas realizadas"
            );

            tablaIndicadores.addCell(
                    String.valueOf(
                            datos.getVentasRealizadas()
                    )
            );

            tablaIndicadores.addCell(
                    "Total vendido"
            );

            tablaIndicadores.addCell(
                    "S/ " + datos.getTotalVendido()
            );

            tablaIndicadores.addCell(
                    "Ganancia bruta"
            );

            tablaIndicadores.addCell(
                    "S/ " + datos.getGananciaBruta()
            );

            tablaIndicadores.addCell(
                    "Merma"
            );

            tablaIndicadores.addCell(
                    "S/ " + totalCostoMerma
            );

            tablaIndicadores.addCell(
                    "Stock defectuoso"
            );

            tablaIndicadores.addCell(
                    datos.getStockDefectuosoActual()
                    + " unidades"
            );

            documento.add(tablaIndicadores);

            // =========================
            // PIE
            // =========================

            documento.add(
                    new Paragraph(
                            "FERRETERÍA COLORÍN — "
                            + "Reporte gerencial de ventas"
                    )
            );

            documento.add(
                    new Paragraph(
                            "Información generada a partir de las "
                            + "operaciones registradas en el sistema."
                    )
            );

            documento.close();

        } catch (DocumentException e) {

            throw new IOException(
                    "Error al generar el reporte mensual.",
                    e
            );
        }

        return ruta;
    }
}