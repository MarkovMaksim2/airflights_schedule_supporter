package com.airflights.flight.application.port.out;

import com.airflights.flight.application.dto.AirlineDto;

public interface AirlineVerifierPort {
    void ensureAirlineExists(Long airlineId);
    AirlineDto getAirline(Long airlineId);
}
