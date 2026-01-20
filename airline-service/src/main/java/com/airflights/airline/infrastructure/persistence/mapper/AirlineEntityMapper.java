package com.airflights.airline.infrastructure.persistence.mapper;

import com.airflights.airline.domain.model.Airline;
import com.airflights.airline.infrastructure.persistence.entity.AirlineEntity;
import org.springframework.stereotype.Component;

@Component
public class AirlineEntityMapper {
    public Airline toDomain(AirlineEntity entity) {
        return new Airline(
                entity.getId(),
                entity.getName(),
                entity.getContactEmail()
        );
    }

    public AirlineEntity toEntity(Airline airline) {
        return new AirlineEntity(
                airline.getId(),
                airline.getName(),
                airline.getContactEmail()
        );
    }
}
