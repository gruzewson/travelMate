package org.travelmate.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
            User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    public void create(User entity) {
        em.persist(entity);
    }

    public void delete(UUID id) {
        User entity = em.find(User.class, id);
        if (entity != null) {
            em.createQuery("UPDATE Trip t SET t.user = NULL WHERE t.user.id = :userId")
                    .setParameter("userId", id)
                    .executeUpdate();

            em.remove(entity);
        }
    }

    public void update(User entity) {
        em.merge(entity);
    }
}