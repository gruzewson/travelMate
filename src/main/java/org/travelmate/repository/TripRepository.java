package org.travelmate.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.travelmate.model.Trip;
import org.travelmate.model.enums.TripStatus;

import java.time.LocalDate;
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

    /**
     * Dynamic filtering method using Criteria API.
     * All filters are optional and combined with AND operator.
     * If no filters are provided, returns all trips for the given category.
     */
    public List<Trip> findByFilters(UUID categoryId, UUID userId, String title,
                                     LocalDate startDateFrom, LocalDate startDateTo,
                                     LocalDate endDateFrom, LocalDate endDateTo,
                                     Double minCost, Double maxCost, TripStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> trip = cq.from(Trip.class);

        List<Predicate> predicates = new ArrayList<>();

        // Required: category filter
        if (categoryId != null) {
            predicates.add(cb.equal(trip.get("category").get("id"), categoryId));
        }

        // Required for non-admin: user filter
        if (userId != null) {
            predicates.add(cb.equal(trip.get("user").get("id"), userId));
        }

        // Optional: title filter (case-insensitive LIKE)
        if (title != null && !title.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(trip.get("title")), "%" + title.toLowerCase() + "%"));
        }

        // Optional: start date range filter
        if (startDateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(trip.get("startDate"), startDateFrom));
        }
        if (startDateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(trip.get("startDate"), startDateTo));
        }

        // Optional: end date range filter
        if (endDateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(trip.get("endDate"), endDateFrom));
        }
        if (endDateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(trip.get("endDate"), endDateTo));
        }

        // Optional: cost range filter
        if (minCost != null) {
            predicates.add(cb.greaterThanOrEqualTo(trip.get("estimatedCost"), minCost));
        }
        if (maxCost != null) {
            predicates.add(cb.lessThanOrEqualTo(trip.get("estimatedCost"), maxCost));
        }

        // Optional: status filter
        if (status != null) {
            predicates.add(cb.equal(trip.get("status"), status));
        }

        // Combine all predicates with AND
        if (!predicates.isEmpty()) {
            cq.select(trip).where(cb.and(predicates.toArray(new Predicate[0])));
        } else {
            cq.select(trip);
        }

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
