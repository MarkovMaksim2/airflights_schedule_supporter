package com.airflights.airport.controller;

import com.airflights.airport.dto.AirportManagerDto;
import com.airflights.airport.service.AirportManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/airport-managers")
@RequiredArgsConstructor
@Tag(name = "Airport Manager API", description = "Airport manager mappings")
public class AirportManagerController {

    private final AirportManagerService airportManagerService;

    @GetMapping("/by-email")
    @Operation(summary = "Get airport manager by email")
    public Mono<AirportManagerDto> getByEmail(@RequestParam String email) {
        return airportManagerService.getByEmail(email);
    }

    @PostMapping
    @Operation(summary = "Create airport manager mapping")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mapping created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Airport not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AirportManagerDto> create(@Valid @RequestBody AirportManagerDto dto) {
        return airportManagerService.create(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airport manager mapping")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return airportManagerService.delete(id);
    }
}
