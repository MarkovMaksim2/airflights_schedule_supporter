package com.airflights.airport.application.service;

import com.airflights.airport.application.dto.AirportManagerDto;
import com.airflights.airport.application.exception.ResourceNotFoundException;
import com.airflights.airport.application.mapper.AirportManagerMapper;
import com.airflights.airport.application.port.in.AirportManagerUseCase;
import com.airflights.airport.application.port.out.AirportManagerRepository;
import com.airflights.airport.application.port.out.AirportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportManagerService implements AirportManagerUseCase {

    private final AirportManagerRepository airportManagerRepository;
    private final AirportRepository airportRepository;
    private final AirportManagerMapper airportManagerMapper;
    private final TransactionTemplate tx;

    @Override
    public Mono<AirportManagerDto> create(AirportManagerDto dto) {
        return Mono.fromCallable(() ->
                        tx.execute(status -> {
                            if (dto.getAirportId() == null) {
                                throw new IllegalArgumentException("Airport id is required");
                            }
                            if (dto.getUserEmail() == null || dto.getUserEmail().isBlank()) {
                                throw new IllegalArgumentException("User email is required");
                            }
                            if (!airportRepository.existsById(dto.getAirportId())) {
                                throw new ResourceNotFoundException("Airport not found: " + dto.getAirportId());
                            }
                            if (airportManagerRepository.existsByUserEmailIgnoreCase(dto.getUserEmail())) {
                                throw new IllegalArgumentException("Airport manager already exists: " + dto.getUserEmail());
                            }
                            return airportManagerMapper.toDto(
                                    airportManagerRepository.save(airportManagerMapper.toDomain(dto))
                            );
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(m -> log.info("Created airport manager: {}", m))
                .doOnError(e -> log.error("Error creating airport manager: {}", e.getMessage()));
    }

    @Override
    public Mono<AirportManagerDto> getByEmail(String email) {
        return Mono.fromCallable(() ->
                        airportManagerRepository.findByUserEmailIgnoreCase(email)
                                .orElseThrow(() -> new ResourceNotFoundException("Airport manager not found: " + email))
                )
                .map(airportManagerMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(m -> log.debug("Found airport manager for {}: {}", email, m))
                .doOnError(e -> log.error("Error getByEmail {}: {}", email, e.getMessage()));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() ->
                        tx.executeWithoutResult(status -> {
                            if (!airportManagerRepository.existsById(id)) {
                                throw new ResourceNotFoundException("Airport manager not found: " + id);
                            }
                            airportManagerRepository.deleteById(id);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then()
                .doOnSuccess(v -> log.info("Deleted airport manager {}", id))
                .doOnError(e -> log.error("Error deleting airport manager {}: {}", id, e.getMessage()));
    }
}
