package com.airflights.flight.application.mapper;

import org.springframework.stereotype.Component;
import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.domain.model.Flight;

@Component
public class FlightMapper {
    public FlightDto toDto(Flight flight) {
        return new FlightDto(
                flight.getId(),
                flight.getAirlineId(),
                flight.getDepartureAirportId(),
                flight.getArrivalAirportId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getStatus()
        );
    }

    public Flight toDomain(FlightDto flightDto) {
        return new Flight(
                flightDto.getId(),
                flightDto.getAirlineId(),
                flightDto.getDepartureAirportId(),
                flightDto.getArrivalAirportId(),
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getStatus()
        );
    }
}
