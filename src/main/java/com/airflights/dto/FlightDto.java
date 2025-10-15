package com.airflights.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    @JsonAlias("id")
    @JsonProperty("id")
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
