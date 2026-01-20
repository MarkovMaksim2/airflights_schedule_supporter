package com.airflights.airport.application.service;

import com.airflights.airport.application.event.FlightActionRequestedEvent;
import com.airflights.airport.application.exception.ResourceNotFoundException;
import com.airflights.airport.application.port.in.FlightActionUseCase;
import com.airflights.airport.application.port.out.AirportManagerRepository;
import com.airflights.airport.application.port.out.FlightActionEventPublisher;
import com.airflights.airport.domain.model.FlightAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightActionService implements FlightActionUseCase {

    private final AirportManagerRepository airportManagerRepository;
    private final FlightActionEventPublisher eventPublisher;

    @Override
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
        return Mono.fromCallable(() ->
                        airportManagerRepository.findByUserEmailIgnoreCase(userEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("Airport manager not found: " + userEmail))
                                .getAirportId()
                )
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
