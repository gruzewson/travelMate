package org.travelmate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.travelmate.model.Trip;

/**
 * Validator that checks if the trip's end date is after or equal to start date.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Trip> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Trip trip, ConstraintValidatorContext context) {
        // If either date is null, let @NotNull handle it
        if (trip == null || trip.getStartDate() == null || trip.getEndDate() == null) {
            return true;
        }

        // Check if end date is after or equal to start date
        return !trip.getEndDate().isBefore(trip.getStartDate());
    }
}

