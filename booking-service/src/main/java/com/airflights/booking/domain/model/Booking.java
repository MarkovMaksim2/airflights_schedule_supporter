package com.airflights.booking.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private Long id;
    private Long passengerId;
    private Long flightId;
    private LocalDateTime bookingTime;
}
