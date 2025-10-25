package com.airflights.passenger.mapper;

import com.airflights.passenger.dto.PassengerDto;
import com.airflights.passenger.entity.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {
    public com.airflights.passenger.dto.PassengerDto toDto(com.airflights.passenger.entity.Passenger passenger) {
        return new com.airflights.passenger.dto.PassengerDto(
                passenger.getId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getEmail(),
                passenger.getPassportNumber()
        );
    }
    public com.airflights.passenger.entity.Passenger toEntity(com.airflights.passenger.dto.PassengerDto passengerDto) {
        return new com.airflights.passenger.entity.Passenger(
                passengerDto.getId(),
                passengerDto.getFirstName(),
                passengerDto.getLastName(),
                passengerDto.getEmail(),
                passengerDto.getPassportNumber()
        );
    }
}
