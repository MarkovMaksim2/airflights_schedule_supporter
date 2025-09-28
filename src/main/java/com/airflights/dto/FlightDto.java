package com.airflights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlightDto {
    private Long id;
    private Long airlineId;
    private Long departureAirportId;
    private Long arrivalAirportId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
}
