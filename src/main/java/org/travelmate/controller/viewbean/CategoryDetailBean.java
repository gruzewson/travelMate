package org.travelmate.controller.viewbean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
import org.travelmate.model.User;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
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

    // Filter fields for dynamic filtering (Task 4)
    @Getter @Setter
    private String filterTitle;

    @Getter @Setter
    private LocalDate filterStartDateFrom;

    @Getter @Setter
    private LocalDate filterStartDateTo;

    @Getter @Setter
    private LocalDate filterEndDateFrom;

    @Getter @Setter
    private LocalDate filterEndDateTo;

    @Getter @Setter
    private Double filterMinCost;

    @Getter @Setter
    private Double filterMaxCost;

    @Getter @Setter
    private TripStatus filterStatus;

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

            loadTrips();
        }
    }

    /**
     * Load trips using dynamic Criteria API filtering.
     */
    private void loadTrips() {
        User currentUser = authBean.getCurrentUser();
        UUID userId = null;

        if (!authBean.isAdmin() && currentUser != null) {
            userId = currentUser.getId();
        }

        trips = tripService.findByFilters(
                categoryId,
                userId,
                filterTitle,
                filterStartDateFrom,
                filterStartDateTo,
                filterEndDateFrom,
                filterEndDateTo,
                filterMinCost,
                filterMaxCost,
                filterStatus
        );
    }

    public void applyFilters() {
        loadTrips();
    }

    public void clearFilters() {
        filterTitle = null;
        filterStartDateFrom = null;
        filterStartDateTo = null;
        filterEndDateFrom = null;
        filterEndDateTo = null;
        filterMinCost = null;
        filterMaxCost = null;
        filterStatus = null;
        loadTrips();
    }

    public void refreshTrips() {
        loadTrips();
    }

    public void deleteTrip(UUID tripId) {
        tripService.delete(tripId);
        loadTrips();
    }

    public TripStatus[] getTripStatuses() {
        return TripStatus.values();
    }
}
