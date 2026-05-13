package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.Libro;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class LibroDAO {

    @PersistenceContext
    private EntityManager em;

    public List<Libro> findAll() {
        List<Libro> libros = em.createQuery("SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
                 .setMaxResults(200)
                 .getResultList();
        cargarPuntuaciones(libros);
        return libros;
    }

    public List<Libro> findAllAdmin() {
        List<Libro> libros = em.createQuery("SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
                 .getResultList();
        cargarPuntuaciones(libros);
        return libros;
    }

    public long count() {
        return em.createQuery("SELECT COUNT(l) FROM Libro l", Long.class)
                 .getSingleResult();
    }

    public Optional<Libro> findById(Long id) {
        Optional<Libro> libro = Optional.ofNullable(em.find(Libro.class, id));
        libro.ifPresent(l -> {
            l.setPuntuacion(calcularPromedio(l.getId()));
            l.setTotalValoraciones(contarValoraciones(l.getId()));
        });
        return libro;
    }

    public List<Libro> buscarPorKeywordYCategoria(String keyword, Long categoriaId) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM Libro l WHERE 1=1");
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (LOWER(l.titulo) LIKE LOWER(:kw) OR LOWER(l.autor) LIKE LOWER(:kw))");
        }
        if (categoriaId != null) {
            jpql.append(" AND l.categoria.id = :catId");
        }
        jpql.append(" ORDER BY l.titulo");

        TypedQuery<Libro> query = em.createQuery(jpql.toString(), Libro.class);
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
        if (categoriaId != null) {
            query.setParameter("catId", categoriaId);
        }

        List<Libro> libros = query.setMaxResults(200).getResultList();
        cargarPuntuaciones(libros);
        return libros;
    }

    public List<Libro> buscarPorKeywordYCategoriaAdmin(String keyword, Long categoriaId) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM Libro l WHERE 1=1");
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (LOWER(l.titulo) LIKE LOWER(:kw) OR LOWER(l.autor) LIKE LOWER(:kw))");
        }
        if (categoriaId != null) {
            jpql.append(" AND l.categoria.id = :catId");
        }
        jpql.append(" ORDER BY l.titulo");

        TypedQuery<Libro> query = em.createQuery(jpql.toString(), Libro.class);
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
        if (categoriaId != null) {
            query.setParameter("catId", categoriaId);
        }

        List<Libro> libros = query.getResultList();
        cargarPuntuaciones(libros);
        return libros;
    }

    public List<Libro> buscar(String titulo, String autor, String categoria) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM Libro l WHERE 1=1");
        if (titulo != null && !titulo.isBlank())
            jpql.append(" AND LOWER(l.titulo) LIKE LOWER(:titulo)");
        if (autor != null && !autor.isBlank())
            jpql.append(" AND LOWER(l.autor) LIKE LOWER(:autor)");
        if (categoria != null && !categoria.isBlank())
            jpql.append(" AND LOWER(l.categoria.nombre) = LOWER(:categoria)");
        jpql.append(" ORDER BY l.titulo");

        TypedQuery<Libro> query = em.createQuery(jpql.toString(), Libro.class);
        if (titulo != null && !titulo.isBlank())
            query.setParameter("titulo", "%" + titulo + "%");
        if (autor != null && !autor.isBlank())
            query.setParameter("autor", "%" + autor + "%");
        if (categoria != null && !categoria.isBlank())
            query.setParameter("categoria", categoria);

        List<Libro> libros = query.setMaxResults(200).getResultList();
        cargarPuntuaciones(libros);
        return libros;
    }

    public long countPrestamosActivos(Long libroId) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Prestamo p WHERE p.libro.id = :libroId AND p.activo = true",
            Long.class)
            .setParameter("libroId", libroId)
            .getSingleResult();
    }

    public Libro save(Libro libro) {
        if (libro.getId() == null) {
            em.persist(libro);
            return libro;
        }
        return em.merge(libro);
    }

    public void delete(Libro libro) {
        em.remove(em.contains(libro) ? libro : em.merge(libro));
    }

    public List<Libro> findTopRated(int limite) {
        List<Libro> libros = em.createQuery(
            "SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
            .setMaxResults(200)
            .getResultList();
        cargarPuntuaciones(libros);
        libros.sort((a, b) -> {
            double pa = a.getPuntuacion() != null ? a.getPuntuacion() : 0;
            double pb = b.getPuntuacion() != null ? b.getPuntuacion() : 0;
            return Double.compare(pb, pa);
        });
        if (libros.size() > limite) {
            return libros.subList(0, limite);
        }
        return libros;
    }

    private void cargarPuntuaciones(List<Libro> libros) {
        for (Libro libro : libros) {
            libro.setPuntuacion(calcularPromedio(libro.getId()));
            libro.setTotalValoraciones(contarValoraciones(libro.getId()));
        }
    }

    private Double calcularPromedio(Long libroId) {
        try {
            return em.createQuery(
                "SELECT AVG(CAST(v.puntuacion AS double)) FROM Valoracion v WHERE v.libro.id = :libroId",
                Double.class)
                .setParameter("libroId", libroId)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer contarValoraciones(Long libroId) {
        try {
            return em.createQuery(
                "SELECT COUNT(v) FROM Valoracion v WHERE v.libro.id = :libroId",
                Long.class)
                .setParameter("libroId", libroId)
                .getSingleResult().intValue();
        } catch (Exception e) {
            return 0;
        }
    }
}

