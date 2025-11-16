package org.travelmate.controller.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserRegistrationRequestDTO {
    private String login;
    private String password;
    private LocalDate dateOfBirth;

}

