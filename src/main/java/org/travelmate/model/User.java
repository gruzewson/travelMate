package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.*;

import org.travelmate.model.enums.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @ToString.Exclude
    @JsonbTransient
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ToString.Exclude
    @JsonbTransient
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Trip> trips;

    public User(String login, LocalDate dateOfBirth) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.dateOfBirth = dateOfBirth;
        this.role = UserRole.USER; // default role
    }

    public User(String login, String password, UserRole role, LocalDate dateOfBirth) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
    }
}

