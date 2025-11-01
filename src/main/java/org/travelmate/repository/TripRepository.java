package org.travelmate.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.travelmate.model.Trip;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TripRepository {
    private final Map<UUID, Trip> Trips = new ConcurrentHashMap<>();

    public Optional<Trip> find(UUID id) {
        return Optional.ofNullable(Trips.get(id));
    }

    public List<Trip> findAll() {
        return new ArrayList<>(Trips.values());
    }

    public List<Trip> findByCategoryId(UUID categoryId) {
        return Trips.values().stream()
                .filter(t -> categoryId.equals(t.getCategoryId()))
                .toList();
    }

    public void create(Trip entity) {
        Trips.put(entity.getId(), entity);
    }

    public void delete(UUID id) {
        Trips.remove(id);
    }

    public void update(Trip entity) {
        Trips.put(entity.getId(), entity);
    }
}
