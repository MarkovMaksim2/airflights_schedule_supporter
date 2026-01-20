package com.airflights.airport.application.event;

public record FlightActionRequestedEvent(
        String eventId,
        Long flightId,
        Long airportId,
        String action,
        String userEmail
) {}
