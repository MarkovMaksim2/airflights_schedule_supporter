package com.airflights.passenger.controlller;

import com.airflights.passenger.dto.PassengerDto;
import com.airflights.passenger.service.PassengerService;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Tag(name = "Passenger API", description = "Passenger usages")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    @Operation(summary = "Get all passengers with pagination", description = "Retrieve a paginated list of all passengers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    })
    public ResponseEntity<Page<PassengerDto>> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        return ResponseEntity.ok(passengerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID", description = "Retrieve a specific passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved passenger"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<PassengerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getById(id));
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get passenger by email", description = "Retrieve a specific passenger by its email")
    public ResponseEntity<PassengerDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(passengerService.getByEmail(email));
    }

    @PostMapping
    @Operation(summary = "Create a new passenger", description = "Create a new passenger with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Passenger created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<PassengerDto> create(
            @Valid @RequestBody PassengerDto dto,
            @RequestHeader(value = "X-Auth-Roles", required = false) String rolesHeader,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        if (hasPassengerRole(rolesHeader)) {
            if (userEmail == null || userEmail.isBlank()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User email required");
            }
            if (!userEmail.equalsIgnoreCase(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Passenger email mismatch");
            }
        }
        dto.setEmail(userEmail);
        PassengerDto created = passengerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private boolean hasPassengerRole(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return false;
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_PASSENGER"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing passenger", description = "Update the details of an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Passenger updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<PassengerDto> update(@PathVariable Long id, @Valid @RequestBody PassengerDto dto) {
        return ResponseEntity.ok(passengerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a passenger", description = "Delete an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Passenger deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
