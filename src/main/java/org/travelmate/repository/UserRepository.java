package org.travelmate.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.travelmate.model.Trip;
import org.travelmate.model.User;
import java.util.*;

@Stateless
public class UserRepository {

    @PersistenceContext(unitName = "travelMatePU")
    private EntityManager em;

    public Optional<User> find(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByLogin(String login) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> user = cq.from(User.class);
            cq.select(user).where(cb.equal(user.get("login"), login));
            User result = em.createQuery(cq).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);
        cq.select(user);
        return em.createQuery(cq).getResultList();
    }

    public void create(User entity) {
        em.persist(entity);
    }

    public void delete(UUID id) {
        User entity = em.find(User.class, id);
        if (entity != null) {
            // Update trips using Criteria API
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Trip> cu = cb.createCriteriaUpdate(Trip.class);
            Root<Trip> trip = cu.from(Trip.class);
            cu.set(trip.<User>get("user"), (User) null);
            cu.where(cb.equal(trip.get("user").get("id"), id));
            em.createQuery(cu).executeUpdate();

            em.remove(entity);
        }
    }

    public void update(User entity) {
        em.merge(entity);
    }
}