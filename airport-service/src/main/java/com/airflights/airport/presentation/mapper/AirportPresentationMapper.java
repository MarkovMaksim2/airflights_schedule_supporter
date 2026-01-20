package com.airflights.airport.presentation.mapper;

import com.airflights.airport.application.dto.AirportDto;
import com.airflights.airport.presentation.dto.AirportRequest;
import com.airflights.airport.presentation.dto.AirportResponse;
import org.springframework.stereotype.Component;

@Component
public class AirportPresentationMapper {
    public AirportDto toDto(AirportRequest request) {
        return new AirportDto(
                null,
                request.getName(),
                request.getCode(),
                request.getCity()
        );
    }

    public AirportResponse toResponse(AirportDto dto) {
        return new AirportResponse(
                dto.getId(),
                dto.getName(),
                dto.getCode(),
                dto.getCity()
        );
    }
}
