package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.*;
import org.travelmate.model.enums.TripStatus;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip")
public class Trip {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "estimated_cost")
    private double estimatedCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TripStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonbTransient  // Nie serializuj całego obiektu category
    private DestinationCategory category;

    // Getter dla categoryId do serializacji JSON
    public UUID getCategoryId() {
        return category != null ? category.getId() : null;
    }
}
