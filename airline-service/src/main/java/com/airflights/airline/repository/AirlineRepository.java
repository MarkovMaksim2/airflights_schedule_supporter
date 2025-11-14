package com.airflights.airline.repository;

import com.airflights.airline.entity.Airline;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AirlineRepository extends ReactiveCrudRepository<Airline, Long> {
    Mono<Boolean> existsByName(String name);

    Mono<Airline> findByName(String name);
}