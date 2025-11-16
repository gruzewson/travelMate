package org.travelmate.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.travelmate.model.Trip;
import org.travelmate.repository.TripRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
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

    public List<Trip> findByUserId(UUID userId) {
        return tripRepository.findByUserId(userId);
    }

    public void create(Trip entity) {
        tripRepository.create(entity);
    }

    public void delete(UUID id) {
        tripRepository.delete(id);
    }

    public void deleteByCategoryId(UUID categoryId) {
        findByCategoryId(categoryId).forEach(trip -> tripRepository.delete(trip.getId()));
    }

    public void update(Trip entity) {
        tripRepository.update(entity);
    }
}