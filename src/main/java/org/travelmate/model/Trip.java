package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.validation.ValidDateRange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange
@Entity
@Table(name = "trip")
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

    @Column(nullable = false)
    @NotBlank(message = "Trip title is required")
    @Size(min = 3, max = 100, message = "Trip title must be between 3 and 100 characters")
    private String title;

    @Column(name = "start_date")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Column(name = "estimated_cost")
    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("1000000.00")
    private BigDecimal estimatedCost;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull(message = "Trip status is required")
    private TripStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonbTransient
    @ToString.Exclude
    private DestinationCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonbTransient
    @ToString.Exclude
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
}

