package com.airflights.airport.application.port.in;

import com.airflights.airport.domain.model.FlightAction;
import reactor.core.publisher.Mono;

public interface FlightActionUseCase {
    Mono<Void> requestFlightAction(Long flightId, FlightAction action, String userEmail);
}
