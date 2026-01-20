package com.airflights.passenger.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerRequest {
    @JsonAlias("first_name")
    @JsonProperty("first_name")
    private String firstName;

    @JsonAlias("last_name")
    @JsonProperty("last_name")
    private String lastName;

    @JsonAlias("email")
    @JsonProperty("email")
    private String email;

    @JsonAlias("passport_number")
    @JsonProperty("passport_number")
    private String passportNumber;
}
