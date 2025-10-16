package org.travelmate.model;

import lombok.*;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String login;
    private LocalDate dateOfBirth;

    @ToString.Exclude
    private List<Trip> trips;

    public User(String login, LocalDate dateOfBirth) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.dateOfBirth = dateOfBirth;
    }
}
