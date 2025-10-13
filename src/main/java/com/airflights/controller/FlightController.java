package com.airflights.controller;

import com.airflights.dto.FlightDto;
import com.airflights.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<Page<FlightDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(flightService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FlightDto> create(@Valid @RequestBody FlightDto dto) {
        FlightDto created = flightService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> update(@PathVariable Long id, @Valid @RequestBody FlightDto dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Бесконечная прокрутка без total count — lazy pagination (пример для задания).
     */
    @GetMapping("/scroll")
    public ResponseEntity<Iterable<FlightDto>> infiniteScroll(@RequestParam(required = false, defaultValue = "0") int offset,
                                                              @RequestParam(required = false, defaultValue = "20") int limit) {
        return ResponseEntity.ok(flightService.getInfiniteScroll(offset, limit));
    }
}
