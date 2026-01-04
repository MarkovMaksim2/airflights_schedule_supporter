package com.airflights.airport.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManagerDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonAlias("airport_id")
    @JsonProperty("airport_id")
    @NotNull
    private Long airportId;

    @JsonAlias("user_email")
    @JsonProperty("user_email")
    @NotBlank
    private String userEmail;
}
