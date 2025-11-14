package com.airflights.airline.mapper;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.entity.Airline;
import org.springframework.stereotype.Component;

@Component
public class AirlineMapper {
    public AirlineDto toDto(Airline airline) {
        return new AirlineDto(
                airline.getId(),
                airline.getName(),
                airline.getContactEmail()
        );
    }
    public Airline toEntity(AirlineDto airlineDto) {
        return new Airline(
                airlineDto.getId(),
                airlineDto.getName(),
                airlineDto.getContactEmail()
        );
    }
}
