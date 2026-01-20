package com.airflights.booking.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @JsonAlias("passenger_id")
    @JsonProperty("passenger_id")
    private Long passengerId;

    @JsonAlias("flight_id")
    @JsonProperty("flight_id")
    private Long flightId;
}
