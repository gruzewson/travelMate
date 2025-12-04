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
import org.travelmate.model.enums.TripStatus;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@ViewScoped
public class TripEditBean implements Serializable {

    @Inject
    private TripService tripService;

    @Inject
    private DestinationCategoryService categoryService;

    @Inject
    private AuthBean authBean;

    @Setter
    @Getter
    private Trip trip;

    @Setter
    @Getter
    private String categoryId;

    @Getter
    private boolean editMode;

    @Setter
    @Getter
    private UUID tripId;

    @Getter
    @Setter
    private boolean optimisticLockError = false;

    @Getter
    @Setter
    private Trip currentDbTrip;

    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String categoryParam = params.get("categoryId");

        if (categoryParam != null) {
            categoryId = categoryParam;
        }

        if (tripId != null) {
            editMode = true;
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

            User currentUser = authBean.getCurrentUser();
            boolean isOwner = trip.getUser() != null && currentUser != null &&
                            trip.getUser().getId().equals(currentUser.getId());

            if (!authBean.isAdmin() && !isOwner) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                     "You are not authorized to edit this trip.");
                    context.responseComplete();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send 403 error", e);
                }
                return;
            }

            if (trip.getCategory() != null) {
                categoryId = trip.getCategory().getId().toString();
            }
        } else {
            editMode = false;
            trip = new Trip();
            trip.setStatus(TripStatus.PLANNED);
            trip.setUser(authBean.getCurrentUser());
        }
    }

    public String save() {
        try {
            optimisticLockError = false;
            currentDbTrip = null;

            // Manual Bean Validation - validate the entire Trip object including class-level constraints
            jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            java.util.Set<jakarta.validation.ConstraintViolation<Trip>> violations = validator.validate(trip);

            if (!violations.isEmpty()) {
                // Add validation errors to FacesContext
                for (jakarta.validation.ConstraintViolation<Trip> violation : violations) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, violation.getMessage(), null));
                }
                return null; // Stay on the same page
            }

            if (categoryId != null && !categoryId.isEmpty()) {
                UUID catId = UUID.fromString(categoryId);
                categoryService.find(catId).ifPresent(trip::setCategory);
            }

            if (trip.getUser() == null) {
                trip.setUser(authBean.getCurrentUser());
            }

            if (editMode) {
                tripService.update(trip);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Trip has been updated", null));
            } else {
                tripService.create(trip);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Trip has been created", null));
            }

            return "/pages/category/category-view?faces-redirect=true&id=" + categoryId;
        } catch (jakarta.persistence.OptimisticLockException e) {
            optimisticLockError = true;
            currentDbTrip = tripService.find(trip.getId()).orElse(null);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "The trip was modified by another user. Please review the current data below.", null));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while saving: " + e.getMessage(), null));
            return null;
        }
    }

    public List<DestinationCategory> getAllCategories() {
        return categoryService.findAll();
    }

    public TripStatus[] getTripStatuses() {
        return TripStatus.values();
    }
}

