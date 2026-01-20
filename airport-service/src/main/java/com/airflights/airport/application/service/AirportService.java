package com.airflights.airport.application.service;

import com.airflights.airport.application.dto.AirportDto;
import com.airflights.airport.application.exception.ResourceNotFoundException;
import com.airflights.airport.application.mapper.AirportMapper;
import com.airflights.airport.application.port.in.AirportUseCase;
import com.airflights.airport.application.port.out.AirportRepository;
import com.airflights.airport.domain.model.Airport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService implements AirportUseCase {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;
    private final TransactionTemplate tx;

    @Override
    public Flux<AirportDto> getAll(int page, int size) {
        return Mono.fromCallable(() ->
                        tx.execute(status -> airportRepository.findAll(page, size)
                                .stream()
                                .map(airportMapper::toDto)
                                .collect(Collectors.toList())
                        )
                )
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> log.debug("Fetching airports: page={}, size={}", page, size))
                .doOnError(e -> log.error("Error fetching airports: {}", e.getMessage()));
    }

    @Override
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

    @Override
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

                            Airport saved = airportRepository.save(airportMapper.toDomain(dto));
                            return airportMapper.toDto(saved);
                        })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(a -> log.info("Created airport: {}", a))
                .doOnError(e -> log.error("Error creating airport: {}", e.getMessage()));
    }

    @Override
    public Mono<AirportDto> update(Long id, AirportDto dto) {
        return Mono.fromCallable(() ->
                        tx.execute(status -> updateAirport(id, dto))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(a -> log.info("Updated airport {}: {}", id, a))
                .doOnError(e -> log.error("Error updating {}: {}", id, e.getMessage()));
    }

    @Override
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

    @Override
    public Mono<Long> count() {
        return Mono.fromCallable(airportRepository::count)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<AirportDto> findByCode(String code) {
        return Mono.fromCallable(() ->
                        airportRepository.findByCode(code)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Airport not found with code: " + code))
                )
                .map(airportMapper::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private AirportDto updateAirport(Long id, AirportDto dto) {
        Airport existing = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found: " + id));

        updateCodeIfNeeded(existing, dto);
        updateNameIfNeeded(existing, dto);
        updateCityIfNeeded(existing, dto);

        Airport saved = airportRepository.save(existing);
        return airportMapper.toDto(saved);
    }

    private void updateCodeIfNeeded(Airport existing, AirportDto dto) {
        if (dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {
            if (airportRepository.existsByCode(dto.getCode())) {
                throw new IllegalArgumentException("Airport with code '" + dto.getCode() + "' already exists");
            }
            existing.setCode(dto.getCode());
        }
    }

    private void updateNameIfNeeded(Airport existing, AirportDto dto) {
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            if (airportRepository.existsByName(dto.getName())) {
                throw new IllegalArgumentException("Airport with name '" + dto.getName() + "' already exists");
            }
            existing.setName(dto.getName());
        }
    }

    private void updateCityIfNeeded(Airport existing, AirportDto dto) {
        if (dto.getCity() != null) {
            existing.setCity(dto.getCity());
        }
    }
}
