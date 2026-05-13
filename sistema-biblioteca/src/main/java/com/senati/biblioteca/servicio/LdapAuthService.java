package com.senati.biblioteca.servicio;

import com.senati.biblioteca.modelo.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class LdapAuthService implements AuthService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean autenticar(String username, String password) {
        try {
            Usuario usuario = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.codigoUniversitario = :username AND u.activo = true",
                Usuario.class)
                .setParameter("username", username)
                .getSingleResult();

            return BCrypt.checkpw(password, usuario.getPassword());
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public String obtenerRol(String username) {
        try {
            return em.createQuery(
                "SELECT u.rol FROM Usuario u WHERE u.codigoUniversitario = :username AND u.activo = true",
                String.class)
                .setParameter("username", username)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
