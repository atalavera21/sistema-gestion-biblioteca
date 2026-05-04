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

    public Prestamo save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            em.persist(prestamo);
            return prestamo;
        }
        return em.merge(prestamo);
    }
}
