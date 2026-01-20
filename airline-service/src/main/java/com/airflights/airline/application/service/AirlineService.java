package com.airflights.airline.application.service;

import com.airflights.airline.application.dto.AirlineDto;
import com.airflights.airline.application.exception.ResourceNotFoundException;
import com.airflights.airline.application.mapper.AirlineMapper;
import com.airflights.airline.application.port.in.AirlineUseCase;
import com.airflights.airline.application.port.out.AirlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineService implements AirlineUseCase {
    private static final String NOT_FOUND = "Airline not found: ";
    private static final String ALREADY_EXISTS = "' already exists";
    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    @Override
    public Flux<AirlineDto> getAll(int page, int size) {
        long offset = (long) page * size;
        return airlineRepository.findAll()
                .skip(offset)
                .take(size)
                .map(airlineMapper::toDto)
                .doOnSubscribe(subscription -> log.debug("Fetching all airlines with page: {}, size: {}", page, size));
    }

    @Override
    public Mono<AirlineDto> getById(Long id) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(NOT_FOUND + id)))
                .map(airlineMapper::toDto)
                .doOnSuccess(airline -> log.debug("Found airline by id {}: {}", id, airline))
                .doOnError(error -> log.error("Error finding airline by id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<AirlineDto> create(AirlineDto dto) {
        return Mono.just(dto)
                .flatMap(airlineDto -> {
                    if (dto.getName() != null) {
                        return airlineRepository.existsByName(dto.getName())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new IllegalArgumentException("Airline with name '" + dto.getName() + ALREADY_EXISTS));
                                    }
                                    return Mono.just(dto);
                                });
                    }
                    return Mono.just(dto);
                })
                .flatMap(airlineDto -> {
                    if (dto.getContactEmail() != null) {
                        return airlineRepository.existsByContactEmail(dto.getContactEmail())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new IllegalArgumentException("Airline with contact_email '" + dto.getContactEmail() + ALREADY_EXISTS));
                                    }
                                    return Mono.just(dto);
                                });
                    }
                    return Mono.just(dto);
                })
                .map(airlineMapper::toDomain)
                .flatMap(airlineRepository::save)
                .map(airlineMapper::toDto)
                .doOnSuccess(saved -> log.info("Created new airline: {}", saved))
                .doOnError(error -> log.error("Error creating airline: {}", error.getMessage()));
    }

    @Override
    public Mono<AirlineDto> update(Long id, AirlineDto dto) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(NOT_FOUND + id)))
                .flatMap(existingAirline -> {
                    if (dto.getName() != null && !dto.getName().equals(existingAirline.getName())) {
                        return airlineRepository.existsByName(dto.getName())
                                .flatMap(nameExists -> {
                                    if (Boolean.TRUE.equals(nameExists)) {
                                        return Mono.error(new IllegalArgumentException("Airline with name '" + dto.getName() + ALREADY_EXISTS));
                                    }
                                    existingAirline.setName(dto.getName());
                                    if (dto.getContactEmail() != null) {
                                        existingAirline.setContactEmail(dto.getContactEmail());
                                    }
                                    return Mono.just(existingAirline);
                                });
                    }
                    if (dto.getContactEmail() != null) {
                        existingAirline.setContactEmail(dto.getContactEmail());
                    }
                    return Mono.just(existingAirline);
                })
                .flatMap(airlineRepository::save)
                .map(airlineMapper::toDto)
                .doOnSuccess(updated -> log.info("Updated airline with id {}: {}", id, updated))
                .doOnError(error -> log.error("Error updating airline with id {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return airlineRepository.existsById(id)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new ResourceNotFoundException(NOT_FOUND + id));
                    }
                    return airlineRepository.deleteById(id);
                })
                .doOnSuccess(v -> log.info("Deleted airline with id: {}", id))
                .doOnError(error -> log.error("Error deleting airline with id {}: {}", id, error.getMessage()));
    }
}
