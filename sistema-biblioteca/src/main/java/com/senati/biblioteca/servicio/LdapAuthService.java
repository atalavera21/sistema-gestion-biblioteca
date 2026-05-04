package com.senati.biblioteca.servicio;

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
            Object[] result = (Object[]) em.createNativeQuery(
                "SELECT password, activo FROM ldap_users WHERE username = ?")
                .setParameter(1, username)
                .getSingleResult();

            String hash = (String) result[0];

            Object activoVal = result[1];
            boolean activo;
            if (activoVal instanceof Boolean) {
                activo = (Boolean) activoVal;
            } else {
                activo = activoVal != null && ((Number) activoVal).intValue() != 0;
            }

            if (!activo) return false;
            return BCrypt.checkpw(password, hash);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String obtenerRol(String username) {
        try {
            return (String) em.createNativeQuery(
                "SELECT rol FROM ldap_users WHERE username = ? AND activo = true")
                .setParameter(1, username)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
