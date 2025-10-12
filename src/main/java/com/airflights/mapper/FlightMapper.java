package com.airflights.mapper;

import com.airflights.dto.FlightDto;
import com.airflights.entity.Airline;
import com.airflights.entity.Airport;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightMapper {
    public FlightDto toDto(Flight flight) {
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

    public Flight toEntity(FlightDto flightDto, Airline airline, Airport departureAirport, Airport arrivalAirport, List<Booking> bookingList) {
        return new Flight(
                flightDto.getId(),
                airline,
                departureAirport,
                arrivalAirport,
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                bookingList,
                flightDto.getStatus()
        );
    }
}
