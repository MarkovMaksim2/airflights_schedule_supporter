package com.airflights.airline.controller;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.service.AirlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@Tag(name = "Airline API")
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    @Operation(summary = "Get all airlines")
    public ResponseEntity<Page<AirlineDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(airlineService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airline by ID")
    public ResponseEntity<AirlineDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(airlineService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new airline")
    public ResponseEntity<AirlineDto> create(@Valid @RequestBody AirlineDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(airlineService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airline")
    public ResponseEntity<AirlineDto> update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return ResponseEntity.ok(airlineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airline")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airlineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
