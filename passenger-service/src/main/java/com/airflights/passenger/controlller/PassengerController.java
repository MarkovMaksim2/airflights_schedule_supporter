package com.airflights.passenger.controlller;

import com.airflights.passenger.dto.PassengerDto;
import com.airflights.passenger.service.PassengerService;
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
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<PassengerDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(passengerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID", description = "Retrieve a specific passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved passenger"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PassengerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new passenger", description = "Create a new passenger with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Passenger created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PassengerDto> create(@Valid @RequestBody PassengerDto dto) {
        PassengerDto created = passengerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing passenger", description = "Update the details of an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Passenger updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PassengerDto> update(@PathVariable Long id, @Valid @RequestBody PassengerDto dto) {
        return ResponseEntity.ok(passengerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a passenger", description = "Delete an existing passenger by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Passenger deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
