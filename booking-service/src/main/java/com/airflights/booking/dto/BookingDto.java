package com.airflights.booking.dto;

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
public class BookingDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonAlias("passenger_id")
    @JsonProperty("passenger_id")
    private Long passengerId;

    @JsonAlias("flight_id")
    @JsonProperty("flight_id")
    private Long flightId;

    @JsonAlias("booking_time")
    @JsonProperty("booking_time")
    private LocalDateTime bookingTime;
}
