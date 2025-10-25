package com.airflights.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-service", path = "/api/v1/passenger")
public interface PassengerClient {
    @GetMapping("/{id}")
    ResponseEntity<Void> passengerExists(@PathVariable("id") Long id);
}