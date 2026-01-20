package com.airflights.passenger.application.mapper;

import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.domain.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {
    public PassengerDto toDto(Passenger passenger) {
        return new PassengerDto(
                passenger.getId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getEmail(),
                passenger.getPassportNumber()
        );
    }
    public Passenger toDomain(PassengerDto passengerDto) {
        return new Passenger(
                passengerDto.getId(),
                passengerDto.getFirstName(),
                passengerDto.getLastName(),
                passengerDto.getEmail(),
                passengerDto.getPassportNumber()
        );
    }
}
