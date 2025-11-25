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
import org.travelmate.model.User;
import org.travelmate.service.TripService;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Named
@ViewScoped
public class TripDetailBean implements Serializable {

    @Inject
    private TripService tripService;

    @Inject
    private AuthBean authBean;

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
                    String message = "Trip with ID " + tripId + " does not exist.";
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 404 error", e);
                }
                return;
            }

            // Authorization check - only owner or admin can view
            User currentUser = authBean.getCurrentUser();
            boolean isOwner = trip.getUser() != null && currentUser != null &&
                            trip.getUser().getId().equals(currentUser.getId());

            if (!authBean.isAdmin() && !isOwner) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                     "You are not authorized to view this trip.");
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 403 error", e);
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