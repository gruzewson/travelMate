package org.travelmate.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.travelmate.config.Logged;
import org.travelmate.model.Trip;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.repository.TripRepository;

import java.time.LocalDate;
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

    public List<Trip> findByCategoryIdAndUserId(UUID categoryId, UUID userId) {
        return tripRepository.findByCategoryIdAndUserId(categoryId, userId);
    }

    public List<Trip> findByUserId(UUID userId) {
        return tripRepository.findByUserId(userId);
    }

    /**
     * Find trips with dynamic filtering using Criteria API.
     * All filters are optional and combined with AND operator.
     */
    public List<Trip> findByFilters(UUID categoryId, UUID userId, String title,
                                     LocalDate startDateFrom, LocalDate startDateTo,
                                     LocalDate endDateFrom, LocalDate endDateTo,
                                     Double minCost, Double maxCost, TripStatus status) {
        return tripRepository.findByFilters(categoryId, userId, title,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                minCost, maxCost, status);
    }

    @Logged
    public void create(Trip entity) {
        tripRepository.create(entity);
    }

    @Logged
    public void delete(UUID id) {
        tripRepository.delete(id);
    }

    @Logged
    public void deleteByCategoryId(UUID categoryId) {
        findByCategoryId(categoryId).forEach(trip -> tripRepository.delete(trip.getId()));
    }

    @Logged
    public void update(Trip entity) {
        tripRepository.update(entity);
    }
}