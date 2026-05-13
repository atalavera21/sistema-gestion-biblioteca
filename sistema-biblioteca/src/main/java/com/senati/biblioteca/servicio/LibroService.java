package com.senati.biblioteca.servicio;

import com.senati.biblioteca.dao.CategoriaDAO;
import com.senati.biblioteca.dao.LibroDAO;
import com.senati.biblioteca.dao.PrestamoDAO;
import com.senati.biblioteca.modelo.Categoria;
import com.senati.biblioteca.modelo.Libro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LibroService {

    @Inject
    private LibroDAO libroDAO;

    @Inject
    private PrestamoDAO prestamoDAO;

    @Inject
    private CategoriaDAO categoriaDAO;

    public List<Libro> listarTodos() {
        return libroDAO.findAll();
    }

    public List<Libro> listarTodosAdmin() {
        return libroDAO.findAllAdmin();
    }

    public Optional<Libro> buscarPorId(Long id) {
        return libroDAO.findById(id);
    }

    public List<Libro> buscarConFiltros(String titulo, String autor, String categoria) {
        return libroDAO.buscar(titulo, autor, categoria);
    }

    public List<Libro> buscarPorKeywordYCategoria(String keyword, Long categoriaId) {
        return libroDAO.buscarPorKeywordYCategoria(keyword, categoriaId);
    }

    public List<Libro> buscarPorKeywordYCategoriaAdmin(String keyword, Long categoriaId) {
        return libroDAO.buscarPorKeywordYCategoriaAdmin(keyword, categoriaId);
    }

    public List<Libro> listarDisponibles() {
        List<Libro> todos = libroDAO.findAll();
        todos.removeIf(libro -> !isDisponible(libro));
        return todos;
    }

    public boolean isDisponible(Libro libro) {
        long activos = prestamoDAO.countByLibroIdAndEstadoActivoVencido(libro.getId());
        return activos < libro.getStockTotal();
    }

    public long stockDisponible(Libro libro) {
        long activos = prestamoDAO.countByLibroIdAndEstadoActivoVencido(libro.getId());
        return Math.max(0, libro.getStockTotal() - activos);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.findAll();
    }

    public List<Libro> listarMejorPuntuados(int limite) {
        return libroDAO.findTopRated(limite);
    }

    public Libro crear(Libro libro) {
        if (libro.getStockTotal() <= 0) {
            throw new IllegalArgumentException("El stock total debe ser mayor a 0.");
        }
        return libroDAO.save(libro);
    }

    public Libro actualizar(Libro libro) {
        return libroDAO.save(libro);
    }

    public void eliminar(Long id) {
        Libro libro = libroDAO.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));
        long activos = prestamoDAO.countByLibroIdAndEstadoActivoVencido(id);
        if (activos > 0) {
            throw new IllegalStateException("No se puede eliminar: hay " + activos + " prestamos activos.");
        }
        libroDAO.delete(libro);
    }

    public void alternarActivo(Long id) {
        Libro libro = libroDAO.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));
        libro.setActivo(!libro.isActivo());
        libroDAO.save(libro);
    }
}
