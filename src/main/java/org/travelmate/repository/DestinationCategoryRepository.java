package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.travelmate.model.DestinationCategory;

import java.util.*;

@ApplicationScoped
public class DestinationCategoryRepository {

    @PersistenceContext(unitName = "travelMatePU")
    private EntityManager em;

    public Optional<DestinationCategory> find(UUID id) {
        return Optional.ofNullable(em.find(DestinationCategory.class, id));
    }

    public List<DestinationCategory> findAll() {
        return em.createQuery("SELECT c FROM DestinationCategory c", DestinationCategory.class)
                .getResultList();
    }

    public void create(DestinationCategory entity) {
        em.persist(entity);
    }

    public void delete(UUID id) {
        DestinationCategory entity = em.find(DestinationCategory.class, id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    public void update(DestinationCategory entity) {
        em.merge(entity);
    }
}