package com.airflights.flight.application.port.out;

public interface AirportManagerVerifierPort {
    Long getAirportIdByEmail(String email);
}
