package com.airflights.airport.service;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.entity.Airport;
import com.airflights.airport.exception.ResourceNotFoundException;
import com.airflights.airport.mapper.AirportMapper;
import com.airflights.airport.repository.AirportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;
    private final TransactionTemplate tx;

    public Flux<AirportDto> getAll(Pageable pageable) {
        return Mono.fromCallable(() ->
                        tx.execute( status -> airportRepository.findAll(pageable)
                                            .map(airportMapper::toDto)
                        )
                )
                .flatMapMany(pg -> Flux.fromIterable(pg.getContent()))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.debug("Fetching airports: {}", pageable))
                .doOnError(e -> log.error("Error fetching airports: {}", e.getMessage()));
    }

    public Mono<AirportDto> getById(Long id) {
        return Mono.fromCallable(() ->
                            airportRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Airport not found: " + id))
                        )
                .map(airportMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(a -> log.debug("Found airport {}: {}", id, a))
                .doOnError(e -> log.error("Error findById {}: {}", id, e.getMessage()));
    }

    public Mono<AirportDto> create(AirportDto dto) {
        return Mono.fromCallable(() ->
                        tx.execute(status -> {

                            if (airportRepository.existsByCode(dto.getCode())) {
                                throw new IllegalArgumentException(
                                        "Airport with code '" + dto.getCode() + "' already exists");
                            }

                            if (dto.getName() != null && airportRepository.existsByName(dto.getName())) {
                                throw new IllegalArgumentException(
                                        "Airport with name '" + dto.getName() + "' already exists");
                            }

                            Airport saved = airportRepository.save(airportMapper.toEntity(dto));
                            return airportMapper.toDto(saved);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(a -> log.info("Created airport: {}", a))
                .doOnError(e -> log.error("Error creating airport: {}", e.getMessage()));
    }

    public Mono<AirportDto> update(Long id, AirportDto dto) {
        return Mono.fromCallable(() ->
                        tx.execute(status -> {

                            Airport existing = airportRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Airport not found: " + id));

                            if (dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {
                                if (airportRepository.existsByCode(dto.getCode())) {
                                    throw new IllegalArgumentException(
                                            "Airport with code '" + dto.getCode() + "' already exists");
                                }
                                existing.setCode(dto.getCode());
                            }

                            if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
                                if (airportRepository.existsByName(dto.getName())) {
                                    throw new IllegalArgumentException(
                                            "Airport with name '" + dto.getName() + "' already exists");
                                }
                                existing.setName(dto.getName());
                            }

                            if (dto.getCity() != null) existing.setCity(dto.getCity());

                            Airport saved = airportRepository.save(existing);
                            return airportMapper.toDto(saved);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(a -> log.info("Updated airport {}: {}", id, a))
                .doOnError(e -> log.error("Error updating {}: {}", id, e.getMessage()));
    }

    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() ->
                        tx.executeWithoutResult(status -> {

                            if (!airportRepository.existsById(id)) {
                                throw new ResourceNotFoundException("Airport not found: " + id);
                            }

                            airportRepository.deleteById(id);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then()
                .doOnSuccess(v -> log.info("Deleted airport {}", id))
                .doOnError(e -> log.error("Error deleting {}: {}", id, e.getMessage()));
    }

    public Mono<Long> count() {
        return Mono.fromCallable(airportRepository::count)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AirportDto> findByCode(String code) {
        return Mono.fromCallable(() ->
                            airportRepository.findByCode(code)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("Airport not found with code: " + code))

                        )
                .map(airportMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
