package com.airflights.airport.service;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.entity.Airport;
import com.airflights.airport.mapper.AirportMapper;
import com.airflights.airport.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public Mono<Page<AirportDto>> getAll(Pageable pageable) {
        return Mono.fromCallable(() -> airportRepository.findAll(pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .map(page -> page.map(airportMapper::toDto))
                .doOnSubscribe(sub -> log.debug("Fetching all airports with pageable: {}", pageable))
                .doOnSuccess(page -> log.debug("Successfully fetched {} airports", page.getContent().size()))
                .doOnError(error -> log.error("Error fetching airports: {}", error.getMessage()));
    }

    public Mono<AirportDto> getById(Long id) {
        return Mono.fromCallable(() -> airportRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(new RuntimeException("Airport not found: " + id)))
                .map(airportMapper::toDto)
                .doOnSuccess(airport -> log.debug("Found airport by id {}: {}", id, airport))
                .doOnError(error -> log.error("Error finding airport by id {}: {}", id, error.getMessage()));
    }

    // Альтернативная реализация с асинхронными методами репозитория
    public Mono<AirportDto> getByIdAsync(Long id) {
        return Mono.fromFuture(airportRepository.findByIdAsync(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(new RuntimeException("Airport not found: " + id)))
                .map(airportMapper::toDto);
    }

    public Mono<Airport> getByIdEntity(Long id) {
        return Mono.fromCallable(() -> airportRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(new RuntimeException("Airport not found: " + id)))
                .doOnSuccess(airport -> log.debug("Found airport entity by id: {}", id));
    }

    public Mono<AirportDto> create(AirportDto dto) {
        return Mono.fromCallable(() -> airportRepository.existsByCode(dto.getCode()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Airport with code '" + dto.getCode() + "' already exists"));
                    }
                    Airport entity = airportMapper.toEntity(dto);
                    return Mono.fromCallable(() -> airportRepository.save(entity))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .map(airportMapper::toDto)
                .doOnSuccess(saved -> log.info("Created new airport: {}", saved))
                .doOnError(error -> log.error("Error creating airport: {}", error.getMessage()));
    }

    public Mono<AirportDto> update(Long id, AirportDto dto) {
        return Mono.fromCallable(() -> airportRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new RuntimeException("Airport not found: " + id));
                    }
                    Airport existing = optional.get();

                    // Проверяем уникальность кода, если он изменился
                    if (dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {
                        return Mono.fromCallable(() -> airportRepository.existsByCode(dto.getCode()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(codeExists -> {
                                    if (codeExists) {
                                        return Mono.error(new IllegalArgumentException("Airport with code '" + dto.getCode() + "' already exists"));
                                    }
                                    return updateAirportFields(existing, dto);
                                });
                    } else {
                        return updateAirportFields(existing, dto);
                    }
                })
                .map(airportMapper::toDto)
                .doOnSuccess(updated -> log.info("Updated airport with id {}: {}", id, updated))
                .doOnError(error -> log.error("Error updating airport with id {}: {}", id, error.getMessage()));
    }

    private Mono<Airport> updateAirportFields(Airport existing, AirportDto dto) {
        return Mono.fromCallable(() -> {
            if (dto.getCode() != null) existing.setCode(dto.getCode());
            if (dto.getName() != null) existing.setName(dto.getName());
            if (dto.getCity() != null) existing.setCity(dto.getCity());
            return airportRepository.save(existing);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> delete(Long id) {
        return Mono.fromCallable(() -> airportRepository.existsById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new RuntimeException("Airport not found: " + id));
                    }
                    return Mono.fromRunnable(() -> airportRepository.deleteById(id))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .then()
                .doOnSuccess(v -> log.info("Deleted airport with id: {}", id))
                .doOnError(error -> log.error("Error deleting airport with id {}: {}", id, error.getMessage()));
    }

    // Дополнительные реактивные методы
    public Flux<AirportDto> getAllStream() {
        return Mono.fromCallable(airportRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(airportMapper::toDto);
    }

    public Mono<Long> count() {
        return Mono.fromCallable(airportRepository::count)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AirportDto> findByCode(String code) {
        return Mono.fromCallable(() -> airportRepository.findByCode(code))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(new RuntimeException("Airport not found with code: " + code)))
                .map(airportMapper::toDto);
    }
}