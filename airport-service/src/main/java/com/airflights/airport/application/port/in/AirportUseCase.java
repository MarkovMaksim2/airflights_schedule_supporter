package com.airflights.airport.application.port.in;

import com.airflights.airport.application.dto.AirportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AirportUseCase {
    Flux<AirportDto> getAll(int page, int size);
    Mono<AirportDto> getById(Long id);
    Mono<AirportDto> findByCode(String code);
    Mono<AirportDto> create(AirportDto dto);
    Mono<AirportDto> update(Long id, AirportDto dto);
    Mono<Void> delete(Long id);
    Mono<Long> count();
}
