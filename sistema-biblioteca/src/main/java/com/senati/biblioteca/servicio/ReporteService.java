package com.senati.biblioteca.servicio;

import com.senati.biblioteca.dao.DevolucionDAO;
import com.senati.biblioteca.dao.PrestamoDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ReporteService {

    @Inject
    private PrestamoDAO prestamoDAO;

    @Inject
    private DevolucionDAO devolucionDAO;

    /** Cantidad de filas para los rankings (libros, morosidad). */
    public static final int TOP = 8;

    private static final String[] MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    // ============================================================
    // Datos crudos (para graficos y tablas en la vista)
    // ============================================================

    public List<Object[]> datosLibrosMasPrestados() {
        return prestamoDAO.findLibrosMasPrestadosDetalle(TOP);
    }

    public List<Object[]> datosMorosidad() {
        return devolucionDAO.findMorosidadPorUsuario(TOP);
    }

    public List<Object[]> datosPorCategoria() {
        return prestamoDAO.findLibrosPorCategoria(50);
    }

    /** Devuelve 12 valores (Ene..Dic) con el conteo de prestamos del anio. */
    public long[] datosTendencia(int anio) {
        long[] meses = new long[12];
        for (Object[] row : prestamoDAO.findPrestamosPorMes(anio)) {
            int mes = ((Number) row[0]).intValue();
            long total = ((Number) row[1]).longValue();
            if (mes >= 1 && mes <= 12) {
                meses[mes - 1] = total;
            }
        }
        return meses;
    }

    public List<Integer> aniosDisponibles() {
        return prestamoDAO.findAniosConPrestamos();
    }

    public String nombreMes(int indice) {
        return (indice >= 0 && indice < 12) ? MESES[indice] : "";
    }

    // ============================================================
    // Reporte 1: Libros mas prestados
    // ============================================================

    public byte[] pdfLibrosMasPrestados() {
        List<String[]> filas = new ArrayList<>();
        for (Object[] r : datosLibrosMasPrestados()) {
            filas.add(new String[]{
                texto(r[0]), texto(r[1]), texto(r[2]), texto(r[3]), texto(r[4])
            });
        }
        return generarPDF("Libros mas prestados (Top " + TOP + ")",
            new String[]{"Titulo", "Autor", "Categoria", "Ejemplares", "Prestamos"},
            filas, new float[]{34, 25, 18, 11, 12});
    }

    public byte[] excelLibrosMasPrestados() {
        return generarExcel("Libros mas prestados",
            new String[]{"Titulo", "Autor", "Categoria", "Ejemplares", "Prestamos"},
            datosLibrosMasPrestados());
    }

    // ============================================================
    // Reporte 2: Usuarios con mas morosidad
    // ============================================================

    public byte[] pdfMorosidad() {
        List<String[]> filas = new ArrayList<>();
        for (Object[] r : datosMorosidad()) {
            filas.add(new String[]{
                texto(r[0]), texto(r[1]), texto(r[2]),
                promedio(r[3]), texto(r[4]),
                Boolean.TRUE.equals(r[5]) ? "Penalizado" : "Habilitado"
            });
        }
        return generarPDF("Usuarios con mas morosidad (Top " + TOP + ")",
            new String[]{"Estudiante", "Codigo", "Dev. tardias", "Dias promedio", "Total dias", "Estado"},
            filas, new float[]{27, 17, 15, 15, 12, 14});
    }

    public byte[] excelMorosidad() {
        List<Object[]> filas = new ArrayList<>();
        for (Object[] r : datosMorosidad()) {
            filas.add(new Object[]{
                texto(r[0]), texto(r[1]), texto(r[2]),
                promedio(r[3]), texto(r[4]),
                Boolean.TRUE.equals(r[5]) ? "Penalizado" : "Habilitado"
            });
        }
        return generarExcel("Usuarios con mas morosidad",
            new String[]{"Estudiante", "Codigo", "Dev. tardias", "Dias promedio", "Total dias", "Estado"},
            filas);
    }

    // ============================================================
    // Reporte 3: Prestamos por categoria
    // ============================================================

    public byte[] pdfPorCategoria() {
        List<Object[]> datos = datosPorCategoria();
        long total = totalConteo(datos);
        List<String[]> filas = new ArrayList<>();
        for (Object[] r : datos) {
            long c = ((Number) r[1]).longValue();
            filas.add(new String[]{texto(r[0]), String.valueOf(c), porcentaje(c, total)});
        }
        return generarPDF("Prestamos por categoria",
            new String[]{"Categoria", "Prestamos", "Porcentaje"},
            filas, new float[]{50, 25, 25});
    }

    public byte[] excelPorCategoria() {
        List<Object[]> datos = datosPorCategoria();
        long total = totalConteo(datos);
        List<Object[]> filas = new ArrayList<>();
        for (Object[] r : datos) {
            long c = ((Number) r[1]).longValue();
            filas.add(new Object[]{texto(r[0]), c, porcentaje(c, total)});
        }
        return generarExcel("Prestamos por categoria",
            new String[]{"Categoria", "Prestamos", "Porcentaje"}, filas);
    }

    // ============================================================
    // Reporte 4: Tendencia de prestamos por mes
    // ============================================================

    public byte[] pdfTendencia(int anio) {
        long[] meses = datosTendencia(anio);
        List<String[]> filas = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            filas.add(new String[]{MESES[i], String.valueOf(meses[i])});
        }
        return generarPDF("Tendencia de prestamos - " + anio,
            new String[]{"Mes", "Prestamos"}, filas, new float[]{60, 40});
    }

    public byte[] excelTendencia(int anio) {
        long[] meses = datosTendencia(anio);
        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            filas.add(new Object[]{MESES[i], meses[i]});
        }
        return generarExcel("Tendencia " + anio,
            new String[]{"Mes", "Prestamos"}, filas);
    }

    // ============================================================
    // Helpers de formato
    // ============================================================

    private String texto(Object o) {
        return o != null ? o.toString() : "";
    }

    private String promedio(Object o) {
        if (o == null) return "0";
        return String.format("%.1f", ((Number) o).doubleValue());
    }

    private String porcentaje(long parte, long total) {
        if (total <= 0) return "0%";
        return String.format("%.1f%%", parte * 100.0 / total);
    }

    private long totalConteo(List<Object[]> datos) {
        long total = 0;
        for (Object[] r : datos) {
            total += ((Number) r[1]).longValue();
        }
        return total;
    }

    // ============================================================
    // Generacion de PDF (Apache PDFBox)
    // ============================================================

    private byte[] generarPDF(String titulo, String[] columnas, List<String[]> filas,
                              float[] porcentajes) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            float pageWidth = PDRectangle.A4.getWidth();
            float[] anchos = new float[porcentajes.length];
            float totalAncho = pageWidth - 100;
            for (int i = 0; i < porcentajes.length; i++) {
                anchos[i] = totalAncho * porcentajes[i] / 100;
            }

            float y = escribirPagina(doc, titulo, columnas, filas, anchos, 0);

            int filaIdx = (int) ((PDRectangle.A4.getHeight() - y - 50) / 15);
            while (filaIdx < filas.size()) {
                y = escribirPagina(doc, null, columnas, filas, anchos, filaIdx);
                filaIdx += (int) ((PDRectangle.A4.getHeight() - y - 50) / 15);
            }

            doc.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    private float escribirPagina(PDDocument doc, String titulo, String[] columnas,
                                 List<String[]> filas, float[] anchos, int desdeFila) {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = PDRectangle.A4.getHeight() - 50;
            float x = 50;

            if (titulo != null) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                cs.newLineAtOffset(x, y);
                cs.showText(titulo);
                cs.endText();
                y -= 25;
            }

            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
            cs.newLineAtOffset(x, y);
            for (int i = 0; i < columnas.length; i++) {
                cs.showText(columnas[i]);
                cs.newLineAtOffset(anchos[i], 0);
            }
            cs.endText();
            y -= 15;

            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
            for (int i = desdeFila; i < filas.size() && y > 50; i++) {
                String[] fila = filas.get(i);
                cs.beginText();
                cs.newLineAtOffset(x, y);
                for (int j = 0; j < Math.min(fila.length, anchos.length); j++) {
                    String valor = fila[j] != null ? fila[j] : "";
                    if (valor.length() > 35) valor = valor.substring(0, 33) + "..";
                    cs.showText(valor);
                    cs.newLineAtOffset(anchos[j], 0);
                }
                cs.endText();
                y -= 14;
            }

            return y;

        } catch (Exception e) {
            throw new RuntimeException("Error escribiendo pagina PDF", e);
        }
    }

    // ============================================================
    // Generacion de Excel (Apache POI)
    // ============================================================

    private byte[] generarExcel(String tituloHoja, String[] columnas, List<Object[]> filas) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet(tituloHoja);

            CellStyle estiloHeader = wb.createCellStyle();
            Font fontHeader = wb.createFont();
            fontHeader.setBold(true);
            estiloHeader.setFont(fontHeader);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            int rowIdx = 1;
            for (Object[] fila : filas) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < fila.length; i++) {
                    row.createCell(i).setCellValue(fila[i] != null ? fila[i].toString() : "");
                }
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
}
