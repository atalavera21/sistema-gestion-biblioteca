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
    private List<Libro> listaLibros;
    private List<Prestamo> prestamosVencidos;
    private Long prestamoIdDevolucion;
    private List<Categoria> categorias;

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
    }

    public String guardarLibro() {
        if (libroEdicion.getId() == null) {
            return crearLibro();
        }
        return actualizarLibro();
    }

    public String crearLibro() {
        libroService.crear(libroEdicion);
        libroEdicion = new Libro();
        listaLibros = libroService.listarTodosAdmin();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Libro creado correctamente."));
        return null;
    }

    public String editarLibro(Libro libro) {
        this.libroEdicion = libro;
        return null;
    }

    public String actualizarLibro() {
        libroService.actualizar(libroEdicion);
        libroEdicion = new Libro();
        listaLibros = libroService.listarTodosAdmin();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Libro actualizado."));
        return null;
    }

    public String cancelarEdicion() {
        libroEdicion = new Libro();
        return null;
    }

    public void eliminarLibro(Long id) {
        try {
            libroService.eliminar(id);
            listaLibros = libroService.listarTodosAdmin();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Libro eliminado."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void registrarDevolucion() {
        if (prestamoIdDevolucion != null) {
            prestamoService.registrarDevolucion(prestamoIdDevolucion);
            prestamosVencidos = prestamoService.buscarPrestamosVencidos();
            listaLibros = libroService.listarTodosAdmin();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Devolucion registrada."));
        }
    }

    public List<Prestamo> buscarPorRango(Date inicio, Date fin) {
        return prestamoService.buscarPrestamosPorRangoFechas(inicio, fin);
    }

    public Libro getLibroEdicion() { return libroEdicion; }
    public List<Libro> getListaLibros() { return listaLibros; }
    public List<Prestamo> getPrestamosVencidos() { return prestamosVencidos; }
    public Long getPrestamoIdDevolucion() { return prestamoIdDevolucion; }
    public void setPrestamoIdDevolucion(Long prestamoIdDevolucion) { this.prestamoIdDevolucion = prestamoIdDevolucion; }
    public List<Categoria> getCategorias() { return categorias; }
}
