package com.airflights.flight.application.port.in;

import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.dto.RestrictedZoneDto;
import com.airflights.flight.application.event.FlightActionRequestedEvent;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightUseCase {
    Page<FlightDto> getAll(Pageable pageable);
    FlightDto create(FlightDto dto, String rolesHeader, String userEmail);
    FlightDto update(Long id, FlightDto dto, String rolesHeader, String userEmail);
    FlightDto getById(Long id);
    void delete(Long id, String rolesHeader, String userEmail);
    List<FlightDto> getInfiniteScroll(int offset, int limit);
    void updateFlightsDueToRestriction(RestrictedZoneDto zone);
    void applyAirportAction(FlightActionRequestedEvent event);
    FlightDto approveByAirport(Long id, Long airportId);
    FlightDto departByAirport(Long id, Long airportId);
    FlightDto arriveByAirport(Long id, Long airportId);
}
