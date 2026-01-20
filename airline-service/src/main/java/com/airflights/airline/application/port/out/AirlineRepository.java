package com.airflights.airline.application.port.out;

import com.airflights.airline.domain.model.Airline;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AirlineRepository {
    Flux<Airline> findAll();
    Mono<Airline> findById(Long id);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByName(String name);
    Mono<Boolean> existsByContactEmail(String contactEmail);
    Mono<Airline> save(Airline airline);
    Mono<Void> deleteById(Long id);
}
