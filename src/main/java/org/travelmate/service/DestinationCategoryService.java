package org.travelmate.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.travelmate.model.DestinationCategory;
import org.travelmate.repository.DestinationCategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DestinationCategoryService {

    @Inject
    private DestinationCategoryRepository repository;

    @Inject
    private TripService tripService;

    public Optional<DestinationCategory> find(UUID id) {
        return repository.find(id);
    }

    public List<DestinationCategory> findAll() {
        return repository.findAll();
    }

    public void create(DestinationCategory entity) {
        repository.create(entity);
    }

    public void delete(UUID categoryId) {
        tripService.deleteByCategoryId(categoryId);
        repository.delete(categoryId);
    }

    public void update(DestinationCategory entity) {
        repository.update(entity);
    }
}