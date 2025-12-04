package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import org.travelmate.model.enums.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @JsonbTransient
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @JsonbTransient
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Trip> trips;

    public User() {
    }

    public User(String login, LocalDate dateOfBirth) {
        this.login = login;
        this.dateOfBirth = dateOfBirth;
        this.role = UserRole.USER;
    }

    public User(String login, String password, UserRole role, LocalDate dateOfBirth) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
