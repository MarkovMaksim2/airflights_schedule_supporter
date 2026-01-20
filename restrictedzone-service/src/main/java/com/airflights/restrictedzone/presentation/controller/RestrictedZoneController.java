package com.airflights.restrictedzone.presentation.controller;

import com.airflights.restrictedzone.application.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.application.port.in.RestrictedZoneUseCase;
import com.airflights.restrictedzone.presentation.dto.RestrictedZoneRequest;
import com.airflights.restrictedzone.presentation.dto.RestrictedZoneResponse;
import com.airflights.restrictedzone.presentation.mapper.RestrictedZonePresentationMapper;
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
@RequestMapping("/api/restricted-zones")
@Tag(name = "Restricted Zone API", description = "Restricted Zone usages")
public class RestrictedZoneController {

    private final RestrictedZoneUseCase restrictedZoneUseCase;
    private final RestrictedZonePresentationMapper restrictedZonePresentationMapper;

    public RestrictedZoneController(
            RestrictedZoneUseCase restrictedZoneUseCase,
            RestrictedZonePresentationMapper restrictedZonePresentationMapper
    ) {
        this.restrictedZoneUseCase = restrictedZoneUseCase;
        this.restrictedZonePresentationMapper = restrictedZonePresentationMapper;
    }

    @GetMapping
    @Operation(summary = "Get all restricted zones with pagination", description = "Retrieve a paginated list of all restricted zones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
    })
    public ResponseEntity<Page<RestrictedZoneResponse>> getAll(@ParameterObject Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size cannot exceed 50. Maximum allowed is 50, but received: " + pageable.getPageSize());
        }
        return ResponseEntity.ok(
                restrictedZoneUseCase.getAll(pageable)
                        .map(restrictedZonePresentationMapper::toResponse)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restricted zone by ID", description = "Retrieve a specific restricted zone by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved restricted zone"),
        @ApiResponse(responseCode = "404", description = "Restricted zone not found"),
    })
    public ResponseEntity<RestrictedZoneResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                restrictedZonePresentationMapper.toResponse(restrictedZoneUseCase.getById(id))
        );
    }

    @PostMapping
    @Operation(summary = "Create a new restricted zone", description = "Create a new restricted zone with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restricted zone created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
    })
    public ResponseEntity<RestrictedZoneResponse> create(@Valid @RequestBody RestrictedZoneRequest request) {
        RestrictedZoneDto created = restrictedZoneUseCase.create(
                restrictedZonePresentationMapper.toDto(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restrictedZonePresentationMapper.toResponse(created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restricted zone", description = "Delete an existing restricted zone by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Restricted zone deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Restricted zone not found"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restrictedZoneUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
