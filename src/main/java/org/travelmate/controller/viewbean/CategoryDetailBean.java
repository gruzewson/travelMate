package org.travelmate.controller.viewbean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
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
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 404 error", e);
                }
                return;
            }

            trips = tripService.findAll().stream()
                    .filter(trip -> trip.getCategoryId() != null &&
                            trip.getCategoryId().equals(categoryId))
                    .toList();
        }
    }
}
