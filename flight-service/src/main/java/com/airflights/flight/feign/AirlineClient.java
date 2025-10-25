package com.airflights.flight.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "airline-service", path = "/api/v1/airline")
public interface AirlineClient {
    @GetMapping("/{id}")
    ResponseEntity<Void> airlineExists(@PathVariable("id") Long id);
}