package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.validation.ValidTripDates;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip")
@ValidTripDates
public class Trip {

    @Id
    @GeneratedValue
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotBlank(message = "{trip.validation.title.notBlank}")
    @Size(min = 3, max = 100, message = "{trip.validation.title.size}")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "{trip.validation.startDate.notNull}")
    @FutureOrPresent(message = "{trip.validation.startDate.futureOrPresent}")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "{trip.validation.endDate.notNull}")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Min(value = 0, message = "{trip.validation.cost.min}")
    @Max(value = 1000000, message = "{trip.validation.cost.max}")
    @Column(name = "estimated_cost")
    private double estimatedCost;

    @NotNull(message = "{trip.validation.status.notNull}")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TripStatus status;

    @NotNull(message = "{trip.validation.category.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonbTransient
    private DestinationCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonbTransient
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }
}
