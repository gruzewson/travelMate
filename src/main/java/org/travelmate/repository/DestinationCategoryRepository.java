package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.travelmate.model.DestinationCategory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DestinationCategoryRepository {
    private final Map<UUID, DestinationCategory> categories = new ConcurrentHashMap<>();

    public Optional<DestinationCategory> find(UUID id) {
        return Optional.ofNullable(categories.get(id));
    }

    public List<DestinationCategory> findAll() {
        return new ArrayList<>(categories.values());
    }

    public void create(DestinationCategory entity) {
        categories.put(entity.getId(), entity);
    }

    public void delete(DestinationCategory entity) {
        categories.remove(entity.getId());
    }

    public void update(DestinationCategory entity) {
        categories.put(entity.getId(), entity);
    }
}