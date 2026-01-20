package com.airflights.airport.presentation.mapper;

import com.airflights.airport.application.dto.AirportManagerDto;
import com.airflights.airport.presentation.dto.AirportManagerRequest;
import com.airflights.airport.presentation.dto.AirportManagerResponse;
import org.springframework.stereotype.Component;

@Component
public class AirportManagerPresentationMapper {
    public AirportManagerDto toDto(AirportManagerRequest request) {
        return new AirportManagerDto(
                null,
                request.getAirportId(),
                request.getUserEmail()
        );
    }

    public AirportManagerResponse toResponse(AirportManagerDto dto) {
        return new AirportManagerResponse(
                dto.getId(),
                dto.getAirportId(),
                dto.getUserEmail()
        );
    }
}
