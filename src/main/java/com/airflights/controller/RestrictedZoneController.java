package com.airflights.controller;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.service.RestrictedZoneService;
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
public class RestrictedZoneController {

    private final RestrictedZoneService restrictedZoneService;

    @GetMapping
    public ResponseEntity<Page<RestrictedZoneDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(restrictedZoneService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestrictedZoneDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restrictedZoneService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RestrictedZoneDto> create(@Valid @RequestBody RestrictedZoneDto dto) {
        RestrictedZoneDto created = restrictedZoneService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restrictedZoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
