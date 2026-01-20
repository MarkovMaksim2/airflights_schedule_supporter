package com.airflights.flight.infrastructure.feign;

import com.airflights.flight.application.dto.AirlineDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "airline-service", path = "/api/airlines")
public interface AirlineClient {
    @GetMapping("/{id}")
    ResponseEntity<AirlineDto> getAirline(@PathVariable("id") Long id);
}
