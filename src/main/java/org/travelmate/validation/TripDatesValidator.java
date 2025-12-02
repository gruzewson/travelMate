package org.travelmate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.travelmate.model.Trip;

public class TripDatesValidator implements ConstraintValidator<ValidTripDates, Trip> {

    @Override
    public void initialize(ValidTripDates constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Trip trip, ConstraintValidatorContext context) {
        if (trip == null) {
            return true;
        }

        // If either date is null, let @NotNull handle it
        if (trip.getStartDate() == null || trip.getEndDate() == null) {
            return true;
        }

        // Check if start date is before or equal to end date
        boolean isValid = !trip.getStartDate().isAfter(trip.getEndDate());

        if (!isValid) {
            // Customize the error message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
        }

        return isValid;
    }
}

