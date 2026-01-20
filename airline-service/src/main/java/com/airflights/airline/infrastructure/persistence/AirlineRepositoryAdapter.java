package com.airflights.airline.infrastructure.persistence;

import com.airflights.airline.application.port.out.AirlineRepository;
import com.airflights.airline.domain.model.Airline;
import com.airflights.airline.infrastructure.persistence.mapper.AirlineEntityMapper;
import com.airflights.airline.infrastructure.persistence.repository.R2dbcAirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AirlineRepositoryAdapter implements AirlineRepository {
    private final R2dbcAirlineRepository r2dbcAirlineRepository;
    private final AirlineEntityMapper airlineEntityMapper;

    @Override
    public Flux<Airline> findAll() {
        return r2dbcAirlineRepository.findAll()
                .map(airlineEntityMapper::toDomain);
    }

    @Override
    public Mono<Airline> findById(Long id) {
        return r2dbcAirlineRepository.findById(id)
                .map(airlineEntityMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return r2dbcAirlineRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return r2dbcAirlineRepository.existsByName(name);
    }

    @Override
    public Mono<Boolean> existsByContactEmail(String contactEmail) {
        return r2dbcAirlineRepository.existsByContactEmail(contactEmail);
    }

    @Override
    public Mono<Airline> save(Airline airline) {
        return r2dbcAirlineRepository.save(airlineEntityMapper.toEntity(airline))
                .map(airlineEntityMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return r2dbcAirlineRepository.deleteById(id);
    }
}
