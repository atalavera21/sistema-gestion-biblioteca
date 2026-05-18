package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class UsuarioDAO {

    @PersistenceContext
    private EntityManager em;

    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public Optional<Usuario> findByCodigoUniversitario(String codigo) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.codigoUniversitario = :codigo", Usuario.class)
                 .setParameter("codigo", codigo)
                 .getResultStream()
                 .findFirst();
    }

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombre", Usuario.class)
                 .getResultList();
    }

    public List<Usuario> findEstudiantes() {
        return em.createQuery(
            "SELECT u FROM Usuario u WHERE u.rol = 'ESTUDIANTE' ORDER BY u.nombre", Usuario.class)
                 .getResultList();
    }

    public List<Usuario> findPenalizados() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.penalizado = true", Usuario.class)
                 .getResultList();
    }

    public long countPrestamosActivos(Long usuarioId) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.activo = true",
            Long.class)
            .setParameter("usuarioId", usuarioId)
            .getSingleResult();
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
            return usuario;
        }
        return em.merge(usuario);
    }
}
