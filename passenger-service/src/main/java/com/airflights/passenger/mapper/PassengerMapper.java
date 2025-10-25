package com.airflights.passenger.mapper;

import com.airflights.passenger.dto.PassengerDto;
import com.airflights.passenger.entity.Passenger;
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
    public Passenger toEntity(PassengerDto passengerDto) {
        return new Passenger(
                passengerDto.getId(),
                passengerDto.getFirstName(),
                passengerDto.getLastName(),
                passengerDto.getEmail(),
                passengerDto.getPassportNumber()
        );
    }
}
