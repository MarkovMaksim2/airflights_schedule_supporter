package com.airflights.airline.presentation.mapper;

import com.airflights.airline.application.dto.AirlineDto;
import com.airflights.airline.presentation.dto.AirlineRequest;
import com.airflights.airline.presentation.dto.AirlineResponse;
import org.springframework.stereotype.Component;

@Component
public class AirlinePresentationMapper {
    public AirlineDto toDto(AirlineRequest request) {
        return new AirlineDto(
                null,
                request.getName(),
                request.getContactEmail()
        );
    }

    public AirlineResponse toResponse(AirlineDto dto) {
        return new AirlineResponse(
                dto.getId(),
                dto.getName(),
                dto.getContactEmail()
        );
    }
}
