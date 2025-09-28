package com.airflights.mapper;

import com.airflights.dto.AirlineDto;
import com.airflights.entity.Airline;
import com.airflights.entity.Flight;

import java.util.List;

public class AirlineMapper {
    AirlineDto toDto(Airline airline) {
        return new AirlineDto(
                airline.getId(),
                airline.getName(),
                airline.getContactEmail()
        );
    }
    Airline toEntity(AirlineDto airlineDto, List<Flight> flightsList) {
        return new Airline(
                airlineDto.getId(),
                airlineDto.getName(),
                flightsList,
                airlineDto.getContactEmail()
        );
    }
}
