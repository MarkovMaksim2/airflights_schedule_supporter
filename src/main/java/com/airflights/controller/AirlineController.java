package com.airflights.controller;

import com.airflights.dto.AirlineDto;
import com.airflights.service.AirlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@Tag(name = "Airline API", description = "Airline usages")
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    @Operation(summary = "Get all airlines with pagination", description = "Retrieve a paginated list of all airlines")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    })
    public ResponseEntity<Page<AirlineDto>> getAll(@ParameterObject Pageable pageable) {
        Page<AirlineDto> result = airlineService.getAll(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airline by ID", description = "Retrieve a specific airline by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved airline"),
        @ApiResponse(responseCode = "404", description = "Airline not found"),
    })
    public ResponseEntity<AirlineDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(airlineService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new airline", description = "Create a new airline with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Airline created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<AirlineDto> create(@Valid @RequestBody AirlineDto dto) {
        AirlineDto created = airlineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing airline", description = "Update the details of an existing airline by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Airline updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Airline not found"),
    })
    public ResponseEntity<AirlineDto> update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return ResponseEntity.ok(airlineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an airline", description = "Delete an existing airline by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Airline deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Airline not found"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airlineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
