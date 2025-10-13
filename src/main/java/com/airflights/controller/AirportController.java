package com.airflights.controller;

import com.airflights.dto.AirportDto;
import com.airflights.service.AirportService;
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
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    public ResponseEntity<Page<AirportDto>> getAll(Pageable pageable) {
        Page<AirportDto> result = airportService.getAll(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(airportService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AirportDto> create(@Valid @RequestBody AirportDto dto) {
        AirportDto created = airportService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDto> update(@PathVariable Long id, @Valid @RequestBody AirportDto dto) {
        return ResponseEntity.ok(airportService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
