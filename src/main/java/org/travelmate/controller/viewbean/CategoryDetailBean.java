package org.travelmate.controller.viewbean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
import org.travelmate.model.User;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class CategoryDetailBean implements Serializable {

    @Inject
    private DestinationCategoryService categoryService;

    @Inject
    private TripService tripService;

    @Inject
    private AuthBean authBean;

    @Getter
    @Setter
    private UUID categoryId;
    @Getter
    private DestinationCategory category;
    @Getter
    private List<Trip> trips;

    public void init() {
        if (categoryId != null) {
            category = categoryService.find(categoryId).orElse(null);

            if (category == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    String message = "Category with ID " + categoryId + " does not exist.";
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 404 error", e);
                }
                return;
            }

            // Filter trips based on user role
            User currentUser = authBean.getCurrentUser();
            if (authBean.isAdmin()) {
                // Admin sees all trips in category
                trips = tripService.findAll().stream()
                        .filter(trip -> trip.getCategory() != null &&
                                trip.getCategory().getId().equals(categoryId))
                        .toList();
            } else {
                // Regular user sees only their own trips
                trips = tripService.findAll().stream()
                        .filter(trip -> trip.getCategory() != null &&
                                trip.getCategory().getId().equals(categoryId) &&
                                trip.getUser() != null &&
                                currentUser != null &&
                                trip.getUser().getId().equals(currentUser.getId()))
                        .toList();
            }
        }
    }

    public void refreshTrips() {
        if (categoryId != null) {
            User currentUser = authBean.getCurrentUser();
            if (authBean.isAdmin()) {
                trips = tripService.findAll().stream()
                        .filter(trip -> trip.getCategory() != null &&
                                trip.getCategory().getId().equals(categoryId))
                        .toList();
            } else {
                trips = tripService.findAll().stream()
                        .filter(trip -> trip.getCategory() != null &&
                                trip.getCategory().getId().equals(categoryId) &&
                                trip.getUser() != null &&
                                currentUser != null &&
                                trip.getUser().getId().equals(currentUser.getId()))
                        .toList();
            }
        }
    }

    public void deleteTrip(UUID tripId) {
        tripService.delete(tripId);
        refreshTrips();
    }
}