package com.senati.biblioteca.bean;

import com.senati.biblioteca.dao.CategoriaDAO;
import com.senati.biblioteca.dao.DevolucionDAO;
import com.senati.biblioteca.dao.UsuarioDAO;
import com.senati.biblioteca.modelo.Categoria;
import com.senati.biblioteca.modelo.Devolucion;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.modelo.Usuario;
import com.senati.biblioteca.servicio.LibroService;
import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class AdminBean implements Serializable {

    @Inject
    private LibroService libroService;

    @Inject
    private PrestamoService prestamoService;

    @Inject
    private CategoriaDAO categoriaDAO;

    @Inject
    private DevolucionDAO devolucionDAO;

    @Inject
    private UsuarioDAO usuarioDAO;

    private Libro libroEdicion = new Libro();
    private Libro libroSeleccionado;
    private List<Libro> listaLibros;
    private List<Prestamo> prestamosVencidos;
    private List<Categoria> categorias;
    private List<Usuario> usuarios;

    private String seccion = "libros";
    private List<Prestamo> prestamosActivos;
    private String keywordPrestamos;
    private Long filtroCategoriaId;
    private Long filtroUsuarioId;
    private boolean filtroConStock;

    private String keywordLibros;
    private Long filtroCategoriaIdLibros;

    private List<Devolucion> devolucionesRegistradas;
    private String keywordDevoluciones;
    private Long filtroCategoriaIdDevoluciones;
    private Date fechaInicioDevoluciones;
    private Date fechaFinDevoluciones;
    private boolean soloRetrasoDevoluciones;

    // Dashboard KPI data
    private List<Object[]> topLibros;
    private List<Object[]> topCategorias;

    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            return;
        }
        try {
            listaLibros = libroService.listarTodosAdmin();
            prestamosVencidos = prestamoService.buscarPrestamosVencidos();
            categorias = categoriaDAO.findAll();
            usuarios = usuarioDAO.findEstudiantes();
            cargarPrestamosActivos();
            cargarDevolucionesRegistradas();
            topLibros = prestamoService.getTopLibrosPrestados(5);
            topCategorias = prestamoService.getTopCategorias(5);
        } catch (Exception e) {
            System.err.println("[AdminBean.init] Error al cargar datos iniciales: " + e.getMessage());
        }
    }

    // Called on preRenderView of the dashboard page (non-postback only)
    public void onDashboardLoad(ComponentSystemEvent event) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            try {
                prestamosVencidos = prestamoService.buscarPrestamosVencidos();
                topLibros = prestamoService.getTopLibrosPrestados(5);
                topCategorias = prestamoService.getTopCategorias(5);
                cargarPrestamosActivos();
                cargarDevolucionesRegistradas();
                if (listaLibros == null) listaLibros = libroService.listarTodosAdmin();
            } catch (Exception e) {
                System.err.println("[AdminBean.onDashboardLoad] " + e.getMessage());
            }
        }
    }

    // ===== KPI GETTERS =====

    public long getKpiPrestamosActivos() {
        return prestamosActivos != null ? prestamosActivos.size() : 0;
    }

    public long getKpiLibrosEnMora() {
        return prestamosVencidos != null ? prestamosVencidos.size() : 0;
    }

    public long getKpiDevolucionesHoy() {
        if (devolucionesRegistradas == null) return 0;
        LocalDate hoy = LocalDate.now();
        return devolucionesRegistradas.stream()
            .filter(d -> {
                if (d.getFechaDevolucion() == null) return false;
                return toLocalDate(d.getFechaDevolucion()).equals(hoy);
            })
            .count();
    }

    public long getKpiTotalLibros() {
        return listaLibros != null ? listaLibros.size() : 0;
    }

    public List<Object[]> getTopLibros() {
        return topLibros != null ? topLibros : Collections.emptyList();
    }

    public List<Object[]> getTopCategorias() {
        return topCategorias != null ? topCategorias : Collections.emptyList();
    }

    public long getTopLibrosMaxCount() {
        if (topLibros == null || topLibros.isEmpty()) return 1;
        Object val = topLibros.get(0)[1];
        return val instanceof Number ? ((Number) val).longValue() : 1L;
    }

    public long getTopCategoriasMaxCount() {
        if (topCategorias == null || topCategorias.isEmpty()) return 1;
        Object val = topCategorias.get(0)[1];
        return val instanceof Number ? ((Number) val).longValue() : 1L;
    }

    public Date getFechaHoy() {
        return new Date();
    }

    // ===== SEMAPHORE =====

    public String getSemaforoFecha(Prestamo p) {
        if (p.getFechaDevolucionEstimada() == null) return "verde";
        LocalDate hoy = LocalDate.now();
        LocalDate estimada = toLocalDate(p.getFechaDevolucionEstimada());
        long dias = ChronoUnit.DAYS.between(hoy, estimada);
        if (dias < 0) return "rojo";
        if (dias <= 3) return "ambar";
        return "verde";
    }

    // ===== QUICK RETURN ACTION =====

    public void registrarDevolucionRapida(Long prestamoId) {
        try {
            prestamoService.registrarDevolucion(prestamoId);
            prestamosVencidos = prestamoService.buscarPrestamosVencidos();
            cargarPrestamosActivos();
            cargarDevolucionesRegistradas();
            topLibros = null; // invalidate stats cache
            topCategorias = null;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Devolución registrada correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar devolución", e.getMessage()));
        }
    }

    // ===== EXISTING SECTION METHODS =====

    public void setSeccion(String seccion) {
        this.seccion = seccion;
        if ("prestamos".equals(seccion)) {
            cargarPrestamosActivos();
        } else if ("devoluciones".equals(seccion)) {
            cargarDevolucionesRegistradas();
        } else {
            listaLibros = libroService.listarTodosAdmin();
        }
    }

    public String getSeccion() { return seccion; }

    public void mostrarLibros() { setSeccion("libros"); }
    public void mostrarPrestamos() { setSeccion("prestamos"); }
    public void mostrarDevoluciones() { setSeccion("devoluciones"); }

    public void cargarPrestamosActivos() {
        prestamosActivos = prestamoService.buscarPrestamosActivosConFiltros(keywordPrestamos, filtroCategoriaId, filtroUsuarioId);
        prestamosActivos = prestamosActivos.stream()
            .filter(p -> p.getLibro().isActivo())
            .filter(p -> !filtroConStock || libroService.stockDisponible(p.getLibro()) > 0)
            .collect(Collectors.toList());
    }

    public void cargarDevolucionesRegistradas() {
        try {
            devolucionesRegistradas = devolucionDAO.listarConFiltros(
                keywordDevoluciones, filtroCategoriaIdDevoluciones,
                fechaInicioDevoluciones, fechaFinDevoluciones, soloRetrasoDevoluciones);
        } catch (Exception e) {
            System.err.println("[AdminBean] Error cargando devoluciones: " + e.getMessage());
            devolucionesRegistradas = Collections.emptyList();
        }
    }

    public void buscarDevoluciones() { cargarDevolucionesRegistradas(); }

    public void limpiarFiltrosDevoluciones() {
        keywordDevoluciones = null;
        filtroCategoriaIdDevoluciones = null;
        fechaInicioDevoluciones = null;
        fechaFinDevoluciones = null;
        soloRetrasoDevoluciones = false;
        cargarDevolucionesRegistradas();
    }

    public void buscarPrestamos() { cargarPrestamosActivos(); }

    public void limpiarFiltros() {
        keywordPrestamos = null;
        filtroCategoriaId = null;
        filtroUsuarioId = null;
        filtroConStock = false;
        cargarPrestamosActivos();
    }

    public void buscarLibros() {
        listaLibros = libroService.buscarPorKeywordYCategoriaAdmin(keywordLibros, filtroCategoriaIdLibros);
    }

    public void limpiarFiltrosLibros() {
        keywordLibros = null;
        filtroCategoriaIdLibros = null;
        listaLibros = libroService.listarTodosAdmin();
    }

    public long getDiasVencidos(Prestamo prestamo) {
        if (prestamo.getFechaDevolucionEstimada() == null) return 0;
        LocalDate hoy = LocalDate.now();
        LocalDate estimada = toLocalDate(prestamo.getFechaDevolucionEstimada());
        long dias = ChronoUnit.DAYS.between(estimada, hoy);
        return Math.max(0, dias);
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void alternarEstadoLibro() {
        if (libroSeleccionado != null) {
            libroService.alternarActivo(libroSeleccionado.getId());
            boolean estabaActivo = libroSeleccionado.isActivo();
            listaLibros = libroService.listarTodosAdmin();
            String estado = estabaActivo ? "desactivado" : "activado";
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Libro " + estado + " correctamente."));
            libroSeleccionado = null;
        }
    }

    public void alternarEstadoLibro(Libro libro) {
        libroSeleccionado = libro;
        alternarEstadoLibro();
    }

    public String crearLibro() {
        libroService.crear(libroEdicion);
        libroEdicion = new Libro();
        listaLibros = libroService.listarTodosAdmin();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Libro creado correctamente."));
        return null;
    }

    public void editarLibro(Libro libro) {
        libroEdicion = libro;
    }

    public void guardarEdicion() {
        libroService.actualizar(libroEdicion);
        libroEdicion = new Libro();
        listaLibros = libroService.listarTodosAdmin();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Libro actualizado correctamente."));
    }

    public void cerrarEditor() {
        libroEdicion = new Libro();
    }

    public List<Prestamo> buscarPorRango(Date inicio, Date fin) {
        return prestamoService.buscarPrestamosPorRangoFechas(inicio, fin);
    }

    public void prepararNuevo() {
        libroEdicion = new Libro();
    }

    // ===== GETTERS / SETTERS =====

    public Libro getLibroEdicion() { return libroEdicion; }
    public Libro getLibroSeleccionado() { return libroSeleccionado; }
    public void setLibroSeleccionado(Libro libroSeleccionado) { this.libroSeleccionado = libroSeleccionado; }
    public List<Libro> getListaLibros() { return listaLibros; }
    public List<Prestamo> getPrestamosVencidos() { return prestamosVencidos; }

    public List<Categoria> getCategorias() {
        if (categorias == null) {
            categorias = categoriaDAO.findAll();
        }
        return categorias;
    }

    public List<Prestamo> getPrestamosActivos() { return prestamosActivos; }
    public String getKeywordPrestamos() { return keywordPrestamos; }
    public void setKeywordPrestamos(String keywordPrestamos) { this.keywordPrestamos = keywordPrestamos; }
    public Long getFiltroCategoriaId() { return filtroCategoriaId; }
    public void setFiltroCategoriaId(Long filtroCategoriaId) { this.filtroCategoriaId = filtroCategoriaId; }
    public Long getFiltroUsuarioId() { return filtroUsuarioId; }
    public void setFiltroUsuarioId(Long filtroUsuarioId) { this.filtroUsuarioId = filtroUsuarioId; }
    public List<Usuario> getUsuarios() {
        if (usuarios == null) {
            usuarios = usuarioDAO.findEstudiantes();
        }
        return usuarios;
    }
    public boolean isFiltroConStock() { return filtroConStock; }
    public void setFiltroConStock(boolean filtroConStock) { this.filtroConStock = filtroConStock; }

    public String getKeywordLibros() { return keywordLibros; }
    public void setKeywordLibros(String keywordLibros) { this.keywordLibros = keywordLibros; }
    public Long getFiltroCategoriaIdLibros() { return filtroCategoriaIdLibros; }
    public void setFiltroCategoriaIdLibros(Long filtroCategoriaIdLibros) { this.filtroCategoriaIdLibros = filtroCategoriaIdLibros; }

    public List<Devolucion> getDevolucionesRegistradas() { return devolucionesRegistradas; }
    public String getKeywordDevoluciones() { return keywordDevoluciones; }
    public void setKeywordDevoluciones(String keywordDevoluciones) { this.keywordDevoluciones = keywordDevoluciones; }
    public Long getFiltroCategoriaIdDevoluciones() { return filtroCategoriaIdDevoluciones; }
    public void setFiltroCategoriaIdDevoluciones(Long filtroCategoriaIdDevoluciones) { this.filtroCategoriaIdDevoluciones = filtroCategoriaIdDevoluciones; }
    public Date getFechaInicioDevoluciones() { return fechaInicioDevoluciones; }
    public void setFechaInicioDevoluciones(Date fechaInicioDevoluciones) { this.fechaInicioDevoluciones = fechaInicioDevoluciones; }
    public Date getFechaFinDevoluciones() { return fechaFinDevoluciones; }
    public void setFechaFinDevoluciones(Date fechaFinDevoluciones) { this.fechaFinDevoluciones = fechaFinDevoluciones; }
    public boolean isSoloRetrasoDevoluciones() { return soloRetrasoDevoluciones; }
    public void setSoloRetrasoDevoluciones(boolean soloRetrasoDevoluciones) { this.soloRetrasoDevoluciones = soloRetrasoDevoluciones; }
}
