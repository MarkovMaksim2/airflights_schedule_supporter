package com.airflights.airline.controller;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.service.AirlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@Tag(name = "Airline API")
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    @Operation(summary = "Get all airlines")
    public Mono<ResponseEntity<Flux<AirlineDto>>> getAll(Pageable pageable) {
        Flux<AirlineDto> airlines = airlineService.getAll(pageable);
        return Mono.just(ResponseEntity.ok(airlines));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airline by ID")
    public Mono<ResponseEntity<AirlineDto>> getById(@PathVariable Long id) {
        return airlineService.getById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    @Operation(summary = "Create new airline")
    public Mono<ResponseEntity<AirlineDto>> create(@Valid @RequestBody AirlineDto dto) {
        return airlineService.create(dto)
                .map(createdDto -> ResponseEntity.status(HttpStatus.CREATED).body(createdDto))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airline")
    public Mono<ResponseEntity<AirlineDto>> update(@PathVariable Long id, @Valid @RequestBody AirlineDto dto) {
        return airlineService.update(id, dto)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airline")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return airlineService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}