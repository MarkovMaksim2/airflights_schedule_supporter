package com.airflights.airline.service;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.entity.Airline;
import com.airflights.airline.exception.ResourceNotFoundException;
import com.airflights.airline.mapper.AirlineMapper;
import com.airflights.airline.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    public Flux<AirlineDto> getAll(Pageable pageable) {
        return airlineRepository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .map(airlineMapper::toDto)
                .doOnSubscribe(subscription -> log.debug("Fetching all airlines with pageable: {}", pageable));
    }

    public Mono<AirlineDto> getById(Long id) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Airline not found: " + id)))
                .map(airlineMapper::toDto)
                .doOnSuccess(airline -> log.debug("Found airline by id {}: {}", id, airline))
                .doOnError(error -> log.error("Error finding airline by id {}: {}", id, error.getMessage()));
    }

    public Mono<Airline> getByIdEntity(Long id) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Airline not found: " + id)))
                .doOnSuccess(airline -> log.debug("Found airline entity by id: {}", id));
    }

    public Mono<AirlineDto> create(AirlineDto dto) {
        return Mono.just(dto)
                .flatMap(airlineDto -> {
                    if (dto.getName() != null) {
                        return airlineRepository.existsByName(dto.getName())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new IllegalArgumentException("Airline with name '" + dto.getName() + "' already exists"));
                                    }
                                    return Mono.just(dto);
                                });
                    }
                    return Mono.just(dto);
                })
                .map(airlineMapper::toEntity)
                .doOnNext(entity -> entity.setId(null))
                .flatMap(airlineRepository::save)
                .map(airlineMapper::toDto)
                .doOnSuccess(saved -> log.info("Created new airline: {}", saved))
                .doOnError(error -> log.error("Error creating airline: {}", error.getMessage()));
    }

    public Mono<AirlineDto> update(Long id, AirlineDto dto) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Airline not found: " + id)))
                .flatMap(existingAirline -> {
                    if (dto.getName() != null && !dto.getName().equals(existingAirline.getName())) {
                        return airlineRepository.existsByName(dto.getName())
                                .flatMap(nameExists -> {
                                    if (nameExists) {
                                        return Mono.error(new IllegalArgumentException("Airline with name '" + dto.getName() + "' already exists"));
                                    }
                                    existingAirline.setName(dto.getName());
                                    if (dto.getContactEmail() != null) {
                                        existingAirline.setContactEmail(dto.getContactEmail());
                                    }
                                    return Mono.just(existingAirline);
                                });
                    } else {
                        if (dto.getContactEmail() != null) {
                            existingAirline.setContactEmail(dto.getContactEmail());
                        }
                        return Mono.just(existingAirline);
                    }
                })
                .flatMap(airlineRepository::save)
                .map(airlineMapper::toDto)
                .doOnSuccess(updated -> log.info("Updated airline with id {}: {}", id, updated))
                .doOnError(error -> log.error("Error updating airline with id {}: {}", id, error.getMessage()));
    }

    public Mono<Void> delete(Long id) {
        return airlineRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Airline not found: " + id));
                    }
                    return airlineRepository.deleteById(id);
                })
                .doOnSuccess(v -> log.info("Deleted airline with id: {}", id))
                .doOnError(error -> log.error("Error deleting airline with id {}: {}", id, error.getMessage()));
    }
}