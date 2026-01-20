package com.airflights.flight.application.port.out;

public interface AirportVerifierPort {
    void ensureAirportExists(Long airportId);
}
