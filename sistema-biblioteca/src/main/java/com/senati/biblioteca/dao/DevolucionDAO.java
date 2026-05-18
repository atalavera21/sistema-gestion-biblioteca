package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.Devolucion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Stateless
public class DevolucionDAO {

    @PersistenceContext
    private EntityManager em;

    public Devolucion guardar(Devolucion devolucion) {
        if (devolucion.getId() == null) {
            em.persist(devolucion);
            return devolucion;
        }
        return em.merge(devolucion);
    }

    public List<Devolucion> listarTodas() {
        return em.createQuery(
            "SELECT d FROM Devolucion d JOIN FETCH d.prestamo JOIN FETCH d.prestamo.libro JOIN FETCH d.prestamo.usuario ORDER BY d.fechaDevolucion DESC",
            Devolucion.class)
            .getResultList();
    }

    public List<Devolucion> listarConFiltros(String keyword, Long categoriaId,
                                              Date fechaInicio, Date fechaFin,
                                              boolean soloRetraso) {
        StringBuilder jpql = new StringBuilder(
            "SELECT d FROM Devolucion d JOIN FETCH d.prestamo JOIN FETCH d.prestamo.libro JOIN FETCH d.prestamo.usuario WHERE 1=1");

        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (LOWER(d.prestamo.libro.titulo) LIKE :kw OR LOWER(d.prestamo.libro.autor) LIKE :kw OR LOWER(d.prestamo.usuario.nombre) LIKE :kw OR LOWER(d.prestamo.usuario.codigoUniversitario) LIKE :kw)");
        }
        if (categoriaId != null) {
            jpql.append(" AND d.prestamo.libro.categoria.id = :catId");
        }
        if (fechaInicio != null) {
            jpql.append(" AND d.fechaDevolucion >= :inicio");
        }
        if (fechaFin != null) {
            jpql.append(" AND d.fechaDevolucion <= :fin");
        }
        if (soloRetraso) {
            jpql.append(" AND d.aTiempo = false");
        }

        jpql.append(" ORDER BY d.fechaDevolucion DESC");

        TypedQuery<Devolucion> query = em.createQuery(jpql.toString(), Devolucion.class);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
        }
        if (categoriaId != null) {
            query.setParameter("catId", categoriaId);
        }
        if (fechaInicio != null) {
            query.setParameter("inicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.setParameter("fin", fechaFin);
        }

        return query.getResultList();
    }

    public List<Object[]> findMorosidadPorUsuario(int limite) {
        return em.createQuery(
            "SELECT u.nombre, u.codigoUniversitario, COUNT(d), AVG(d.diasRetraso), SUM(d.diasRetraso), u.penalizado " +
            "FROM Devolucion d JOIN d.prestamo p JOIN p.usuario u " +
            "WHERE d.aTiempo = false " +
            "GROUP BY u.id, u.nombre, u.codigoUniversitario, u.penalizado " +
            "ORDER BY COUNT(d) DESC",
            Object[].class)
            .setMaxResults(limite)
            .getResultList();
    }
}
