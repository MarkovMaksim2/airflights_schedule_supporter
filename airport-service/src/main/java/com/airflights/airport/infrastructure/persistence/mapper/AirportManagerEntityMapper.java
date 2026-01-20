package com.airflights.airport.infrastructure.persistence.mapper;

import com.airflights.airport.domain.model.AirportManager;
import com.airflights.airport.infrastructure.persistence.entity.AirportManagerEntity;
import org.springframework.stereotype.Component;

@Component
public class AirportManagerEntityMapper {
    public AirportManager toDomain(AirportManagerEntity entity) {
        return new AirportManager(entity.getId(), entity.getAirportId(), entity.getUserEmail());
    }

    public AirportManagerEntity toEntity(AirportManager airportManager) {
        return new AirportManagerEntity(
                airportManager.getId(),
                airportManager.getAirportId(),
                airportManager.getUserEmail()
        );
    }
}
