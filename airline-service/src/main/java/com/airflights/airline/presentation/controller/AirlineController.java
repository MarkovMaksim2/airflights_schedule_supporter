package com.airflights.airline.presentation.controller;

import com.airflights.airline.application.port.in.AirlineUseCase;
import com.airflights.airline.presentation.dto.AirlineRequest;
import com.airflights.airline.presentation.dto.AirlineResponse;
import com.airflights.airline.presentation.mapper.AirlinePresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@Tag(name = "Airline API")
public class AirlineController {

    private final AirlineUseCase airlineUseCase;
    private final AirlinePresentationMapper airlinePresentationMapper;

    @GetMapping
    @Operation(summary = "Get all airlines")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AirlineResponse> getAll(@ParameterObject org.springframework.data.domain.Pageable pageable) {
        return airlineUseCase.getAll(pageable.getPageNumber(), pageable.getPageSize())
                .map(airlinePresentationMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airline by ID")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AirlineResponse> getById(@PathVariable Long id) {
        return airlineUseCase.getById(id)
                .map(airlinePresentationMapper::toResponse);
    }

    @PostMapping
    @Operation(summary = "Create new airline")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirlineResponse> create(
            @Valid @RequestBody AirlineRequest request,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User email required");
        }
        AirlineRequest enriched = new AirlineRequest(request.getName(), userEmail);
        return airlineUseCase.create(airlinePresentationMapper.toDto(enriched))
                .map(airlinePresentationMapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airline")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AirlineResponse> update(@PathVariable Long id, @Valid @RequestBody AirlineRequest request) {
        return airlineUseCase.update(id, airlinePresentationMapper.toDto(request))
                .map(airlinePresentationMapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return airlineUseCase.delete(id);
    }
}
