package org.travelmate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TripDatesValidator.class)
@Documented
public @interface ValidTripDates {
    String message() default "{trip.validation.dates.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

