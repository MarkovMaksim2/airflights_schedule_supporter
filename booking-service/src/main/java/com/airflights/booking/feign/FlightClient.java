package com.airflights.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service", path = "/api/flights")
public interface FlightClient {
    @GetMapping("/{id}")
    ResponseEntity<Void> flightExists(@PathVariable("id") Long id);
}