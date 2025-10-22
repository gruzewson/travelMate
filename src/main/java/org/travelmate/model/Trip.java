package org.travelmate.model;

import lombok.*;
import org.travelmate.model.enums.TripStatus;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private UUID id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private double estimatedCost;
    private TripStatus status;

    private DestinationCategory category;
    private User user;

}
