package org.travelmate.controller;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.travelmate.model.DestinationCategory;
import org.travelmate.model.Trip;
import org.travelmate.model.User;
import org.travelmate.model.enums.TripStatus;
import org.travelmate.model.enums.UserRole;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;
import org.travelmate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Component for initializing sample data and demonstrating service operations.
 * Runs automatically on application startup.
 */
@WebListener
public class DataInitializer implements ServletContextListener {

    private DestinationCategoryService categoryService;
    private TripService tripService;
    private UserService userService;
    private Pbkdf2PasswordHash passwordHash;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        categoryService = CDI.current().select(DestinationCategoryService.class).get();
        tripService = CDI.current().select(TripService.class).get();
        userService = CDI.current().select(UserService.class).get();
        passwordHash = CDI.current().select(Pbkdf2PasswordHash.class).get();

        // Initialize password hash with parameters matching SecurityConfig
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "210000");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "32");
        passwordHash.initialize(parameters);

        if (!categoryService.findAll().isEmpty()) {
            System.out.println("Data already initialized, skipping initialization...");
            return;
        }

        System.out.println("Initializing application data...");

        User admin = createUser("admin", "admin123", UserRole.ADMIN, LocalDate.of(1985, 1, 1));

        User john = createUser("john", "john123", UserRole.USER, LocalDate.of(1990, 5, 15));
        User andrew = createUser("andrew", "andrew123", UserRole.USER, LocalDate.of(2000, 6, 16));
        User mariusz = createUser("mariusz", "mariusz123", UserRole.USER, LocalDate.of(2010, 7, 17));
        User marcin = createUser("marcin", "marcin123", UserRole.USER, LocalDate.of(2020, 8, 18));

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
                1500.0, TripStatus.PLANNED, mountains, john);

        createTrip("Bieszczady - Mountain Meadows",
                LocalDate.of(2025, 6, 10),
                LocalDate.of(2025, 6, 17),
                1200.0, TripStatus.PLANNED, mountains, andrew);

        createTrip("Maldives - Paradise Island",
                LocalDate.of(2026, 1, 20),
                LocalDate.of(2026, 2, 3),
                8500.0, TripStatus.PLANNED, beach, john);

        createTrip("Sopot - Baltic Sea Weekend",
                LocalDate.of(2025, 7, 15),
                LocalDate.of(2025, 7, 18),
                600.0, TripStatus.PLANNED, beach, mariusz);

        createTrip("Krakow - Capital of Culture",
                LocalDate.of(2025, 11, 1),
                LocalDate.of(2025, 11, 5),
                800.0, TripStatus.PLANNED, cities, john);

        createTrip("Barcelona - Gaudi and Tapas",
                LocalDate.of(2025, 9, 20),
                LocalDate.of(2025, 9, 27),
                3200.0, TripStatus.PLANNED, cities, andrew);

        createTrip("Safari in Tanzania",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 24),
                12000.0, TripStatus.PLANNED, adventure, marcin);

        createTrip("Diving in Egypt",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 12),
                4500.0, TripStatus.COMPLETED, adventure, mariusz);
    }

    private User createUser(String login, String plainPassword, UserRole role, LocalDate dateOfBirth) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(login);
        user.setPassword(passwordHash.generate(plainPassword.toCharArray()));
        user.setRole(role);
        user.setDateOfBirth(dateOfBirth);
        userService.create(user);
        System.out.println("Created user: " + login + " with role: " + role);
        return user;
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
                           double cost, TripStatus status, DestinationCategory category, User user) {
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setTitle(title);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setEstimatedCost(cost);
        trip.setStatus(status);
        trip.setCategory(category);
        trip.setUser(user);
        tripService.create(trip);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code if needed
    }
}

