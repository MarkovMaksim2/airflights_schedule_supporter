package com.airflights.airport.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManagerResponse {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty("airport_id")
    private Long airportId;

    @JsonProperty("user_email")
    private String userEmail;
}
