package com.senati.biblioteca.servicio;

import com.senati.biblioteca.dao.PrestamoDAO;
import com.senati.biblioteca.modelo.Prestamo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ReporteService {

    @Inject
    private PrestamoDAO prestamoDAO;

    public byte[] generarLibrosMasPrestados(int top) {
        List<Object[]> datos = prestamoDAO.findLibrosMasPrestados(top);
        List<String[]> filas = new ArrayList<>();
        for (Object[] row : datos) {
            filas.add(new String[]{
                row[0] != null ? row[0].toString() : "",
                row[1] != null ? row[1].toString() : ""
            });
        }
        return generarPDF("Libros mas prestados (Top " + top + ")",
            new String[]{"Titulo", "Cantidad"}, filas,
            new float[]{70, 30});
    }

    public byte[] generarAutoresMasPopulares(int top) {
        List<Object[]> datos = prestamoDAO.findAutoresMasPopulares(top);
        List<String[]> filas = new ArrayList<>();
        for (Object[] row : datos) {
            filas.add(new String[]{
                row[0] != null ? row[0].toString() : "",
                row[1] != null ? row[1].toString() : ""
            });
        }
        return generarPDF("Autores mas populares (Top " + top + ")",
            new String[]{"Autor", "Cantidad"}, filas,
            new float[]{50, 50});
    }

    public byte[] generarPrestamosVencidos() {
        List<Prestamo> vencidos = prestamoDAO.findVencidos();
        List<String[]> filas = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Prestamo p : vencidos) {
            filas.add(new String[]{
                p.getLibro().getTitulo(),
                p.getUsuario().getNombre(),
                sdf.format(p.getFechaDevolucionEstimada()),
                p.getEstado().toString()
            });
        }
        return generarPDF("Prestamos vencidos al " + sdf.format(new Date()),
            new String[]{"Libro", "Usuario", "Vencia", "Estado"}, filas,
            new float[]{35, 25, 20, 20});
    }

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

            // Titulo (solo en primera pagina)
            if (titulo != null) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                cs.newLineAtOffset(x, y);
                cs.showText(titulo);
                cs.endText();
                y -= 25;
            }

            // Encabezados
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
            cs.newLineAtOffset(x, y);
            for (int i = 0; i < columnas.length; i++) {
                cs.showText(columnas[i]);
                cs.newLineAtOffset(anchos[i], 0);
            }
            cs.endText();
            y -= 15;

            // Filas
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
    // Reportes Excel (Apache POI)
    // ============================================================

    public byte[] generarExcelLibrosMasPrestados(int top) {
        List<Object[]> datos = prestamoDAO.findLibrosMasPrestados(top);
        return generarExcel("Libros mas prestados",
            new String[]{"Titulo", "Cantidad de prestamos"}, datos);
    }

    public byte[] generarExcelAutoresPopulares(int top) {
        List<Object[]> datos = prestamoDAO.findAutoresMasPopulares(top);
        return generarExcel("Autores mas populares",
            new String[]{"Autor", "Cantidad de prestamos"}, datos);
    }

    public byte[] generarExcelPrestamosVencidos() {
        List<Prestamo> vencidos = prestamoDAO.findVencidos();
        List<Object[]> datos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Prestamo p : vencidos) {
            datos.add(new Object[]{
                p.getLibro().getTitulo(),
                p.getUsuario().getNombre(),
                sdf.format(p.getFechaDevolucionEstimada()),
                p.getEstado().toString()
            });
        }
        return generarExcel("Prestamos vencidos",
            new String[]{"Libro", "Usuario", "Vencia", "Estado"}, datos);
    }

    private byte[] generarExcel(String tituloHoja, String[] columnas, List<Object[]> filas) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet(tituloHoja);

            // Estilo para encabezados
            CellStyle estiloHeader = wb.createCellStyle();
            Font fontHeader = wb.createFont();
            fontHeader.setBold(true);
            estiloHeader.setFont(fontHeader);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            // Datos
            int rowIdx = 1;
            for (Object[] fila : filas) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < fila.length; i++) {
                    row.createCell(i).setCellValue(fila[i] != null ? fila[i].toString() : "");
                }
            }

            // Auto-ajustar columnas
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
