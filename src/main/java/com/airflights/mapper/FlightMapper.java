package com.airflights.mapper;

import com.airflights.dto.FlightDto;
import com.airflights.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    @Mapping(source = "airline.id", target = "airlineId")
    @Mapping(source = "departureAirport.id", target = "departureAirportId")
    @Mapping(source = "arrivalAirport.id", target = "arrivalAirportId")
    FlightDto toDto(Flight flight);

    @Mapping(source = "airlineId", target = "airline.id")
    @Mapping(source = "departureAirportId", target = "departureAirport.id")
    @Mapping(source = "arrivalAirportId", target = "arrivalAirport.id")
    Flight toEntity(FlightDto flightDto);
}
