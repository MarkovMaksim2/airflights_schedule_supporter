package com.airflights.airport.controller;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<Page<AirportDto>>> getAll(Pageable pageable) {
        return airportService.getAll(pageable)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve a specific airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<AirportDto>> getById(@PathVariable Long id) {
        return airportService.getById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get airport by code", description = "Retrieve a specific airport by its code")
    public Mono<ResponseEntity<AirportDto>> getByCode(@PathVariable String code) {
        return airportService.findByCode(code)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    @Operation(summary = "Create a new airport", description = "Create a new airport with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<AirportDto>> create(@Valid @RequestBody AirportDto dto) {
        return airportService.create(dto)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airport", description = "Update the details of an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Airport updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<AirportDto>> update(@PathVariable Long id, @Valid @RequestBody AirportDto dto) {
        return airportService.update(id, dto)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    if (error instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an airport", description = "Delete an existing airport by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Airport deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return airportService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> {
                    if (error.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/count")
    @Operation(summary = "Get airports count", description = "Get total number of airports")
    public Mono<ResponseEntity<Long>> count() {
        return airportService.count()
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().build()));
    }
}