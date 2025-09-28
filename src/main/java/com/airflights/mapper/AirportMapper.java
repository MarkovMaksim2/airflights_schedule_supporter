package com.airflights.mapper;

import com.airflights.dto.AirportDto;
import com.airflights.entity.Airport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirportMapper {
    AirportDto toDto(Airport airport);
    Airport toEntity(AirportDto airportDto);
}
