package com.airflights.unit.service;

import com.airflights.dto.FlightDto;
import com.airflights.entity.Airline;
import com.airflights.entity.Airport;
import com.airflights.entity.Flight;
import com.airflights.entity.RestrictedZone;
import com.airflights.mapper.FlightMapper;
import com.airflights.repository.FlightRepository;
import com.airflights.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightService flightService;

    private Flight flight;
    private FlightDto flightDto;

    @BeforeEach
    void setUp() {
        // minimal flight object for unit testing
        flight = new Flight();
        flight.setId(1L);
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setStatus("SCHEDULED");

        flightDto = new FlightDto();
        flightDto.setId(1L);
        flightDto.setDepartureTime(flight.getDepartureTime());
        flightDto.setArrivalTime(flight.getArrivalTime());
        flightDto.setAirlineId(1L);
        flightDto.setDepartureAirportId(1L);
        flightDto.setArrivalAirportId(2L);
        flightDto.setStatus("SCHEDULED");
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        when(flightMapper.toEntity(flightDto, new Airline(), new Airport(), new Airport(), new ArrayList<>())).thenReturn(flight);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto res = flightService.create(flightDto);
        assertNotNull(res);
        assertEquals(flightDto.getId(), res.getId());
        verify(flightRepository).save(flight);
    }

    @Test
    void updateFlightsDueToRestriction_updatesAffectedFlights() {
        // prepare a zone that overlaps with flight departure
        RestrictedZone zone = new RestrictedZone();
        zone.setStartTime(LocalDateTime.now());
        zone.setEndTime(LocalDateTime.now().plusDays(2));

        when(flightRepository.findAll()).thenReturn(List.of(flight));

        flightService.updateFlightsDueToRestriction(zone);

        // flightRepository.save called for the affected flight
        verify(flightRepository, atLeastOnce()).save(any(Flight.class));
    }
}
