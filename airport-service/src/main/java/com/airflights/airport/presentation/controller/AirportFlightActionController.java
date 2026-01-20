package com.airflights.airport.presentation.controller;

import com.airflights.airport.application.port.in.FlightActionUseCase;
import com.airflights.airport.domain.model.FlightAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flight Actions API", description = "Airport-driven flight actions")
public class AirportFlightActionController {

    private final FlightActionUseCase flightActionUseCase;

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Request flight approval", description = "Request approval of a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Approval request accepted"),
            @ApiResponse(responseCode = "404", description = "Airport manager not found")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> approve(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        return flightActionUseCase.requestFlightAction(id, FlightAction.APPROVE, userEmail);
    }

    @PatchMapping("/{id}/depart")
    @Operation(summary = "Request flight departure", description = "Request departure of a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Departure request accepted"),
            @ApiResponse(responseCode = "404", description = "Airport manager not found")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> depart(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        return flightActionUseCase.requestFlightAction(id, FlightAction.DEPART, userEmail);
    }

    @PatchMapping("/{id}/arrive")
    @Operation(summary = "Request flight arrival", description = "Request arrival of a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Arrival request accepted"),
            @ApiResponse(responseCode = "404", description = "Airport manager not found")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> arrive(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Email", required = false) String userEmail
    ) {
        return flightActionUseCase.requestFlightAction(id, FlightAction.ARRIVE, userEmail);
    }
}
