package com.senati.biblioteca.bean;

import com.senati.biblioteca.servicio.ReporteService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class ReporteBean implements Serializable {

    @Inject
    private ReporteService reporteService;

    private static final String PDF = "application/pdf";
    private static final String XLSX =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String[] MESES_CORTO = {
        "Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    private static final String JSON_VACIO = "{\"labels\":[],\"data\":[]}";

    // ---- Reporte 1: libros mas prestados ----
    private String chartLibrosJson = JSON_VACIO;
    private List<Object[]> datosLibros = new ArrayList<>();

    // ---- Reporte 2: usuarios con morosidad ----
    private String chartMorosidadJson = JSON_VACIO;
    private List<Object[]> datosMorosidad = new ArrayList<>();

    // ---- Reporte 3: prestamos por categoria ----
    private String chartCategoriaJson = JSON_VACIO;
    private List<Object[]> datosCategoria = new ArrayList<>();   // [nombre, count, "xx.x%"]

    // ---- Reporte 4: tendencia por mes ----
    private String chartTendenciaJson = JSON_VACIO;
    private List<Object[]> datosTendencia = new ArrayList<>();   // [mesNombre, count]
    private List<Integer> anios = new ArrayList<>();
    private int anioSeleccionado;

    @PostConstruct
    public void init() {
        cargarTodo();
    }

    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            cargarTodo();
        }
    }

    private void cargarTodo() {
        try {
            anios = reporteService.aniosDisponibles();
            if (anios == null) {
                anios = new ArrayList<>();
            }
            if (anios.isEmpty()) {
                anios.add(Year.now().getValue());
            }
            if (!anios.contains(anioSeleccionado)) {
                anioSeleccionado = anios.get(0);
            }
            cargarLibros();
            cargarMorosidad();
            cargarCategoria();
            cargarTendencia();
        } catch (Exception e) {
            System.err.println("[ReporteBean] Error al cargar reportes: " + e.getMessage());
        }
    }

    public void onAnioChange() {
        cargarTendencia();
    }

    // ============================================================
    // Construccion de datos (tablas + JSON para los graficos)
    // ============================================================

    private void cargarLibros() {
        datosLibros = reporteService.datosLibrosMasPrestados();
        List<String> labels = new ArrayList<>();
        List<Long> valores = new ArrayList<>();
        for (Object[] r : datosLibros) {
            labels.add(acortar(str(r[0]), 16));
            valores.add(((Number) r[4]).longValue());
        }
        chartLibrosJson = chartJson(labels, valores);
    }

    private void cargarMorosidad() {
        datosMorosidad = reporteService.datosMorosidad();
        List<String> labels = new ArrayList<>();
        List<Long> valores = new ArrayList<>();
        for (Object[] r : datosMorosidad) {
            labels.add(acortar(str(r[0]), 16));
            valores.add(((Number) r[2]).longValue());
        }
        chartMorosidadJson = chartJson(labels, valores);
    }

    private void cargarCategoria() {
        List<Object[]> crudo = reporteService.datosPorCategoria();
        long total = 0;
        for (Object[] r : crudo) {
            total += ((Number) r[1]).longValue();
        }
        datosCategoria = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Long> valores = new ArrayList<>();
        for (Object[] r : crudo) {
            long c = ((Number) r[1]).longValue();
            String pct = total > 0 ? String.format("%.1f%%", c * 100.0 / total) : "0%";
            datosCategoria.add(new Object[]{str(r[0]), c, pct});
            labels.add(str(r[0]));
            valores.add(c);
        }
        chartCategoriaJson = chartJson(labels, valores);
    }

    private void cargarTendencia() {
        long[] meses = reporteService.datosTendencia(anioSeleccionado);
        datosTendencia = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Long> valores = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            datosTendencia.add(new Object[]{reporteService.nombreMes(i), meses[i]});
            labels.add(MESES_CORTO[i]);
            valores.add(meses[i]);
        }
        chartTendenciaJson = chartJson(labels, valores);
    }

    private String chartJson(List<String> labels, List<Long> valores) {
        JsonArrayBuilder l = Json.createArrayBuilder();
        for (String s : labels) {
            l.add(s != null ? s : "");
        }
        JsonArrayBuilder d = Json.createArrayBuilder();
        for (Long n : valores) {
            d.add(n != null ? n : 0L);
        }
        return Json.createObjectBuilder()
            .add("labels", l)
            .add("data", d)
            .build()
            .toString();
    }

    private String str(Object o) {
        return o != null ? o.toString() : "";
    }

    private String acortar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    // ============================================================
    // Descargas PDF / Excel
    // ============================================================

    public void pdfLibros() {
        descargar("libros-mas-prestados.pdf", PDF, reporteService.pdfLibrosMasPrestados());
    }

    public void excelLibros() {
        descargar("libros-mas-prestados.xlsx", XLSX, reporteService.excelLibrosMasPrestados());
    }

    public void pdfMorosidad() {
        descargar("usuarios-morosidad.pdf", PDF, reporteService.pdfMorosidad());
    }

    public void excelMorosidad() {
        descargar("usuarios-morosidad.xlsx", XLSX, reporteService.excelMorosidad());
    }

    public void pdfCategoria() {
        descargar("prestamos-por-categoria.pdf", PDF, reporteService.pdfPorCategoria());
    }

    public void excelCategoria() {
        descargar("prestamos-por-categoria.xlsx", XLSX, reporteService.excelPorCategoria());
    }

    public void pdfTendencia() {
        descargar("tendencia-" + anioSeleccionado + ".pdf", PDF,
            reporteService.pdfTendencia(anioSeleccionado));
    }

    public void excelTendencia() {
        descargar("tendencia-" + anioSeleccionado + ".xlsx", XLSX,
            reporteService.excelTendencia(anioSeleccionado));
    }

    private void descargar(String nombre, String contentType, byte[] datos) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"");
        response.setContentLength(datos.length);
        try (OutputStream out = response.getOutputStream()) {
            out.write(datos);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar reporte", e);
        }
        ctx.responseComplete();
    }

    // ============================================================
    // Getters / setters
    // ============================================================

    public String getChartLibrosJson() { return chartLibrosJson; }
    public String getChartMorosidadJson() { return chartMorosidadJson; }
    public String getChartCategoriaJson() { return chartCategoriaJson; }
    public String getChartTendenciaJson() { return chartTendenciaJson; }

    public List<Object[]> getDatosLibros() { return datosLibros; }
    public List<Object[]> getDatosMorosidad() { return datosMorosidad; }
    public List<Object[]> getDatosCategoria() { return datosCategoria; }
    public List<Object[]> getDatosTendencia() { return datosTendencia; }

    public List<Integer> getAnios() { return anios; }
    public int getAnioSeleccionado() { return anioSeleccionado; }
    public void setAnioSeleccionado(int anioSeleccionado) { this.anioSeleccionado = anioSeleccionado; }
}
