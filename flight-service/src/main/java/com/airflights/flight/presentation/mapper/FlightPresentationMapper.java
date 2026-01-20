package com.airflights.flight.presentation.mapper;

import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.dto.RestrictedZoneDto;
import com.airflights.flight.presentation.dto.FlightRequest;
import com.airflights.flight.presentation.dto.FlightResponse;
import com.airflights.flight.presentation.dto.RestrictedZoneRequest;
import org.springframework.stereotype.Component;

@Component
public class FlightPresentationMapper {
    public FlightDto toDto(FlightRequest request) {
        return new FlightDto(
                null,
                request.getAirlineId(),
                request.getDepartureAirportId(),
                request.getArrivalAirportId(),
                request.getDepartureTime(),
                request.getArrivalTime(),
                request.getStatus()
        );
    }

    public FlightResponse toResponse(FlightDto dto) {
        return new FlightResponse(
                dto.getId(),
                dto.getAirlineId(),
                dto.getDepartureAirportId(),
                dto.getArrivalAirportId(),
                dto.getDepartureTime(),
                dto.getArrivalTime(),
                dto.getStatus()
        );
    }

    public RestrictedZoneDto toDto(RestrictedZoneRequest request) {
        return new RestrictedZoneDto(
                null,
                request.getRegion(),
                request.getStartTime(),
                request.getEndTime()
        );
    }
}
