package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.EstadoPrestamo;
import com.senati.biblioteca.modelo.Prestamo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Stateless
public class PrestamoDAO {

    @PersistenceContext
    private EntityManager em;

    public Prestamo findById(Long id) {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.id = :id",
            Prestamo.class)
            .setParameter("id", id)
            .getSingleResult();
    }

    public long countByLibroIdAndEstadoActivoVencido(Long libroId) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Prestamo p WHERE p.libro.id = :libroId AND p.activo = true",
            Long.class)
            .setParameter("libroId", libroId)
            .getSingleResult();
    }

    public long countByUsuarioIdAndEstadoActivoVencido(Long usuarioId) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.activo = true",
            Long.class)
            .setParameter("usuarioId", usuarioId)
            .getSingleResult();
    }

    public List<Prestamo> findActivosByUsuario(Long usuarioId) {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro WHERE p.usuario.id = :usuarioId AND p.activo = true",
            Prestamo.class)
            .setParameter("usuarioId", usuarioId)
            .getResultList();
    }

    public List<Prestamo> findHistorialByUsuario(Long usuarioId) {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro WHERE p.usuario.id = :usuarioId ORDER BY p.fechaPrestamo DESC",
            Prestamo.class)
            .setParameter("usuarioId", usuarioId)
            .getResultList();
    }

    public List<Prestamo> findVencidos() {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'VENCIDO' ORDER BY p.fechaDevolucionEstimada",
            Prestamo.class)
            .getResultList();
    }

    public List<Prestamo> findProximosAVencer(Date limite) {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEstimada <= :limite",
            Prestamo.class)
            .setParameter("limite", limite)
            .getResultList();
    }

    public List<Prestamo> findByRangoFechas(Date inicio, Date fin) {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.fechaPrestamo BETWEEN :inicio AND :fin ORDER BY p.fechaPrestamo",
            Prestamo.class)
            .setParameter("inicio", inicio)
            .setParameter("fin", fin)
            .getResultList();
    }

    public List<Object[]> findLibrosMasPrestados(int limite) {
        return em.createQuery(
            "SELECT p.libro.titulo, COUNT(p) as total FROM Prestamo p GROUP BY p.libro.titulo ORDER BY total DESC",
            Object[].class)
            .setMaxResults(limite)
            .getResultList();
    }

    public List<Object[]> findAutoresMasPopulares(int limite) {
        return em.createQuery(
            "SELECT p.libro.autor, COUNT(p) as total FROM Prestamo p GROUP BY p.libro.autor ORDER BY total DESC",
            Object[].class)
            .setMaxResults(limite)
            .getResultList();
    }

    public List<Object[]> findLibrosPorCategoria(int limite) {
        return em.createQuery(
            "SELECT p.libro.categoria.nombre, COUNT(p) as total FROM Prestamo p GROUP BY p.libro.categoria.nombre ORDER BY total DESC",
            Object[].class)
            .setMaxResults(limite)
            .getResultList();
    }

    public List<Object[]> findLibrosMasPrestadosDetalle(int limite) {
        return em.createQuery(
            "SELECT l.titulo, l.autor, c.nombre, l.stockTotal, COUNT(p) AS total " +
            "FROM Prestamo p JOIN p.libro l JOIN l.categoria c " +
            "GROUP BY l.id, l.titulo, l.autor, c.nombre, l.stockTotal " +
            "ORDER BY total DESC",
            Object[].class)
            .setMaxResults(limite)
            .getResultList();
    }

    public List<Object[]> findPrestamosPorMes(int anio) {
        return em.createQuery(
            "SELECT MONTH(p.fechaPrestamo), COUNT(p) FROM Prestamo p " +
            "WHERE YEAR(p.fechaPrestamo) = :anio " +
            "GROUP BY MONTH(p.fechaPrestamo) ORDER BY MONTH(p.fechaPrestamo)",
            Object[].class)
            .setParameter("anio", anio)
            .getResultList();
    }

    public List<Integer> findAniosConPrestamos() {
        return em.createQuery(
            "SELECT DISTINCT YEAR(p.fechaPrestamo) FROM Prestamo p " +
            "ORDER BY YEAR(p.fechaPrestamo) DESC",
            Integer.class)
            .getResultList();
    }

    public List<Prestamo> findPrestamosPenalizados() {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'PENALIZADO' ORDER BY p.fechaDevolucionEstimada DESC",
            Prestamo.class)
            .getResultList();
    }

    public List<Prestamo> findAllActivos() {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'ACTIVO' ORDER BY p.fechaPrestamo DESC",
            Prestamo.class)
            .getResultList();
    }

    public List<Prestamo> searchActivos(String keyword, Long categoriaId, Long usuarioId) {
        StringBuilder jpql = new StringBuilder(
            "SELECT DISTINCT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'ACTIVO'");
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (LOWER(p.libro.titulo) LIKE :kw OR LOWER(p.libro.autor) LIKE :kw OR LOWER(p.usuario.nombre) LIKE :kw)");
        }
        if (categoriaId != null) {
            jpql.append(" AND p.libro.categoria.id = :catId");
        }
        if (usuarioId != null) {
            jpql.append(" AND p.usuario.id = :usuarioId");
        }
        jpql.append(" ORDER BY p.fechaPrestamo DESC");

        TypedQuery<Prestamo> query = em.createQuery(jpql.toString(), Prestamo.class);
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
        }
        if (categoriaId != null) {
            query.setParameter("catId", categoriaId);
        }
        if (usuarioId != null) {
            query.setParameter("usuarioId", usuarioId);
        }
        return query.getResultList();
    }

    public List<Prestamo> findDevolucionesPendientes() {
        return em.createQuery(
            "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario WHERE p.estado = 'VENCIDO' AND p.fechaDevolucionReal IS NULL ORDER BY p.fechaDevolucionEstimada ASC",
            Prestamo.class)
            .getResultList();
    }

    public Prestamo save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            em.persist(prestamo);
            return prestamo;
        }
        return em.merge(prestamo);
    }
}
