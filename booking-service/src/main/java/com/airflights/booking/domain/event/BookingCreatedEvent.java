package com.airflights.booking.domain.event;

import java.time.LocalDateTime;

public record BookingCreatedEvent(
        String eventId,
        Long bookingId,
        Long passengerId,
        Long flightId,
        LocalDateTime bookingTime,
        String passengerEmail
) {}
