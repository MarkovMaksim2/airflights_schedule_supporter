package com.airflights.controller;

import com.airflights.dto.PassengerDto;
import com.airflights.service.PassengerService;
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
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<Page<PassengerDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(passengerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PassengerDto> create(@Valid @RequestBody PassengerDto dto) {
        PassengerDto created = passengerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> update(@PathVariable Long id, @Valid @RequestBody PassengerDto dto) {
        return ResponseEntity.ok(passengerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
