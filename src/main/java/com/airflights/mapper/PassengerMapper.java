package com.airflights.mapper;

import com.airflights.dto.BookingDto;
import com.airflights.dto.PassengerDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Passenger;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public Passenger toEntity(PassengerDto passengerDto, List<Booking> bookings) {
        return new Passenger(
                passengerDto.getId(),
                passengerDto.getFirstName(),
                passengerDto.getLastName(),
                passengerDto.getEmail(),
                passengerDto.getPassportNumber(),
                bookings
        );
    }
}
