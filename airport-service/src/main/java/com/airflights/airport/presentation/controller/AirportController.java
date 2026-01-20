package com.airflights.airport.presentation.controller;

import com.airflights.airport.application.port.in.AirportUseCase;
import com.airflights.airport.presentation.dto.AirportRequest;
import com.airflights.airport.presentation.dto.AirportResponse;
import com.airflights.airport.presentation.mapper.AirportPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Tag(name = "Airport API", description = "Airport usages")
public class AirportController {

    private final AirportUseCase airportUseCase;
    private final AirportPresentationMapper airportPresentationMapper;

    @GetMapping
    @Operation(summary = "Get all airports with pagination", description = "Retrieve a paginated list of all airports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<AirportResponse> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        return airportUseCase.getAll(pageable.getPageNumber(), pageable.getPageSize())
                .map(airportPresentationMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve a specific airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<AirportResponse> getById(@PathVariable Long id) {
        return airportUseCase.getById(id)
                .map(airportPresentationMapper::toResponse);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get airport by code", description = "Retrieve a specific airport by its code")
    public Mono<AirportResponse> getByCode(@PathVariable String code) {
        return airportUseCase.findByCode(code)
                .map(airportPresentationMapper::toResponse);
    }

    @PostMapping
    @Operation(summary = "Create a new airport", description = "Create a new airport with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirportResponse> create(@Valid @RequestBody AirportRequest request) {
        return airportUseCase.create(airportPresentationMapper.toDto(request))
                .map(airportPresentationMapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airport", description = "Update the details of an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Airport updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<AirportResponse> update(@PathVariable Long id, @Valid @RequestBody AirportRequest request) {
        return airportUseCase.update(id, airportPresentationMapper.toDto(request))
                .map(airportPresentationMapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an airport", description = "Delete an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Airport deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<Void> delete(@PathVariable Long id) {
        return airportUseCase.delete(id);
    }

    @GetMapping("/count")
    @Operation(summary = "Get airports count", description = "Get total number of airports")
    public Mono<Long> count() {
        return airportUseCase.count();
    }
}
