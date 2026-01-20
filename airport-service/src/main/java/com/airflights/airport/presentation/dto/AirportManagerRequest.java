package com.airflights.airport.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManagerRequest {
    @JsonAlias("airport_id")
    @JsonProperty("airport_id")
    @NotNull
    private Long airportId;

    @JsonAlias("user_email")
    @JsonProperty("user_email")
    @NotBlank
    private String userEmail;
}
