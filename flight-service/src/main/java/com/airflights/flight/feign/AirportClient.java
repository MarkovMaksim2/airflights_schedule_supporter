package com.airflights.flight.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "airport-service", contextId = "airportClient", path = "/api/airports")
public interface AirportClient {
    @GetMapping("/{id}")
    ResponseEntity<Void> airportExists(@PathVariable("id") Long id);
}
