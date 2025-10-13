package com.airflights.controller;

import com.airflights.dto.AirportDto;
import com.airflights.service.AirportService;
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
    })
    public ResponseEntity<Page<AirportDto>> getAll(Pageable pageable) {
        Page<AirportDto> result = airportService.getAll(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve a specific airport by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
        @ApiResponse(responseCode = "404", description = "Airport not found"),
    })
    public ResponseEntity<AirportDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(airportService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new airport", description = "Create a new airport with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Airport created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<AirportDto> create(@Valid @RequestBody AirportDto dto) {
        AirportDto created = airportService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airport", description = "Update the details of an existing airport by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Airport updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Airport not found"),
    })
    public ResponseEntity<AirportDto> update(@PathVariable Long id, @Valid @RequestBody AirportDto dto) {
        return ResponseEntity.ok(airportService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an airport", description = "Delete an existing airport by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Airport deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Airport not found"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
