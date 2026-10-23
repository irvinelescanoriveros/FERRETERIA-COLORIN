package pe.ferreteria.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

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

public class GeneradorPDF {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    private GeneradorPDF() {
    }

    public static Path generar(
            GeneradorComprobante.DatosComprobante datos)
            throws IOException {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del comprobante no pueden ser nulos."
            );
        }

        /*
         * ========================================
         * CARPETA: comprobantes/AÑO/MES
         * ========================================
         */
        java.time.LocalDateTime fechaVenta =
                datos.getFechaVenta() != null
                        ? datos.getFechaVenta()
                        : java.time.LocalDateTime.now();

        String anio =
                String.valueOf(
                        fechaVenta.getYear()
                );

        String mes =
                String.format(
                        "%02d",
                        fechaVenta.getMonthValue()
                );

        Path carpeta =
                Paths.get(
                        "comprobantes",
                        anio,
                        mes
                );

        Files.createDirectories(carpeta);

        String nombreArchivo =
                datos.getTipoComprobante()
                        .replace(" ", "_")
                        .toUpperCase()
                + "_"
                + datos.getNroComprobante()
                + ".pdf";

        Path rutaArchivo =
                carpeta.resolve(
                        nombreArchivo
                );

        /*
         * ========================================
         * DOCUMENTO A6 VERTICAL
         * ========================================
         */
        Document documento =
                new Document(
                        PageSize.A6,
                        16,
                        16,
                        12,
                        12
                );

        try {

            PdfWriter.getInstance(
                    documento,
                    Files.newOutputStream(
                            rutaArchivo
                    )
            );

            documento.open();

            /*
             * ========================================
             * FUENTES
             * ========================================
             */
            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            6.8f,
                            Font.NORMAL
                    );

            Font negrita =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            6.8f,
                            Font.BOLD
                    );

            Font empresa =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10f,
                            Font.BOLD
                    );

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8.5f,
                            Font.BOLD
                    );

            Font total =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            7.3f,
                            Font.BOLD
                    );

            /*
             * ========================================
             * CABECERA
             * ========================================
             */

            Paragraph razonSocial =
                    new Paragraph(
                            "R O COLORÍN S.R.L.",
                            empresa
                    );

            razonSocial.setAlignment(
                    Element.ALIGN_CENTER
            );

            razonSocial.setSpacingAfter(1);

            documento.add(
                    razonSocial
            );

            Paragraph nombreEmpresa =
                    new Paragraph(
                            "FERRETERÍA COLORÍN",
                            empresa
                    );

            nombreEmpresa.setAlignment(
                    Element.ALIGN_CENTER
            );

            nombreEmpresa.setSpacingAfter(3);

            documento.add(
                    nombreEmpresa
            );

            PdfPTable datosNegocio =
                    new PdfPTable(2);

            datosNegocio.setWidthPercentage(
                    100
            );

            datosNegocio.setWidths(
                    new float[]{
                        1.8f,
                        1.0f
                    }
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "RUC: 20214649374",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "Tel.: 994 263 899",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "Av. los Guindos Mza. C Lote. 2",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "Coo. Coop de Vivienda la Flore",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "(Alt. Km.19 Carretera Central)",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "Chaclacayo - Lima - Perú",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    datosNegocio,
                    "8:00 a. m. - 6:00 p. m.",
                    normal,
                    Element.ALIGN_RIGHT
            );

            documento.add(
                    datosNegocio
            );

            /*
             * ========================================
             * TIPO Y NÚMERO
             * ========================================
             */

            documento.add(
                    new Paragraph(" ")
            );

            Paragraph tipoComprobante =
                    new Paragraph(
                            datos.getTipoComprobante()
                                    .toUpperCase(),
                            titulo
                    );

            tipoComprobante.setAlignment(
                    Element.ALIGN_CENTER
            );

            tipoComprobante.setSpacingAfter(1);

            documento.add(
                    tipoComprobante
            );

            Paragraph numeroComprobante =
                    new Paragraph(
                            datos.getNroComprobante(),
                            titulo
                    );

            numeroComprobante.setAlignment(
                    Element.ALIGN_CENTER
            );

            numeroComprobante.setSpacingAfter(3);

            documento.add(
                    numeroComprobante
            );

            /*
             * ========================================
             * INFORMACIÓN DE VENTA + CLIENTE
             * ========================================
             */

            PdfPTable informacion =
                    new PdfPTable(2);

            informacion.setWidthPercentage(
                    100
            );

            informacion.setWidths(
                    new float[]{
                        1.25f,
                        1.75f
                    }
            );

            /*
             * IZQUIERDA
             */
            PdfPCell izquierda =
                    new PdfPCell();

            izquierda.setPadding(4);
            izquierda.setBorder(
                    PdfPCell.BOX
            );

            izquierda.addElement(
                    new Paragraph(
                            "Fecha: "
                            + FORMATO_FECHA.format(
                                    fechaVenta
                            ),
                            normal
                    )
            );

            izquierda.addElement(
                    new Paragraph(
                            "Moneda: Soles(S/)",
                            normal
                    )
            );

            izquierda.addElement(
                    new Paragraph(
                            "Forma de pago: "
                            + safe(
                                    datos.getMetodoPago()
                            ),
                            normal
                    )
            );

            izquierda.addElement(
                    new Paragraph(
                            "Usuario: "
                            + safe(
                                    datos.getUsuario()
                            ),
                            normal
                    )
            );

            informacion.addCell(
                    izquierda
            );

            /*
             * DERECHA
             */
            PdfPCell derecha =
                    new PdfPCell();

            derecha.setPadding(4);
            derecha.setBorder(
                    PdfPCell.BOX
            );

            Paragraph tituloCliente =
                    new Paragraph(
                            "DATOS DEL CLIENTE",
                            negrita
                    );

            tituloCliente.setSpacingAfter(2);

            derecha.addElement(
                    tituloCliente
            );

            derecha.addElement(
                    new Paragraph(
                            "Cliente: "
                            + safe(
                                    datos.getCliente()
                            ),
                            normal
                    )
            );

            if (datos.getContactoCliente() != null
                    && !datos.getContactoCliente()
                            .isBlank()) {

                derecha.addElement(
                        new Paragraph(
                                "Contacto: "
                                + safe(
                                        datos.getContactoCliente()
                                ),
                                normal
                        )
                );

                derecha.addElement(
                        new Paragraph(
                                "RUC: "
                                + safe(
                                        datos.getIdentificacionCliente()
                                ),
                                normal
                        )
                );

            } else {

                derecha.addElement(
                        new Paragraph(
                                "DNI: "
                                + safe(
                                        datos.getIdentificacionCliente()
                                ),
                                normal
                        )
                );
            }

            informacion.addCell(
                    derecha
            );

            documento.add(
                    informacion
            );

            /*
             * ========================================
             * DETALLE
             * ========================================
             */

            documento.add(
                    new Paragraph(" ")
            );

            Paragraph tituloDetalle =
                    new Paragraph(
                            "DETALLE DE LA VENTA",
                            negrita
                    );

            tituloDetalle.setSpacingAfter(2);

            documento.add(
                    tituloDetalle
            );

            int numeroLinea = 1;

            for (
                    GeneradorComprobante.ItemComprobante item
                    : datos.getItems()) {

                PdfPTable itemTabla =
                        new PdfPTable(2);

                itemTabla.setWidthPercentage(
                        100
                );

                itemTabla.setWidths(
                        new float[]{
                            4.1f,
                            1.5f
                        }
                );

                /*
                 * Primera línea:
                 * número + producto + importe
                 */
                String productoTexto =
                        numeroLinea
                        + ". "
                        + safe(
                                item.getProducto()
                        );

                PdfPCell productoCelda =
                        new PdfPCell(
                                new Phrase(
                                        productoTexto,
                                        normal
                                )
                        );

                productoCelda.setBorder(
                        PdfPCell.NO_BORDER
                );

                productoCelda.setPadding(
                        1.5f
                );

                productoCelda.setHorizontalAlignment(
                        Element.ALIGN_LEFT
                );

                itemTabla.addCell(
                        productoCelda
                );

                PdfPCell importeCelda =
                        new PdfPCell(
                                new Phrase(
                                        "S/ "
                                        + formatear(
                                                item.getImporteTotal()
                                        ),
                                        negrita
                                )
                        );

                importeCelda.setBorder(
                        PdfPCell.NO_BORDER
                );

                importeCelda.setPadding(
                        1.5f
                );

                importeCelda.setHorizontalAlignment(
                        Element.ALIGN_RIGHT
                );

                itemTabla.addCell(
                        importeCelda
                );

                /*
                 * Segunda línea:
                 * lote + cantidad + unidad + precio + descuento
                 */
                String detalleLinea =
                        "Lote: "
                        + safe(
                                item.getLote()
                        )
                        + " | "
                        + item.getCantidad()
                        + " "
                        + safe(
                                item.getUnidad()
                        )
                        + " x S/"
                        + formatear(
                                item.getPrecioUnitario()
                        )
                        + " | Desc.: "
                        + obtenerTextoDescuento(
                                item
                        );

                PdfPCell detalleCelda =
                        new PdfPCell(
                                new Phrase(
                                        detalleLinea,
                                        normal
                                )
                        );

                detalleCelda.setColspan(2);
                detalleCelda.setBorder(
                        PdfPCell.NO_BORDER
                );

                detalleCelda.setPadding(
                        1.5f
                );

                itemTabla.addCell(
                        detalleCelda
                );

                documento.add(
                        itemTabla
                );

                numeroLinea++;
            }

            /*
             * ========================================
             * SEPARADOR
             * ========================================
             */

            documento.add(
                    new Paragraph(
                            "----------------------------------------",
                            normal
                    )
            );

            /*
             * ========================================
             * TOTALES Y PAGO
             * ========================================
             */

            PdfPTable tablaTotales =
                    new PdfPTable(4);

            tablaTotales.setWidthPercentage(
                    100
            );

            tablaTotales.setWidths(
                    new float[]{
                        1.30f,
                        0.90f,
                        1.50f,
                        0.90f
                    }
            );

            boolean efectivo =
                    "Efectivo".equalsIgnoreCase(
                            safe(
                                    datos.getMetodoPago()
                            )
                    );

            /*
             * Fila 1
             */
            if (efectivo) {

                agregarCeldaSinBorde(
                        tablaTotales,
                        "Monto recibido:",
                        normal,
                        Element.ALIGN_LEFT
                );

                agregarCeldaSinBorde(
                        tablaTotales,
                        "S/"
                        + formatear(
                                datos.getMontoRecibido()
                        ),
                        normal,
                        Element.ALIGN_RIGHT
                );

            } else {

                agregarCeldaSinBorde(
                        tablaTotales,
                        "",
                        normal,
                        Element.ALIGN_LEFT
                );

                agregarCeldaSinBorde(
                        tablaTotales,
                        "",
                        normal,
                        Element.ALIGN_RIGHT
                );
            }

            agregarCeldaSinBorde(
                    tablaTotales,
                    "Operaciones gravadas:",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    tablaTotales,
                    "S/"
                    + formatear(
                            datos.getSubtotal()
                    ),
                    normal,
                    Element.ALIGN_RIGHT
            );

            /*
             * Fila 2
             */
            if (efectivo) {

                agregarCeldaSinBorde(
                        tablaTotales,
                        "Vuelto:",
                        negrita,
                        Element.ALIGN_LEFT
                );

                agregarCeldaSinBorde(
                        tablaTotales,
                        "S/"
                        + formatear(
                                datos.getVuelto()
                        ),
                        negrita,
                        Element.ALIGN_RIGHT
                );

            } else {

                agregarCeldaSinBorde(
                        tablaTotales,
                        "",
                        normal,
                        Element.ALIGN_LEFT
                );

                agregarCeldaSinBorde(
                        tablaTotales,
                        "",
                        normal,
                        Element.ALIGN_RIGHT
                );
            }

            agregarCeldaSinBorde(
                    tablaTotales,
                    "IGV (18%):",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    tablaTotales,
                    "S/"
                    + formatear(
                            datos.getIgv()
                    ),
                    normal,
                    Element.ALIGN_RIGHT
            );

            /*
             * Fila 3
             */
            agregarCeldaSinBorde(
                    tablaTotales,
                    "",
                    normal,
                    Element.ALIGN_LEFT
            );

            agregarCeldaSinBorde(
                    tablaTotales,
                    "",
                    normal,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    tablaTotales,
                    "IMPORTE TOTAL:",
                    total,
                    Element.ALIGN_RIGHT
            );

            agregarCeldaSinBorde(
                    tablaTotales,
                    "S/"
                    + formatear(
                            datos.getTotal()
                    ),
                    total,
                    Element.ALIGN_RIGHT
            );

            documento.add(
                    tablaTotales
            );

            /*
             * ========================================
             * TOTAL EN LETRAS
             * ========================================
             */

            documento.add(
                    new Paragraph(" ")
            );

            Paragraph son =
                    new Paragraph(
                            "SON: "
                            + convertirNumeroALetras(
                                    datos.getTotal()
                            ),
                            negrita
                    );

            son.setSpacingAfter(3);

            documento.add(
                    son
            );

            /*
             * ========================================
             * PIE
             * ========================================
             */

            Paragraph gracias =
                    new Paragraph(
                            "Gracias por su compra.",
                            normal
                    );

            gracias.setAlignment(
                    Element.ALIGN_CENTER
            );

            gracias.setSpacingAfter(2);

            documento.add(
                    gracias
            );

            Paragraph pieContacto =
                    new Paragraph(
                            "Tel.: 994 263 899 "
                            + "· Chaclacayo - Lima - Perú",
                            normal
                    );

            pieContacto.setAlignment(
                    Element.ALIGN_CENTER
            );

            pieContacto.setSpacingAfter(2);

            documento.add(
                    pieContacto
            );

            Paragraph pieSistema =
                    new Paragraph(
                            "Comprobante generado por el "
                            + "Sistema de Gestión de Inventario "
                            + "y Ventas - Ferretería Colorín",
                            normal
                    );

            pieSistema.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(
                    pieSistema
            );

        } catch (DocumentException e) {

            throw new IOException(
                    "No se pudo generar el PDF del comprobante.",
                    e
            );

        } finally {

            documento.close();
        }

        return rutaArchivo;
    }

    private static String obtenerTextoDescuento(
            GeneradorComprobante.ItemComprobante item) {

        if (item.getDescuento() == null
                || item.getDescuento()
                        .compareTo(
                                BigDecimal.ZERO
                        ) <= 0) {

            return "—";
        }

        BigDecimal subtotalNormal =
                item.getPrecioUnitario()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getCantidad()
                                )
                        );

        if (subtotalNormal.compareTo(
                BigDecimal.ZERO) == 0) {

            return "—";
        }

        BigDecimal porcentaje =
                item.getDescuento()
                        .multiply(
                                new BigDecimal("100")
                        )
                        .divide(
                                subtotalNormal,
                                2,
                                RoundingMode.HALF_UP
                        );

        return porcentaje
                .stripTrailingZeros()
                .toPlainString()
                + "%";
    }

    private static String formatear(
            BigDecimal numero) {

        if (numero == null) {
            return "0.00";
        }

        return numero
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }

    private static String safe(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    private static void agregarCeldaSinBorde(
            PdfPTable tabla,
            String texto,
            Font fuente,
            int alineacion) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                safe(texto),
                                fuente
                        )
                );

        celda.setBorder(
                PdfPCell.NO_BORDER
        );

        celda.setHorizontalAlignment(
                alineacion
        );

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celda.setPadding(
                1.3f
        );

        tabla.addCell(
                celda
        );
    }

    private static String convertirNumeroALetras(
            BigDecimal numero) {

        if (numero == null) {
            return "CERO SOLES CON 00/100";
        }

        BigDecimal valor =
                numero.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        long soles =
                valor.longValue();

        int centimos =
                valor.subtract(
                        BigDecimal.valueOf(
                                soles
                        )
                )
                .movePointRight(2)
                .intValue();

        return convertirEnteroALetras(
                soles
        ).toUpperCase()
                + " SOLES CON "
                + String.format(
                        "%02d",
                        centimos
                )
                + "/100";
    }

    private static String convertirEnteroALetras(
            long numero) {

        if (numero == 0) {
            return "cero";
        }

        if (numero < 0) {
            return "menos "
                    + convertirEnteroALetras(
                            -numero
                    );
        }

        if (numero < 20) {

            String[] unidades = {
                "cero",
                "uno",
                "dos",
                "tres",
                "cuatro",
                "cinco",
                "seis",
                "siete",
                "ocho",
                "nueve",
                "diez",
                "once",
                "doce",
                "trece",
                "catorce",
                "quince",
                "dieciséis",
                "diecisiete",
                "dieciocho",
                "diecinueve"
            };

            return unidades[(int) numero];
        }

        if (numero < 100) {

            String[] decenas = {
                "",
                "",
                "veinte",
                "treinta",
                "cuarenta",
                "cincuenta",
                "sesenta",
                "setenta",
                "ochenta",
                "noventa"
            };

            if (numero % 10 == 0) {

                return decenas[
                        (int) numero / 10
                ];
            }

            if (numero < 30) {

                return "veinti"
                        + convertirEnteroALetras(
                                numero % 10
                        );
            }

            return decenas[
                    (int) numero / 10
            ]
                    + " y "
                    + convertirEnteroALetras(
                            numero % 10
                    );
        }

        if (numero < 1000) {

            if (numero == 100) {
                return "cien";
            }

            if (numero < 200) {

                return "ciento "
                        + convertirEnteroALetras(
                                numero - 100
                        );
            }

            String[] centenas = {
                "",
                "",
                "doscientos",
                "trescientos",
                "cuatrocientos",
                "quinientos",
                "seiscientos",
                "setecientos",
                "ochocientos",
                "novecientos"
            };

            return centenas[
                    (int) numero / 100
            ]
                    + (
                        numero % 100 == 0
                        ? ""
                        : " "
                        + convertirEnteroALetras(
                                numero % 100
                        )
                    );
        }

        if (numero < 1_000_000) {

            long miles =
                    numero / 1000;

            long resto =
                    numero % 1000;

            String textoMiles =
                    miles == 1
                    ? "mil"
                    : convertirEnteroALetras(
                            miles
                    )
                    + " mil";

            return resto == 0
                    ? textoMiles
                    : textoMiles
                    + " "
                    + convertirEnteroALetras(
                            resto
                    );
        }

        if (numero < 1_000_000_000) {

            long millones =
                    numero / 1_000_000;

            long resto =
                    numero % 1_000_000;

            String textoMillones =
                    millones == 1
                    ? "un millón"
                    : convertirEnteroALetras(
                            millones
                    )
                    + " millones";

            return resto == 0
                    ? textoMillones
                    : textoMillones
                    + " "
                    + convertirEnteroALetras(
                            resto
                    );
        }

        return String.valueOf(
                numero
        );
    }
    public static void abrirExistente(
        String tipoComprobante,
        String nroComprobante,
        java.time.LocalDateTime fechaVenta)
        throws IOException {

    java.time.LocalDateTime fecha =
            fechaVenta != null
            ? fechaVenta
            : java.time.LocalDateTime.now();

    Path carpeta =
            Paths.get(
                    "comprobantes",
                    String.valueOf(fecha.getYear()),
                    String.format(
                            "%02d",
                            fecha.getMonthValue()
                    )
            );

    String nombreArchivo =
            tipoComprobante
                    .replace(" ", "_")
                    .toUpperCase()
            + "_"
            + nroComprobante
            + ".pdf";

    Path ruta =
            carpeta.resolve(nombreArchivo);

    if (!Files.exists(ruta)) {

        throw new IOException(
                "No se encontró el PDF del comprobante:\n"
                + ruta
        );
    }

    if (!java.awt.Desktop.isDesktopSupported()) {

        throw new IOException(
                "No se puede abrir el PDF automáticamente."
        );
    }

    java.awt.Desktop.getDesktop().open(
            ruta.toFile()
    );
}
}