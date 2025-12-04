package org.travelmate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to check if the trip's end date is after or equal to start date.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface ValidDateRange {
    
    String message() default "End date must be after or equal to start date";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}

