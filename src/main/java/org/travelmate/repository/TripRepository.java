package org.travelmate.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.travelmate.model.Trip;
import java.util.*;

@Stateless
public class TripRepository {

    @PersistenceContext(unitName = "travelMatePU")
    private EntityManager em;

    public Optional<Trip> find(UUID id) {
        return Optional.ofNullable(em.find(Trip.class, id));
    }

    public List<Trip> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> trip = cq.from(Trip.class);
        cq.select(trip);
        return em.createQuery(cq).getResultList();
    }

    public List<Trip> findByCategoryId(UUID categoryId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> trip = cq.from(Trip.class);
        cq.select(trip).where(cb.equal(trip.get("category").get("id"), categoryId));
        return em.createQuery(cq).getResultList();
    }

    public List<Trip> findByCategoryIdAndUserId(UUID categoryId, UUID userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> trip = cq.from(Trip.class);
        cq.select(trip).where(
            cb.and(
                cb.equal(trip.get("category").get("id"), categoryId),
                cb.equal(trip.get("user").get("id"), userId)
            )
        );
        return em.createQuery(cq).getResultList();
    }

    public List<Trip> findByUserId(UUID userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> trip = cq.from(Trip.class);
        cq.select(trip).where(cb.equal(trip.get("user").get("id"), userId));
        return em.createQuery(cq).getResultList();
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
