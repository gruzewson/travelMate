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

    public List<DestinationCategory> getAllCategories() {
        return categoryService.findAll();
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

    public String deleteCategory(UUID categoryId) {
        categoryService.find(categoryId).ifPresent(category -> {
            categoryService.delete(categoryId);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Category has been deleted", null));
        });
        return "/pages/category/categories?faces-redirect=true";
    }

    public String deleteTrip(UUID tripId, UUID categoryId) {
        tripService.delete(tripId);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Trip has been deleted", null));
        return "/pages/category/category-view?faces-redirect=true&id=" + categoryId;
    }
}
