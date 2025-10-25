package com.airflights.flight.mapper;

import com.airflights.flight.dto.FlightDto;
import com.airflights.airline.entity.Airline;
import com.airflights.airport.entity.Airport;
import com.airflights.flight.entity.Flight;
import org.springframework.stereotype.Component;


@Component
public class FlightMapper {
    public com.airflights.flight.dto.FlightDto toDto(com.airflights.flight.entity.Flight flight) {
        return new com.airflights.flight.dto.FlightDto(
                flight.getId(),
                flight.getAirline().getId(),
                flight.getDepartureAirport().getId(),
                flight.getArrivalAirport().getId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getStatus()
        );
    }

    public com.airflights.flight.entity.Flight toEntity(com.airflights.flight.dto.FlightDto flightDto, Airline airline, Airport departureAirport, Airport arrivalAirport) {
        return new com.airflights.flight.entity.Flight(
                flightDto.getId(),
                airline,
                departureAirport,
                arrivalAirport,
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getStatus()
        );
    }
}
