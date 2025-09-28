package com.airflights.mapper;

import com.airflights.dto.FlightDto;
import com.airflights.entity.Airline;
import com.airflights.entity.Airport;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public class FlightMapper {
    FlightDto toDto(Flight flight) {
        return new FlightDto(
                flight.getId(),
                flight.getAirline().getId(),
                flight.getDepartureAirport().getId(),
                flight.getArrivalAirport().getId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getStatus()
        );
    }

    Flight toEntity(FlightDto flightDto, Airline airline, Airport departureAirport, Airport arrivalAirport, List<Booking> bookingsList) {
        return new Flight(
                flightDto.getId(),
                airline,
                departureAirport,
                arrivalAirport,
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                bookingsList,
                flightDto.getStatus()
        );
    }
}
