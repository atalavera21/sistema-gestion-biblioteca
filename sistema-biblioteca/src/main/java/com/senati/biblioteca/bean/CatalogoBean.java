package com.senati.biblioteca.bean;

import com.senati.biblioteca.dao.ValoracionDAO;
import com.senati.biblioteca.modelo.Categoria;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Usuario;
import com.senati.biblioteca.modelo.Valoracion;
import com.senati.biblioteca.servicio.LibroService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class CatalogoBean implements Serializable {

    @Inject
    private LibroService libroService;

    @Inject
    private ValoracionDAO valoracionDAO;

    @PersistenceContext
    private EntityManager em;

    private String keyword;
    private Long categoriaSeleccionadaId;
    private List<Libro> resultados;
    private List<Categoria> categorias;
    private Libro libroSeleccionado;
    private Integer ratingSeleccionado;

    @PostConstruct
    public void init() {
        categorias = libroService.listarCategorias();
        aplicarFiltros();
    }

    public void seleccionarCategoria(Long categoriaId) {
        this.categoriaSeleccionadaId = categoriaId;
        aplicarFiltros();
    }

    public void limpiarCategoria() {
        this.categoriaSeleccionadaId = null;
        aplicarFiltros();
    }

    public void buscarPorKeyword() {
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        resultados = libroService.buscarPorKeywordYCategoria(kw, categoriaSeleccionadaId);
    }

    public String getDisponibilidad(Libro libro) {
        return libroService.isDisponible(libro) ? "Disponible" : "Agotado";
    }

    public long stockDisponible(Libro libro) {
        return libroService.stockDisponible(libro);
    }

    public boolean categoriaActiva(Long id) {
        return id != null && id.equals(categoriaSeleccionadaId);
    }

    public String getCategoriaActivaNombre() {
        if (categoriaSeleccionadaId == null) return "Todas las categorías";
        for (Categoria c : categorias) {
            if (c.getId().equals(categoriaSeleccionadaId)) return c.getNombre();
        }
        return "Todas las categorías";
    }

    public void verDetalle(Long libroId) {
        this.libroSeleccionado = libroService.buscarPorId(libroId).orElse(null);
        this.ratingSeleccionado = null;
    }

    public void cerrarDetalle() {
        this.libroSeleccionado = null;
    }

    public List<Integer> estrellasLista(Libro libro) {
        List<Integer> stars = new ArrayList<>();
        if (libro.getPuntuacion() == null) {
            for (int i = 0; i < 5; i++) stars.add(0);
            return stars;
        }
        double rating = libro.getPuntuacion();
        for (int i = 1; i <= 5; i++) {
            if (rating >= i) stars.add(2);
            else if (rating >= i - 0.5) stars.add(1);
            else stars.add(0);
        }
        return stars;
    }

    public void calificar(int puntuacion) {
        String codigo = getCodigoSesion();
        if (codigo == null || libroSeleccionado == null) return;

        try {
            Usuario usuario = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.codigoUniversitario = :codigo", Usuario.class)
                .setParameter("codigo", codigo)
                .getSingleResult();

            valoracionDAO.findByLibroAndUsuario(libroSeleccionado.getId(), usuario.getId())
                .ifPresentOrElse(v -> {
                    v.setPuntuacion(puntuacion);
                    v.setFecha(new java.util.Date());
                    valoracionDAO.save(v);
                }, () -> {
                    Valoracion v = new Valoracion();
                    v.setLibro(libroSeleccionado);
                    v.setUsuario(usuario);
                    v.setPuntuacion(puntuacion);
                    valoracionDAO.save(v);
                });

            libroSeleccionado.setPuntuacion(valoracionDAO.calcularPromedio(libroSeleccionado.getId()));
            libroSeleccionado.setTotalValoraciones(valoracionDAO.contarPorLibro(libroSeleccionado.getId()));
            ratingSeleccionado = puntuacion;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Valoración registrada. ¡Gracias!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la valoración."));
        }
    }

    public String getEstrellasCalificacion(int pos) {
        if (ratingSeleccionado == null) return "pi pi-star-o";
        return pos <= ratingSeleccionado ? "pi pi-star-fill" : "pi pi-star-o";
    }

    private String getCodigoSesion() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return session != null ? (String) session.getAttribute("codigoUniversitario") : null;
    }

    public Libro getLibroSeleccionado() { return libroSeleccionado; }

    public List<Libro> getResultados() { return resultados; }
    public List<Categoria> getCategorias() { return categorias; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCategoriaSeleccionadaId() { return categoriaSeleccionadaId; }
    public void setCategoriaSeleccionadaId(Long categoriaSeleccionadaId) { this.categoriaSeleccionadaId = categoriaSeleccionadaId; }

    public Integer getRatingSeleccionado() { return ratingSeleccionado; }
}

