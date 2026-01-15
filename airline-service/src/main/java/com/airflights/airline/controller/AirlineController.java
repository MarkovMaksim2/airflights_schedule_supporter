package com.airflights.airline.controller;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.service.AirlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
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

    private final AirlineService airlineService;

    @GetMapping
    @Operation(summary = "Get all airlines")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AirlineDto> getAll(@ParameterObject Pageable pageable) {
        return airlineService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airline by ID")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AirlineDto> getById(@PathVariable Long id) {
        return airlineService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create new airline")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirlineDto> create(
            @Valid @RequestBody AirlineDto dto,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User email required");
        }

        return airlineService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airline")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AirlineDto> update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return airlineService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airline")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return airlineService.delete(id);
    }
}