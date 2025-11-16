package org.travelmate.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.*;
import lombok.ToString;

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
    }
}
