package com.airflights.restrictedzone.feign;

import com.airflights.restrictedzone.dto.RestrictedZoneDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "flight-service",
        path = "/api/flights"
)
public interface FlightClient {

    @PatchMapping("/update-due-to-restriction")
    void updateFlightsDueToRestriction(@RequestBody RestrictedZoneDto restrictedZone);
}
