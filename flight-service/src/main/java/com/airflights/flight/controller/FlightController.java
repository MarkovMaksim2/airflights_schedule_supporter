package com.airflights.flight.controller;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.service.FlightService;

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
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flight API", description = "Flight usages")
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    @Operation(summary = "Get all flights with pagination", description = "Retrieve a paginated list of all flights")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<FlightDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(flightService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve a specific flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved flight"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FlightDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new flight", description = "Create a new flight with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Flight created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FlightDto> create(@Valid @RequestBody FlightDto dto) {
        FlightDto created = flightService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing flight", description = "Update the details of an existing flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flight updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FlightDto> update(@PathVariable Long id, @Valid @RequestBody FlightDto dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a flight", description = "Delete an existing flight by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Flight not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
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
    public ResponseEntity<Iterable<FlightDto>> infiniteScroll(@RequestParam(required = false, defaultValue = "0") int offset,
                                                              @RequestParam(required = false, defaultValue = "20") int limit) {
        return ResponseEntity.ok(flightService.getInfiniteScroll(offset, limit));
    }

    @PatchMapping("/update-due-to-restriction")
    public ResponseEntity<Void> updateDueToRestriction(@Valid @RequestBody RestrictedZoneDto dto) {
        flightService.updateFlightsDueToRestriction(dto);
        return  ResponseEntity.noContent().build();
    }
}
