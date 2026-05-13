package com.senati.biblioteca.bean;

import com.senati.biblioteca.dao.CategoriaDAO;
import com.senati.biblioteca.modelo.Categoria;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Prestamo;
import com.senati.biblioteca.servicio.LibroService;
import com.senati.biblioteca.servicio.PrestamoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
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

    private Libro libroEdicion = new Libro();
    private Libro libroAEliminar;
    private Libro libroSeleccionado;
    private List<Libro> listaLibros;
    private List<Prestamo> prestamosVencidos;
    private Long prestamoIdDevolucion;
    private List<Categoria> categorias;

    private String seccion = "libros";
    private List<Prestamo> prestamosActivos;
    private List<Prestamo> devolucionesPendientes;
    private String keywordPrestamos;
    private Long filtroCategoriaId;
    private boolean filtroConStock;

    private String keywordLibros;
    private Long filtroCategoriaIdLibros;

    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            return;
        }
        listaLibros = libroService.listarTodosAdmin();
        prestamosVencidos = prestamoService.buscarPrestamosVencidos();
        categorias = categoriaDAO.findAll();
        cargarPrestamosActivos();
        cargarDevolucionesPendientes();
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
        if ("prestamos".equals(seccion)) {
            cargarPrestamosActivos();
        } else if ("devoluciones".equals(seccion)) {
            cargarDevolucionesPendientes();
        } else {
            listaLibros = libroService.listarTodosAdmin();
        }
    }

    public String getSeccion() { return seccion; }

    public void mostrarLibros() { setSeccion("libros"); }
    public void mostrarPrestamos() { setSeccion("prestamos"); }
    public void mostrarDevoluciones() { setSeccion("devoluciones"); }

    public void cargarPrestamosActivos() {
        prestamosActivos = prestamoService.buscarPrestamosActivosConFiltros(keywordPrestamos, filtroCategoriaId);
        prestamosActivos = prestamosActivos.stream()
            .filter(p -> p.getLibro().isActivo())
            .filter(p -> !filtroConStock || libroService.stockDisponible(p.getLibro()) > 0)
            .collect(Collectors.toList());
    }

    public void cargarDevolucionesPendientes() {
        devolucionesPendientes = prestamoService.buscarDevolucionesPendientes();
    }

    public void buscarPrestamos() {
        cargarPrestamosActivos();
    }

    public void limpiarFiltros() {
        keywordPrestamos = null;
        filtroCategoriaId = null;
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
        long diff = new Date().getTime() - prestamo.getFechaDevolucionEstimada().getTime();
        return Math.max(0, diff / (1000 * 60 * 60 * 24));
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

    public void editarLibro() {
        // libroEdicion already set by f:setPropertyActionListener
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

    public void prepararEliminar() {
        // libroAEliminar already set by f:setPropertyActionListener
    }

    public void prepararEliminar(Libro libro) {
        libroAEliminar = libro;
    }

    public void ejecutarEliminar() {
        try {
            libroService.eliminar(libroAEliminar.getId());
            listaLibros = libroService.listarTodosAdmin();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Libro eliminado correctamente."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
        libroAEliminar = null;
    }

    public void registrarDevolucion() {
        if (prestamoIdDevolucion != null) {
            prestamoService.registrarDevolucion(prestamoIdDevolucion);
            prestamoIdDevolucion = null;
            prestamosVencidos = prestamoService.buscarPrestamosVencidos();
            listaLibros = libroService.listarTodosAdmin();
            cargarPrestamosActivos();
            cargarDevolucionesPendientes();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Devolucion registrada."));
        }
    }

    public List<Prestamo> buscarPorRango(Date inicio, Date fin) {
        return prestamoService.buscarPrestamosPorRangoFechas(inicio, fin);
    }

    public void prepararNuevo() {
        libroEdicion = new Libro();
    }

    public Libro getLibroEdicion() { return libroEdicion; }
    public Libro getLibroAEliminar() { return libroAEliminar; }
    public Libro getLibroSeleccionado() { return libroSeleccionado; }
    public void setLibroSeleccionado(Libro libroSeleccionado) { this.libroSeleccionado = libroSeleccionado; }
    public List<Libro> getListaLibros() { return listaLibros; }
    public List<Prestamo> getPrestamosVencidos() { return prestamosVencidos; }
    public Long getPrestamoIdDevolucion() { return prestamoIdDevolucion; }
    public void setPrestamoIdDevolucion(Long prestamoIdDevolucion) { this.prestamoIdDevolucion = prestamoIdDevolucion; }
    public List<Categoria> getCategorias() { return categorias; }

    public List<Prestamo> getPrestamosActivos() { return prestamosActivos; }
    public List<Prestamo> getDevolucionesPendientes() { return devolucionesPendientes; }
    public String getKeywordPrestamos() { return keywordPrestamos; }
    public void setKeywordPrestamos(String keywordPrestamos) { this.keywordPrestamos = keywordPrestamos; }
    public Long getFiltroCategoriaId() { return filtroCategoriaId; }
    public void setFiltroCategoriaId(Long filtroCategoriaId) { this.filtroCategoriaId = filtroCategoriaId; }
    public boolean isFiltroConStock() { return filtroConStock; }
    public void setFiltroConStock(boolean filtroConStock) { this.filtroConStock = filtroConStock; }

    public String getKeywordLibros() { return keywordLibros; }
    public void setKeywordLibros(String keywordLibros) { this.keywordLibros = keywordLibros; }
    public Long getFiltroCategoriaIdLibros() { return filtroCategoriaIdLibros; }
    public void setFiltroCategoriaIdLibros(Long filtroCategoriaIdLibros) { this.filtroCategoriaIdLibros = filtroCategoriaIdLibros; }
}
