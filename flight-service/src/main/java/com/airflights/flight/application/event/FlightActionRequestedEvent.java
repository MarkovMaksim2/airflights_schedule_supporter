package com.airflights.flight.application.event;

public record FlightActionRequestedEvent(
        String eventId,
        Long flightId,
        Long airportId,
        String action,
        String userEmail
) {}
