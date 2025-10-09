package org.travelmate.model;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class DestinationCategory {
    private UUID id;
    private String name;
    private String description;

    private List<Trip> trips;
}
