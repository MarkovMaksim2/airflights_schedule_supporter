package com.airflights.booking.application.port.out;

public interface FlightVerifierPort {
    void ensureFlightExists(Long flightId);
}
