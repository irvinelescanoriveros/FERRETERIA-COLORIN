package pe.ferreteria.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

public class GeneradorEtiquetaDefectuoso {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

    private GeneradorEtiquetaDefectuoso() {
    }

    public static class DatosEtiqueta {

        private final int idKardex;
        private final String producto;
        private final String codigo;
        private final String categoria;
        private final String unidad;
        private final String marca;
        private final String proveedor;
        private final String lote;
        private final int cantidad;
        private final String usuario;
        private final String motivo;
        private final String comprobante;
        private final LocalDateTime fechaIngreso;

        public DatosEtiqueta(
                int idKardex,
                String producto,
                String codigo,
                String categoria,
                String unidad,
                String marca,
                String proveedor,
                String lote,
                int cantidad,
                String usuario,
                String motivo,
                String comprobante,
                LocalDateTime fechaIngreso) {

            this.idKardex = idKardex;
            this.producto = producto;
            this.codigo = codigo;
            this.categoria = categoria;
            this.unidad = unidad;
            this.marca = marca;
            this.proveedor = proveedor;
            this.lote = lote;
            this.cantidad = cantidad;
            this.usuario = usuario;
            this.motivo = motivo;
            this.comprobante = comprobante;
            this.fechaIngreso = fechaIngreso;
        }
    }

    public static Path generarYabrir(
            DatosEtiqueta datos)
            throws IOException {

        if (datos == null) {

            throw new IllegalArgumentException(
                    "Los datos de la etiqueta no pueden ser nulos."
            );
        }

        if (datos.idKardex <= 0) {

            throw new IllegalArgumentException(
                    "El ID Kardex debe ser válido."
            );
        }

        LocalDateTime fecha =
                datos.fechaIngreso != null
                ? datos.fechaIngreso
                : LocalDateTime.now();

        String anio =
                String.valueOf(
                        fecha.getYear()
                );

        String mes =
                String.format(
                        "%02d",
                        fecha.getMonthValue()
                );

        Path carpeta =
                Paths.get(
                        "etiquetas_defectuosos",
                        anio,
                        mes
                );

        Files.createDirectories(
                carpeta
        );

        String nombreArchivo =
                "DEFECTUOSO_KARDEX_"
                + datos.idKardex
                + "_"
                + safe(
                        datos.comprobante
                )
                + "_"
                + fecha.format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMdd_HHmmss"
                        )
                )
                + ".pdf";

        Path rutaArchivo =
                carpeta.resolve(
                        nombreArchivo
                );

        Document documento =
                new Document(
                        PageSize.A7,
                        8,
                        8,
                        6,
                        6
                );

        try {

            PdfWriter.getInstance(
                    documento,
                    Files.newOutputStream(
                            rutaArchivo
                    )
            );

            documento.open();

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            7f,
                            Font.NORMAL
                    );

            Font negrita =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            7f,
                            Font.BOLD
                    );

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10f,
                            Font.BOLD
                    );

            Font comprobante =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8f,
                            Font.BOLD
                    );

            Font alerta =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8f,
                            Font.BOLD
                    );

            /*
             * CABECERA
             */
            Paragraph empresa =
                    new Paragraph(
                            "FERRETERÍA COLORÍN",
                            titulo
                    );

            empresa.setAlignment(
                    Element.ALIGN_CENTER
            );

            empresa.setSpacingAfter(0);

            documento.add(
                    empresa
            );

            Paragraph defecto =
                    new Paragraph(
                            "PRODUCTO DEFECTUOSO",
                            titulo
                    );

            defecto.setAlignment(
                    Element.ALIGN_CENTER
            );

            defecto.setSpacingAfter(2);

            documento.add(
                    defecto
            );

            agregarLinea(
                    documento
            );

            /*
             * IDENTIFICACIÓN
             */
            Paragraph idKardex =
                    new Paragraph(
                            "ID KARDEX: "
                            + datos.idKardex,
                            comprobante
                    );

            idKardex.setAlignment(
                    Element.ALIGN_CENTER
            );

            idKardex.setSpacingAfter(1);

            documento.add(
                    idKardex
            );

            Paragraph comp =
                    new Paragraph(
                            "COMPROBANTE DE VENTA: "
                            + safe(
                                    datos.comprobante
                            ),
                            comprobante
                    );

            comp.setAlignment(
                    Element.ALIGN_CENTER
            );

            comp.setSpacingAfter(2);

            documento.add(
                    comp
            );

            agregarLinea(
                    documento
            );

            /*
             * DATOS
             */
            PdfPTable tabla =
                    new PdfPTable(2);

            tabla.setWidthPercentage(
                    100
            );

            tabla.setWidths(
                    new float[]{
                        1.15f,
                        2.85f
                    }
            );

            tabla.setSpacingBefore(2);
            tabla.setSpacingAfter(2);

            agregarDato(
                    tabla,
                    "Producto:",
                    datos.producto,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Código:",
                    datos.codigo,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Categoría:",
                    datos.categoria,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Unidad:",
                    datos.unidad,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Marca:",
                    datos.marca,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Proveedor:",
                    datos.proveedor,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Lote:",
                    datos.lote,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Cantidad:",
                    String.valueOf(
                            datos.cantidad
                    ),
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Usuario:",
                    datos.usuario,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Motivo:",
                    datos.motivo,
                    negrita,
                    normal
            );

            agregarDato(
                    tabla,
                    "Ingreso a defectuoso:",
                    fecha.format(
                            FORMATO_FECHA
                    ),
                    negrita,
                    normal
            );

            documento.add(
                    tabla
            );

            agregarLinea(
                    documento
            );

            Paragraph noVenta =
                    new Paragraph(
                            "NO APTO PARA VENTA",
                            alerta
                    );

            noVenta.setAlignment(
                    Element.ALIGN_CENTER
            );

            noVenta.setSpacingBefore(2);
            noVenta.setSpacingAfter(0);

            documento.add(
                    noVenta
            );

        } catch (DocumentException e) {

            throw new IOException(
                    "No se pudo generar la etiqueta.",
                    e
            );

        } finally {

            documento.close();
        }

        /*
         * Abrir automáticamente el PDF.
         */
        try {

            if (java.awt.Desktop.isDesktopSupported()) {

                java.awt.Desktop.getDesktop()
                        .open(
                                rutaArchivo.toFile()
                        );
            }

        } catch (Exception e) {

            throw new IOException(
                    "El PDF se generó correctamente, "
                    + "pero no se pudo abrir automáticamente.",
                    e
            );
        }

        return rutaArchivo;
    }

    private static void agregarDato(
            PdfPTable tabla,
            String etiqueta,
            String valor,
            Font fuenteEtiqueta,
            Font fuenteValor) {

        PdfPCell celdaEtiqueta =
                new PdfPCell(
                        new Phrase(
                                safe(etiqueta),
                                fuenteEtiqueta
                        )
                );

        celdaEtiqueta.setBorder(
                PdfPCell.NO_BORDER
        );

        celdaEtiqueta.setPadding(
                1
        );

        tabla.addCell(
                celdaEtiqueta
        );

        PdfPCell celdaValor =
                new PdfPCell(
                        new Phrase(
                                safe(valor),
                                fuenteValor
                        )
                );

        celdaValor.setBorder(
                PdfPCell.NO_BORDER
        );

        celdaValor.setPadding(
                1
        );

        tabla.addCell(
                celdaValor
        );
    }

    private static void agregarLinea(
            Document documento)
            throws DocumentException {

        Paragraph linea =
                new Paragraph(
                        "------------------------------------------"
                );

        linea.setSpacingBefore(0);
        linea.setSpacingAfter(1);

        documento.add(
                linea
        );
    }

    private static String safe(
            String texto) {

        return texto == null
                ? ""
                : texto;
    }

    public static void abrirExistente(
            int idKardex)
            throws IOException {

        if (idKardex <= 0) {

            throw new IllegalArgumentException(
                    "El ID Kardex es inválido."
            );
        }

        Path carpetaBase =
                Paths.get(
                        "etiquetas_defectuosos"
                );

        if (!Files.exists(carpetaBase)) {

            throw new IOException(
                    "No existe la carpeta de etiquetas."
            );
        }

        Path archivoEncontrado = null;

        java.nio.file.attribute.FileTime ultimaFecha =
                null;

        String prefijo =
                "DEFECTUOSO_KARDEX_"
                + idKardex
                + "_";

        try {

            try (
                    java.util.stream.Stream<Path> archivos =
                            Files.walk(
                                    carpetaBase
                            )
            ) {

                for (
                        java.util.Iterator<Path> iterator =
                                archivos.iterator();
                        iterator.hasNext();
                ) {

                    Path archivo =
                            iterator.next();

                    if (!Files.isRegularFile(
                            archivo
                    )) {
                        continue;
                    }

                    String nombre =
                            archivo.getFileName()
                                    .toString();

                    if (!nombre.startsWith(
                            prefijo
                    )
                            || !nombre.endsWith(
                                    ".pdf"
                            )) {

                        continue;
                    }

                    java.nio.file.attribute.FileTime fechaArchivo =
                            Files.getLastModifiedTime(
                                    archivo
                            );

                    if (archivoEncontrado == null
                            || fechaArchivo.compareTo(
                                    ultimaFecha
                            ) > 0) {

                        archivoEncontrado =
                                archivo;

                        ultimaFecha =
                                fechaArchivo;
                    }
                }
            }

        } catch (IOException e) {

            throw new IOException(
                    "No se pudo buscar la etiqueta PDF.",
                    e
            );
        }

        if (archivoEncontrado == null) {

            throw new IOException(
                    "No se encontró la etiqueta PDF "
                    + "del ID Kardex: "
                    + idKardex
            );
        }

        if (!java.awt.Desktop.isDesktopSupported()) {

            throw new IOException(
                    "No se puede abrir el PDF automáticamente."
            );
        }

        java.awt.Desktop.getDesktop().open(
                archivoEncontrado.toFile()
        );
    }
}