package com.airflights.mapper;

import com.airflights.dto.AirlineDto;
import com.airflights.entity.Airline;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirlineMapper {
    AirlineDto toDto(Airline airline);
    Airline toEntity(AirlineDto airlineDto);
}
