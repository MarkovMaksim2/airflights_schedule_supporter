package com.airflights.airport.infrastructure.persistence.mapper;

import com.airflights.airport.domain.model.Airport;
import com.airflights.airport.infrastructure.persistence.entity.AirportEntity;
import org.springframework.stereotype.Component;

@Component
public class AirportEntityMapper {
    public Airport toDomain(AirportEntity entity) {
        return new Airport(
                entity.getId(),
                entity.getCode(),
                entity.getCity(),
                entity.getName()
        );
    }

    public AirportEntity toEntity(Airport airport) {
        return new AirportEntity(
                airport.getId(),
                airport.getCode(),
                airport.getCity(),
                airport.getName()
        );
    }
}
