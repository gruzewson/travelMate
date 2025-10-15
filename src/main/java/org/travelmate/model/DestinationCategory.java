package org.travelmate.model;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationCategory {
    private UUID id;
    private String name;
    private String description;

    @ToString.Exclude
    private List<Trip> trips;
}
