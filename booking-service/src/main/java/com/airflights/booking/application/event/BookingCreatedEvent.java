package com.airflights.booking.application.event;

import java.time.LocalDateTime;

public record BookingCreatedEvent(
        String eventId,
        Long bookingId,
        Long passengerId,
        Long flightId,
        LocalDateTime bookingTime,
        String passengerEmail
) {}
