package com.airflights.mapper;

import com.airflights.dto.AirportDto;
import com.airflights.entity.Airport;
import com.airflights.entity.Flight;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public class AirportMapper {
    public AirportDto toDto(Airport airport) {
        return new AirportDto(
                airport.getId(),
                airport.getName(),
                airport.getCode(),
                airport.getCity()
        );
    }
    public Airport toEntity(AirportDto airportDto, List<Flight> departures, List<Flight> arrivals) {
        return new Airport(
                airportDto.getId(),
                airportDto.getCode(),
                airportDto.getCity(),
                departures,
                arrivals,
                airportDto.getName()
        );
    }
}
