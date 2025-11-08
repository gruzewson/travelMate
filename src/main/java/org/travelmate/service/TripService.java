package org.travelmate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.travelmate.model.Trip;
import org.travelmate.repository.TripRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TripService {

    @Inject
    private TripRepository tripRepository;

    public Optional<Trip> find(UUID id) {
        return tripRepository.find(id);
    }

    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public List<Trip> findByCategoryId(UUID categoryId) {
        return tripRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public void create(Trip entity) {
        tripRepository.create(entity);
    }

    @Transactional
    public void delete(UUID id) {
        tripRepository.delete(id);
    }

    @Transactional
    public void deleteByCategoryId(UUID categoryId) {
        findByCategoryId(categoryId).forEach(trip -> tripRepository.delete(trip.getId()));
    }

    @Transactional
    public void update(Trip entity) {
        tripRepository.update(entity);
    }
}