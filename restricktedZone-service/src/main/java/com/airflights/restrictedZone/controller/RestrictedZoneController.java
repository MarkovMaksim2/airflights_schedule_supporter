package com.airflights.controller;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.service.RestrictedZoneService;
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
@RequestMapping("/api/restricted-zones")
@RequiredArgsConstructor
@Tag(name = "Restricted Zone API", description = "Restricted Zone usages")
public class RestrictedZoneController {

    private final RestrictedZoneService restrictedZoneService;

    @GetMapping
    @Operation(summary = "Get all restricted zones with pagination", description = "Retrieve a paginated list of all restricted zones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<RestrictedZoneDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(restrictedZoneService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restricted zone by ID", description = "Retrieve a specific restricted zone by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved restricted zone"),
        @ApiResponse(responseCode = "404", description = "Restricted zone not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RestrictedZoneDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restrictedZoneService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new restricted zone", description = "Create a new restricted zone with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restricted zone created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RestrictedZoneDto> create(@Valid @RequestBody RestrictedZoneDto dto) {
        RestrictedZoneDto created = restrictedZoneService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restricted zone", description = "Delete an existing restricted zone by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Restricted zone deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Restricted zone not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restrictedZoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
