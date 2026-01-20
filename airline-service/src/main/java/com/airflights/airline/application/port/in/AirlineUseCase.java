package com.airflights.airline.application.port.in;

import com.airflights.airline.application.dto.AirlineDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AirlineUseCase {
    Flux<AirlineDto> getAll(int page, int size);
    Mono<AirlineDto> getById(Long id);
    Mono<AirlineDto> create(AirlineDto dto);
    Mono<AirlineDto> update(Long id, AirlineDto dto);
    Mono<Void> delete(Long id);
}
