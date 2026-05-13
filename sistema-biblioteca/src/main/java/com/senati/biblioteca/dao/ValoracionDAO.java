package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.Libro;
import com.senati.biblioteca.modelo.Usuario;
import com.senati.biblioteca.modelo.Valoracion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class ValoracionDAO {

    @PersistenceContext
    private EntityManager em;

    public Valoracion save(Valoracion valoracion) {
        if (valoracion.getId() == null) {
            em.persist(valoracion);
            return valoracion;
        }
        return em.merge(valoracion);
    }

    public Valoracion save(Long libroId, Long usuarioId, int puntuacion) {
        Valoracion v = new Valoracion();
        v.setLibro(em.getReference(Libro.class, libroId));
        v.setUsuario(em.getReference(Usuario.class, usuarioId));
        v.setPuntuacion(puntuacion);
        v.setFecha(new Date());
        em.persist(v);
        return v;
    }

    public Optional<Valoracion> findByLibroAndUsuario(Long libroId, Long usuarioId) {
        try {
            return Optional.of(em.createQuery(
                "SELECT v FROM Valoracion v WHERE v.libro.id = :libroId AND v.usuario.id = :usuarioId",
                Valoracion.class)
                .setParameter("libroId", libroId)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Double calcularPromedio(Long libroId) {
        try {
            return em.createQuery(
                "SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.libro.id = :libroId",
                Double.class)
                .setParameter("libroId", libroId)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer contarPorLibro(Long libroId) {
        return em.createQuery(
            "SELECT COUNT(v) FROM Valoracion v WHERE v.libro.id = :libroId",
            Long.class)
            .setParameter("libroId", libroId)
            .getSingleResult().intValue();
    }

    public List<Valoracion> findUltimasPorLibro(Long libroId, int limite) {
        return em.createQuery(
            "SELECT v FROM Valoracion v WHERE v.libro.id = :libroId ORDER BY v.fecha DESC",
            Valoracion.class)
            .setParameter("libroId", libroId)
            .setMaxResults(limite)
            .getResultList();
    }
}
