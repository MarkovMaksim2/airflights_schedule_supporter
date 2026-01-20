package com.airflights.flight.presentation.controller;

import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.port.in.FlightUseCase;
import com.airflights.flight.presentation.dto.FlightRequest;
import com.airflights.flight.presentation.dto.FlightResponse;
import com.airflights.flight.presentation.dto.RestrictedZoneRequest;
import com.airflights.flight.presentation.mapper.FlightPresentationMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "Flight API", description = "Flight usages")
public class FlightController {

    private final FlightUseCase flightUseCase;
    private final FlightPresentationMapper flightPresentationMapper;

    public FlightController(FlightUseCase flightUseCase, FlightPresentationMapper flightPresentationMapper) {
        this.flightUseCase = flightUseCase;
        this.flightPresentationMapper = flightPresentationMapper;
    }

    @GetMapping
    @Operation(summary = "Get all flights with pagination", description = "Retrieve a paginated list of all flights")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    })
    public ResponseEntity<Page<FlightResponse>> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        Page<FlightResponse> page = flightUseCase.getAll(pageable)
                .map(flightPresentationMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve a specific flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved flight"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    public ResponseEntity<FlightResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightPresentationMapper.toResponse(flightUseCase.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new flight", description = "Create a new flight with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Flight created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<FlightResponse> create(
            @Valid @RequestBody FlightRequest request,
            @RequestHeader(value = "X-Auth-Roles", required = false) String roles,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        FlightDto created = flightUseCase.create(flightPresentationMapper.toDto(request), roles, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flightPresentationMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing flight", description = "Update the details of an existing flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flight updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    public ResponseEntity<FlightResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest request,
            @RequestHeader(value = "X-Auth-Roles", required = false) String roles,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        FlightDto updated = flightUseCase.update(id, flightPresentationMapper.toDto(request), roles, userEmail);
        return ResponseEntity.ok(flightPresentationMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a flight", description = "Delete an existing flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Roles", required = false) String roles,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        flightUseCase.delete(id, roles, userEmail);
        return ResponseEntity.noContent().build();
    }

    /**
     * Бесконечная прокрутка без total count — lazy pagination (пример для задания).
     */
    @GetMapping("/scroll")
    @Operation(summary = "Get flights with infinite scroll", description = "Retrieve flights using offset and limit for infinite scrolling")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Iterable<FlightResponse>> infiniteScroll(@RequestParam(required = false, defaultValue = "0") int offset,
                                                                   @RequestParam(required = false, defaultValue = "20") int limit) {
        Iterable<FlightResponse> results = flightUseCase.getInfiniteScroll(offset, limit)
                .stream()
                .map(flightPresentationMapper::toResponse)
                .toList();
        return ResponseEntity.ok(results);
    }

    @PatchMapping("/update-due-to-restriction")
    public ResponseEntity<Void> updateDueToRestriction(@Valid @RequestBody RestrictedZoneRequest request) {
        flightUseCase.updateFlightsDueToRestriction(flightPresentationMapper.toDto(request));
        return  ResponseEntity.noContent().build();
    }

    // Flight approval/arrival/departure actions are handled asynchronously via airport-service + Kafka.
}
