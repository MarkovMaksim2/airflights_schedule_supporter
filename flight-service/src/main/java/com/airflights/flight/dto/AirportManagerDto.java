package com.airflights.flight.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManagerDto {
    private Long id;

    @JsonAlias("airport_id")
    @JsonProperty("airport_id")
    private Long airportId;

    @JsonAlias("user_email")
    @JsonProperty("user_email")
    private String userEmail;
}
