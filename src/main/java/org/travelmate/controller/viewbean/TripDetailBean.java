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
import org.travelmate.service.TripService;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Named
@ViewScoped
public class TripDetailBean implements Serializable {

    @Inject
    private TripService tripService;

    @Getter
    private Trip trip;

    @Getter
    @Setter
    private UUID tripId;

    public void init() {
        if (tripId != null) {
            trip = tripService.find(tripId).orElse(null);

            if (trip == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 404 error", e);
                }
            }
        }
    }

    public DestinationCategory getCategory() {
        if (trip != null && trip.getCategory() != null) {
            return trip.getCategory();
        }
        return null;
    }
}
