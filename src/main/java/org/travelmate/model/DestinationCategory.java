package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "destination_category")
public class DestinationCategory {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(
        mappedBy = "category",
        cascade = CascadeType.REMOVE,
        fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @JsonbTransient  // Nie serializuj listy trips w JSON
    private List<Trip> trips = new ArrayList<>();

    public DestinationCategory(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.trips = new ArrayList<>();
    }
}
