package com.airflights.airline.application.mapper;

import com.airflights.airline.application.dto.AirlineDto;
import com.airflights.airline.domain.model.Airline;
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

    public Airline toDomain(AirlineDto airlineDto) {
        return new Airline(
                airlineDto.getId(),
                airlineDto.getName(),
                airlineDto.getContactEmail()
        );
    }
}
