package com.airflights.airport.application.mapper;

import com.airflights.airport.application.dto.AirportManagerDto;
import com.airflights.airport.domain.model.AirportManager;
import org.springframework.stereotype.Component;

@Component
public class AirportManagerMapper {
    public AirportManagerDto toDto(AirportManager entity) {
        return new AirportManagerDto(entity.getId(), entity.getAirportId(), entity.getUserEmail());
    }

    public AirportManager toDomain(AirportManagerDto dto) {
        return new AirportManager(dto.getId(), dto.getAirportId(), dto.getUserEmail());
    }
}
