package org.travelmate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.travelmate.model.Trip;
import org.travelmate.repository.TripRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TripService {

    @Inject
    private TripRepository TripRepository;

    public Optional<Trip> find(UUID id) {
        return TripRepository.find(id);
    }

    public List<Trip> findAll() {
        return TripRepository.findAll();
    }

    public void create(Trip entity) {
        TripRepository.create(entity);
    }

    public void delete(Trip entity) {
        TripRepository.delete(entity);
    }

    public void update(Trip entity) {
        TripRepository.update(entity);
    }
}