package com.airflights.passenger.infrastructure.persistence.mapper;

import com.airflights.passenger.domain.model.Passenger;
import com.airflights.passenger.infrastructure.persistence.entity.PassengerEntity;
import org.springframework.stereotype.Component;

@Component
public class PassengerEntityMapper {
    public Passenger toDomain(PassengerEntity entity) {
        return new Passenger(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassportNumber()
        );
    }

    public PassengerEntity toEntity(Passenger passenger) {
        return new PassengerEntity(
                passenger.getId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getEmail(),
                passenger.getPassportNumber()
        );
    }
}
