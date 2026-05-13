package com.senati.biblioteca.bean;

import com.senati.biblioteca.servicio.ReporteService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

@Named
@SessionScoped
public class ReporteBean implements Serializable {

    @Inject
    private ReporteService reporteService;

    private int topLibros = 10;
    private int topAutores = 10;
    private int topCategorias = 10;

    public void descargarLibrosMasPrestadosPDF() {
        descargar("libros-mas-prestados.pdf", "application/pdf",
            reporteService.generarLibrosMasPrestados(topLibros));
    }

    public void descargarLibrosMasPrestadosExcel() {
        descargar("libros-mas-prestados.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            reporteService.generarExcelLibrosMasPrestados(topLibros));
    }

    public void descargarAutoresPopularesPDF() {
        descargar("autores-populares.pdf", "application/pdf",
            reporteService.generarAutoresMasPopulares(topAutores));
    }

    public void descargarAutoresPopularesExcel() {
        descargar("autores-populares.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            reporteService.generarExcelAutoresPopulares(topAutores));
    }

    public void descargarPrestamosVencidosPDF() {
        descargar("prestamos-vencidos.pdf", "application/pdf",
            reporteService.generarPrestamosVencidos());
    }

    public void descargarPrestamosVencidosExcel() {
        descargar("prestamos-vencidos.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            reporteService.generarExcelPrestamosVencidos());
    }

    public void descargarLibrosPorCategoriaPDF() {
        descargar("libros-por-categoria.pdf", "application/pdf",
            reporteService.generarLibrosPorCategoria(topCategorias));
    }

    public void descargarLibrosPorCategoriaExcel() {
        descargar("libros-por-categoria.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            reporteService.generarExcelLibrosPorCategoria(topCategorias));
    }

    public void descargarPrestamosPenalizadosPDF() {
        descargar("prestamos-penalizados.pdf", "application/pdf",
            reporteService.generarPrestamosPenalizados());
    }

    public void descargarPrestamosPenalizadosExcel() {
        descargar("prestamos-penalizados.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            reporteService.generarExcelPrestamosPenalizados());
    }

    public boolean isAdmin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null && "ADMIN".equals(session.getAttribute("rol"));
    }

    private void descargar(String nombreArchivo, String contentType, byte[] datos) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentLength(datos.length);
        try (OutputStream out = response.getOutputStream()) {
            out.write(datos);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar reporte", e);
        }
        ctx.responseComplete();
    }

    public int getTopLibros() { return topLibros; }
    public void setTopLibros(int topLibros) { this.topLibros = topLibros; }
    public int getTopAutores() { return topAutores; }
    public void setTopAutores(int topAutores) { this.topAutores = topAutores; }
    public int getTopCategorias() { return topCategorias; }
    public void setTopCategorias(int topCategorias) { this.topCategorias = topCategorias; }
}
