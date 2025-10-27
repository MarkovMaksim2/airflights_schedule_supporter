package com.airflights.flight.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonAlias("airline_id")
    @JsonProperty("airline_id")
    private Long airlineId;

    @JsonAlias("departure_airport_id")
    @JsonProperty("departure_airport_id")
    private Long departureAirportId;

    @JsonAlias("arrival_airport_id")
    @JsonProperty("arrival_airport_id")
    private Long arrivalAirportId;

    @JsonAlias("departure_time")
    @JsonProperty("departure_time")
    private LocalDateTime departureTime;

    @JsonAlias("arrival_time")
    @JsonProperty("arrival_time")
    private LocalDateTime arrivalTime;

    @JsonAlias("status")
    @JsonProperty("status")
    private String status;
}
