package org.travelmate.model;

import lombok.Data;
import org.travelmate.model.enums.TripStatus;

import java.time.LocalDate;

@Data
public class Trip {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private double estimatedCost;

    private TripStatus status;
    private DestinationCategory category;
    private User user;

}
