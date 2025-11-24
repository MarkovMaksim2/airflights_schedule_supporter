package com.airflights.airline.repository;

import com.airflights.airline.entity.Airline;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AirlineRepository extends R2dbcRepository<Airline, Long> {
    Mono<Boolean> existsByName(String name);
}