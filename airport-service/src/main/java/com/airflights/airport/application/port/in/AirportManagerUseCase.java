package com.airflights.airport.application.port.in;

import com.airflights.airport.application.dto.AirportManagerDto;
import reactor.core.publisher.Mono;

public interface AirportManagerUseCase {
    Mono<AirportManagerDto> create(AirportManagerDto dto);
    Mono<AirportManagerDto> getByEmail(String email);
    Mono<Void> delete(Long id);
}
