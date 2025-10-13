package com.airflights.controller;

import com.airflights.dto.AirlineDto;
import com.airflights.service.AirlineService;
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
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    public ResponseEntity<Page<AirlineDto>> getAll(Pageable pageable) {
        Page<AirlineDto> result = airlineService.getAll(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(airlineService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AirlineDto> create(@Valid @RequestBody AirlineDto dto) {
        AirlineDto created = airlineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineDto> update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return ResponseEntity.ok(airlineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airlineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
