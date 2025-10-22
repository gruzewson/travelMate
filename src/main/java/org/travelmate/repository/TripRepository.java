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

    public void create(Trip entity) {
        Trips.put(entity.getId(), entity);
    }

    public void delete(Trip entity) {
        Trips.remove(entity.getId());
    }

    public void update(Trip entity) {
        Trips.put(entity.getId(), entity);
    }
}
