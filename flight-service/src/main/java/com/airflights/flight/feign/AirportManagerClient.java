package com.airflights.flight.feign;

import com.airflights.flight.dto.AirportManagerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "airport-service", contextId = "airportManagerClient", path = "/api/airport-managers")
public interface AirportManagerClient {
    @GetMapping("/by-email")
    ResponseEntity<AirportManagerDto> getByEmail(@RequestParam("email") String email);
}
