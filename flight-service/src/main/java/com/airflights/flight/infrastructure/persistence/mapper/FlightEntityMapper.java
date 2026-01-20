package com.airflights.flight.infrastructure.persistence.mapper;

import com.airflights.flight.domain.model.Flight;
import com.airflights.flight.infrastructure.persistence.entity.FlightEntity;
import org.springframework.stereotype.Component;

@Component
public class FlightEntityMapper {
    public Flight toDomain(FlightEntity entity) {
        return new Flight(
                entity.getId(),
                entity.getAirlineId(),
                entity.getDepartureAirportId(),
                entity.getArrivalAirportId(),
                entity.getDepartureTime(),
                entity.getArrivalTime(),
                entity.getStatus()
        );
    }

    public FlightEntity toEntity(Flight flight) {
        return new FlightEntity(
                flight.getId(),
                flight.getAirlineId(),
                flight.getDepartureAirportId(),
                flight.getArrivalAirportId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getStatus()
        );
    }
}
