package com.airflights.passenger.presentation.controller;

import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.application.exception.ForbiddenException;
import com.airflights.passenger.application.port.in.PassengerUseCase;
import com.airflights.passenger.presentation.dto.PassengerRequest;
import com.airflights.passenger.presentation.dto.PassengerResponse;
import com.airflights.passenger.presentation.mapper.PassengerPresentationMapper;
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
import java.util.Arrays;

@RestController
@RequestMapping("/api/passengers")
@Tag(name = "Passenger API", description = "Passenger usages")
public class PassengerController {

    private final PassengerUseCase passengerUseCase;
    private final PassengerPresentationMapper passengerPresentationMapper;

    public PassengerController(
            PassengerUseCase passengerUseCase,
            PassengerPresentationMapper passengerPresentationMapper
    ) {
        this.passengerUseCase = passengerUseCase;
        this.passengerPresentationMapper = passengerPresentationMapper;
    }

    @GetMapping
    @Operation(summary = "Get all passengers with pagination", description = "Retrieve a paginated list of all passengers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    })
    public ResponseEntity<Page<PassengerResponse>> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        Page<PassengerResponse> page = passengerUseCase.getAll(pageable)
                .map(passengerPresentationMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID", description = "Retrieve a specific passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved passenger"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<PassengerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                passengerPresentationMapper.toResponse(passengerUseCase.getById(id))
        );
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get passenger by email", description = "Retrieve a specific passenger by its email")
    public ResponseEntity<PassengerResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(
                passengerPresentationMapper.toResponse(passengerUseCase.getByEmail(email))
        );
    }

    @PostMapping
    @Operation(summary = "Create a new passenger", description = "Create a new passenger with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Passenger created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<PassengerResponse> create(
            @Valid @RequestBody PassengerRequest request,
            @RequestHeader(value = "X-Auth-Roles", required = false) String rolesHeader,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ForbiddenException("User email required");
        }
        if (!hasPassengerRole(rolesHeader)) {
            throw new ForbiddenException("Passenger email mismatch");
        }
        PassengerDto dto = passengerPresentationMapper.toDto(request);
        dto.setEmail(userEmail);
        PassengerDto created = passengerUseCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(passengerPresentationMapper.toResponse(created));
    }

    private boolean hasPassengerRole(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return false;
        }
        return hasSupervisorRole(rolesHeader) || Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_PASSENGER"));
    }

    private boolean hasSupervisorRole(String rolesHeader) {
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_SUPERVISOR"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing passenger", description = "Update the details of an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Passenger updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<PassengerResponse> update(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
        PassengerDto updated = passengerUseCase.update(id, passengerPresentationMapper.toDto(request));
        return ResponseEntity.ok(passengerPresentationMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a passenger", description = "Delete an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Passenger deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
