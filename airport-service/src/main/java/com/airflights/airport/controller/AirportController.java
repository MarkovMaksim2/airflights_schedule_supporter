package com.airflights.airport.controller;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Tag(name = "Airport API", description = "Airport usages")
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    @Operation(summary = "Get all airports with pagination", description = "Retrieve a paginated list of all airports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<AirportDto> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        return airportService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve a specific airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<AirportDto> getById(@PathVariable Long id) {
        return airportService.getById(id);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get airport by code", description = "Retrieve a specific airport by its code")
    public Mono<AirportDto> getByCode(@PathVariable String code) {
        return airportService.findByCode(code);
    }

    @PostMapping
    @Operation(summary = "Create a new airport", description = "Create a new airport with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirportDto> create(@Valid @RequestBody AirportDto dto) {
        return airportService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airport", description = "Update the details of an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Airport updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<AirportDto> update(@PathVariable Long id, @Valid @RequestBody AirportDto dto) {
        return airportService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an airport", description = "Delete an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Airport deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<Void> delete(@PathVariable Long id) {
        return airportService.delete(id);
    }

    @GetMapping("/count")
    @Operation(summary = "Get airports count", description = "Get total number of airports")
    public Mono<Long> count() {
        return airportService.count();
    }
}
