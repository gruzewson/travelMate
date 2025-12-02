package org.travelmate.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.travelmate.model.DestinationCategory;

import java.util.*;

@Stateless
public class DestinationCategoryRepository {

    @PersistenceContext(unitName = "travelMatePU")
    private EntityManager em;

    public Optional<DestinationCategory> find(UUID id) {
        return Optional.ofNullable(em.find(DestinationCategory.class, id));
    }

    public List<DestinationCategory> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DestinationCategory> cq = cb.createQuery(DestinationCategory.class);
        Root<DestinationCategory> category = cq.from(DestinationCategory.class);
        cq.select(category);
        return em.createQuery(cq).getResultList();
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