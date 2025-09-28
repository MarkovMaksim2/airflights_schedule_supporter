package com.airflights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long passengerId;
    private Long flightId;
    private LocalDateTime bookingTime;
}
