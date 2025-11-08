package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.travelmate.model.Trip;
import java.util.*;

@ApplicationScoped
public class TripRepository {

    @PersistenceContext(unitName = "travelMatePU")
    private EntityManager em;

    public Optional<Trip> find(UUID id) {
        return Optional.ofNullable(em.find(Trip.class, id));
    }

    public List<Trip> findAll() {
        return em.createQuery("SELECT t FROM Trip t", Trip.class)
                .getResultList();
    }

    public List<Trip> findByCategoryId(UUID categoryId) {
        return em.createQuery(
                "SELECT t FROM Trip t WHERE t.category.id = :categoryId", Trip.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public void create(Trip entity) {
        em.persist(entity);
    }

    public void delete(UUID id) {
        Trip entity = em.find(Trip.class, id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    public void update(Trip entity) {
        em.merge(entity);
    }
}
