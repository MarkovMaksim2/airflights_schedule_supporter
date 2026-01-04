package com.airflights.flight.feign;

import org.springframework.cloud.openfeign.FeignClient;
import com.airflights.flight.dto.AirlineDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "airline-service", path = "/api/airlines")
public interface AirlineClient {
    @GetMapping("/{id}")
    ResponseEntity<AirlineDto> getAirline(@PathVariable("id") Long id);
}
