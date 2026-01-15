package com.airflights.flight.messaging.dto;

public record FlightActionRequestedEvent(
        String eventId,
        Long flightId,
        Long airportId,
        String action,
        String userEmail
) {}
