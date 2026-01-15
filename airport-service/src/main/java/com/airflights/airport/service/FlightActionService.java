package com.airflights.airport.service;

import com.airflights.airport.dto.AirportManagerDto;
import com.airflights.airport.messaging.FlightActionEventPublisher;
import com.airflights.airport.messaging.dto.FlightAction;
import com.airflights.airport.messaging.dto.FlightActionRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightActionService {

    private final AirportManagerService airportManagerService;
    private final FlightActionEventPublisher eventPublisher;

    public Mono<Void> requestFlightAction(Long flightId, FlightAction action, String userEmail) {
        if (flightId == null) {
            return Mono.error(new IllegalArgumentException("Flight id is required"));
        }
        if (action == null) {
            return Mono.error(new IllegalArgumentException("Flight action is required"));
        }
        if (userEmail == null || userEmail.isBlank()) {
            return Mono.error(new IllegalArgumentException("User email is required"));
        }
        return airportManagerService.getByEmail(userEmail)
                .map(AirportManagerDto::getAirportId)
                .flatMap(airportId -> Mono.fromRunnable(() -> publishEvent(flightId, airportId, action, userEmail)))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void publishEvent(Long flightId, Long airportId, FlightAction action, String userEmail) {
        FlightActionRequestedEvent event = new FlightActionRequestedEvent(
                UUID.randomUUID().toString(),
                flightId,
                airportId,
                action.name(),
                userEmail
        );
        eventPublisher.publish(event);
    }
}
