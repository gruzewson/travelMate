package org.travelmate.controller;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Component for initializing sample data and demonstrating service operations.
 * Runs automatically on application startup.
 */
@Singleton
@Startup
public class DataInitializer {

    @Inject
    private DestinationCategoryService categoryService;

    @Inject
    private TripService tripService;

    @PostConstruct
    public void init() {

        // Creating categories
        DestinationCategory mountains = createCategory(
                "Mountains",
                "Mountain expeditions - trekking, climbing, scenic views"
        );

        DestinationCategory beach = createCategory(
                "Beach",
                "Relaxing trips to the sea and ocean"
        );

        DestinationCategory cities = createCategory(
                "Cities",
                "City tours, culture, architecture, museums"
        );

        DestinationCategory adventure = createCategory(
                "Adventure",
                "Extreme sports, safari, diving"
        );

        createTrip("Tatra Mountains - Winter Trekking",
                LocalDate.of(2025, 12, 15),
                LocalDate.of(2025, 12, 20),
                1500.0, TripStatus.PLANNED, mountains);

        createTrip("Bieszczady - Mountain Meadows",
                LocalDate.of(2025, 6, 10),
                LocalDate.of(2025, 6, 17),
                1200.0, TripStatus.PLANNED, mountains);

        createTrip("Maldives - Paradise Island",
                LocalDate.of(2026, 1, 20),
                LocalDate.of(2026, 2, 3),
                8500.0, TripStatus.PLANNED, beach);

        createTrip("Sopot - Baltic Sea Weekend",
                LocalDate.of(2025, 7, 15),
                LocalDate.of(2025, 7, 18),
                600.0, TripStatus.PLANNED, beach);

        createTrip("Krakow - Capital of Culture",
                LocalDate.of(2025, 11, 1),
                LocalDate.of(2025, 11, 5),
                800.0, TripStatus.PLANNED, cities);

        createTrip("Barcelona - Gaudi and Tapas",
                LocalDate.of(2025, 9, 20),
                LocalDate.of(2025, 9, 27),
                3200.0, TripStatus.PLANNED, cities);

        createTrip("Safari in Tanzania",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 24),
                12000.0, TripStatus.PLANNED, adventure);

        createTrip("Diving in Egypt",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 12),
                4500.0, TripStatus.COMPLETED, adventure);
    }

    private DestinationCategory createCategory(String name, String description) {
        DestinationCategory category = new DestinationCategory();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setDescription(description);
        categoryService.create(category);
        return category;
    }

    private void createTrip(String title, LocalDate startDate, LocalDate endDate,
                           double cost, TripStatus status, DestinationCategory category) {
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setTitle(title);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setEstimatedCost(cost);
        trip.setStatus(status);
        trip.setCategoryId(category.getId());
        tripService.create(trip);
    }
}
