package com.airflights.airline.infrastructure.persistence.repository;

import com.airflights.airline.infrastructure.persistence.entity.AirlineEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface R2dbcAirlineRepository extends R2dbcRepository<AirlineEntity, Long> {
    Mono<Boolean> existsByName(String name);
    Mono<Boolean> existsByContactEmail(String contactEmail);
}
