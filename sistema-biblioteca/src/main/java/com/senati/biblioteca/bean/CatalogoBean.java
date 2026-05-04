package com.senati.biblioteca.bean;

import com.senati.biblioteca.modelo.Categoria;
import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.servicio.LibroService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class CatalogoBean implements Serializable {

    @Inject
    private LibroService libroService;

    private String keyword;
    private Long categoriaSeleccionadaId;
    private List<Libro> resultados;
    private List<Categoria> categorias;
    private Libro libroSeleccionado;

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
    }

    public void cerrarDetalle() {
        this.libroSeleccionado = null;
    }

    public Libro getLibroSeleccionado() { return libroSeleccionado; }

    public List<Libro> getResultados() { return resultados; }
    public List<Categoria> getCategorias() { return categorias; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCategoriaSeleccionadaId() { return categoriaSeleccionadaId; }
    public void setCategoriaSeleccionadaId(Long categoriaSeleccionadaId) { this.categoriaSeleccionadaId = categoriaSeleccionadaId; }
}
