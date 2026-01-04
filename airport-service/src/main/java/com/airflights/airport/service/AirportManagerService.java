package com.airflights.airport.service;

import com.airflights.airport.dto.AirportManagerDto;
import com.airflights.airport.entity.AirportManager;
import com.airflights.airport.exception.ResourceNotFoundException;
import com.airflights.airport.repository.AirportManagerRepository;
import com.airflights.airport.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportManagerService {

    private final AirportManagerRepository airportManagerRepository;
    private final AirportRepository airportRepository;
    private final TransactionTemplate tx;

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
                            AirportManager saved = airportManagerRepository.save(toEntity(dto));
                            return toDto(saved);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(m -> log.info("Created airport manager: {}", m))
                .doOnError(e -> log.error("Error creating airport manager: {}", e.getMessage()));
    }

    public Mono<AirportManagerDto> getByEmail(String email) {
        return Mono.fromCallable(() ->
                        airportManagerRepository.findByUserEmailIgnoreCase(email)
                                .orElseThrow(() -> new ResourceNotFoundException("Airport manager not found: " + email))
                )
                .map(this::toDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(m -> log.debug("Found airport manager for {}: {}", email, m))
                .doOnError(e -> log.error("Error getByEmail {}: {}", email, e.getMessage()));
    }

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

    private AirportManagerDto toDto(AirportManager entity) {
        return new AirportManagerDto(entity.getId(), entity.getAirportId(), entity.getUserEmail());
    }

    private AirportManager toEntity(AirportManagerDto dto) {
        AirportManager entity = new AirportManager();
        entity.setId(dto.getId());
        entity.setAirportId(dto.getAirportId());
        entity.setUserEmail(dto.getUserEmail());
        return entity;
    }
}
