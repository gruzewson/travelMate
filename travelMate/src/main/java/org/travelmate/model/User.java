package org.travelmate.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
public class User {
    private String login;
    @ToString.Exclude
    private String password;
    private LocalDate dateOfBirth;

    private List<Trip> trips;
}
