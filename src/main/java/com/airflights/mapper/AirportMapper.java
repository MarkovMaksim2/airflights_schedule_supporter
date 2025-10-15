package com.airflights.mapper;

import com.airflights.dto.AirportDto;
import com.airflights.entity.Airport;
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
