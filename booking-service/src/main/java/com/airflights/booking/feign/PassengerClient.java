package com.airflights.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import com.airflights.booking.dto.PassengerSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "passenger-service", path = "/api/passengers")
public interface PassengerClient {
    @GetMapping("/{id}")
    ResponseEntity<PassengerSummary> getById(@PathVariable("id") Long id);

    @GetMapping("/by-email")
    ResponseEntity<PassengerSummary> getByEmail(@RequestParam("email") String email);
}
