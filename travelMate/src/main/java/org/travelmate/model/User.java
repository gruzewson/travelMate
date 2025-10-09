package org.travelmate.model;

import lombok.*;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String login;
    @ToString.Exclude
    private String password;
    private LocalDate dateOfBirth;

    private List<Trip> trips;
}
