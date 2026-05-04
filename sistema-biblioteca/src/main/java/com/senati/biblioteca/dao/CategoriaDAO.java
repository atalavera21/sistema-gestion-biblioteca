package com.senati.biblioteca.dao;

import com.senati.biblioteca.modelo.Categoria;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class CategoriaDAO {

    @PersistenceContext
    private EntityManager em;

    public List<Categoria> findAll() {
        return em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                 .getResultList();
    }

    public Optional<Categoria> findById(Long id) {
        return Optional.ofNullable(em.find(Categoria.class, id));
    }

    public Optional<Categoria> findByNombre(String nombre) {
        return em.createQuery("SELECT c FROM Categoria c WHERE LOWER(c.nombre) = LOWER(:nombre)", Categoria.class)
                 .setParameter("nombre", nombre)
                 .getResultStream()
                 .findFirst();
    }

    public Categoria save(Categoria categoria) {
        if (categoria.getId() == null) {
            em.persist(categoria);
            return categoria;
        }
        return em.merge(categoria);
    }

    public void delete(Categoria categoria) {
        em.remove(em.contains(categoria) ? categoria : em.merge(categoria));
    }
}
