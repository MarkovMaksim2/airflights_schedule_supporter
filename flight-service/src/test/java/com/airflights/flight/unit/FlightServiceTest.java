package com.airflights.flight.unit;

import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.dto.RestrictedZoneDto;
import com.airflights.flight.application.mapper.FlightMapper;
import com.airflights.flight.application.port.out.AirlineVerifierPort;
import com.airflights.flight.application.port.out.AirportVerifierPort;
import com.airflights.flight.application.port.out.FlightRepository;
import com.airflights.flight.application.service.FlightService;
import com.airflights.flight.domain.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private AirlineVerifierPort airlineVerifier;

    @Mock
    private AirportVerifierPort airportVerifier;

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
        when(flightMapper.toDomain(flightDto)).thenReturn(flight);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);

        flightDto.setStatus("");
        FlightDto res = flightService.create(flightDto, null, null);
        assertNotNull(res);
        assertEquals(flightDto.getId(), res.getId());
        assertEquals("WAITING_APPROVAL", flightDto.getStatus());
        verify(flightRepository).save(flight);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(airportVerifier).ensureAirportExists(2L);
    }

    @Test
    void updateFlightsDueToRestriction_updatesAffectedFlights() {
        // prepare a zone that overlaps with flight departure
        RestrictedZoneDto zone = new RestrictedZoneDto();
        zone.setStartTime(LocalDateTime.now());
        zone.setEndTime(LocalDateTime.now().plusDays(2));

        when(flightRepository.findAll()).thenReturn(List.of(flight));

        flightService.updateFlightsDueToRestriction(zone);

        // flightRepository.save called for the affected flight
        verify(flightRepository, atLeastOnce()).save(any(Flight.class));
        assertEquals("CANCELED", flight.getStatus());
    }
}
