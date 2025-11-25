package org.travelmate.controller.viewbean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CategoryViewBean implements Serializable {

    @Inject
    private DestinationCategoryService categoryService;

    @Inject
    private TripService tripService;

    private List<DestinationCategory> cachedCategories;

    public List<DestinationCategory> getAllCategories() {
        if (cachedCategories == null) {
            cachedCategories = categoryService.findAll();
        }
        return cachedCategories;
    }

    public void refreshCategories() {
        cachedCategories = categoryService.findAll();
    }

    public Map<UUID, List<Trip>> getTripsByCategory() {
        return tripService.findAll().stream()
                .filter(trip -> trip.getCategory() != null)
                .collect(Collectors.groupingBy(trip -> trip.getCategory().getId()));
    }

    public int getTripCount(UUID categoryId) {
        return (int) tripService.findAll().stream()
                .filter(trip -> trip.getCategory() != null &&
                        trip.getCategory().getId().equals(categoryId))
                .count();
    }

    public void deleteCategory(UUID categoryId) {
        categoryService.find(categoryId).ifPresent(category -> {
            categoryService.delete(categoryId);
            refreshCategories();
        });
    }

    public void deleteTrip(UUID tripId) {
        tripService.delete(tripId);
    }
}