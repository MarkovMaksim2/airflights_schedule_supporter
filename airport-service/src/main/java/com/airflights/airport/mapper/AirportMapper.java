package com.airflights.airport.mapper;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.entity.Airport;
import org.springframework.stereotype.Component;

@Component
public class AirportMapper {
    public AirportDto toDto(Airport airport) {
        return new AirportDto(
                airport.getId(),
                airport.getName(),
                airport.getCode(),
                airport.getCity()
        );
    }
    public Airport toEntity(AirportDto airportDto) {
        return new Airport(
                airportDto.getId(),
                airportDto.getCode(),
                airportDto.getCity(),
                airportDto.getName()
        );
    }
}
