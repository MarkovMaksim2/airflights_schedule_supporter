package com.airflights.flight.unit;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.entity.Flight;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import com.airflights.flight.service.FlightService;
import com.airflights.flight.feign.AirlineVerifier;
import com.airflights.flight.feign.AirportManagerVerifier;
import com.airflights.flight.feign.AirportVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceExtendedTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private AirlineVerifier airlineVerifier;

    @Mock
    private AirportVerifier airportVerifier;

    @Mock
    private AirportManagerVerifier airportManagerVerifier;

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
    void getAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));

        when(flightRepository.findAll(pageable)).thenReturn(flightPage);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        Page<FlightDto> result = flightService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(flightRepository).findAll(pageable);
    }

    @Test
    void create_success() {
        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);

        FlightDto res = flightService.create(flightDto, null, null);
        assertNotNull(res);
        assertEquals(flightDto.getId(), res.getId());
        verify(flightRepository).save(flight);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(airportVerifier).ensureAirportExists(2L);
    }

    @Test
    void create_whenAirlineMissing_throws() {
        doThrow(new IllegalArgumentException("Airline not found")).when(airlineVerifier).ensureAirlineExists(1L);

        assertThrows(IllegalArgumentException.class, () -> flightService.create(flightDto, null, null));
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier, never()).ensureAirportExists(anyLong());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void create_whenDepartureAirportMissing_throws() {
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doThrow(new IllegalArgumentException("Airport not found")).when(airportVerifier).ensureAirportExists(1L);

        assertThrows(IllegalArgumentException.class, () -> flightService.create(flightDto, null, null));
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(airportVerifier, never()).ensureAirportExists(2L);
        verify(flightRepository, never()).save(any(Flight.class));
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

    @Test
    void update_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);

        FlightDto result = flightService.update(1L, flightDto, null, null);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(flightRepository).findById(1L);
        verify(flightRepository).save(flight);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(airportVerifier).ensureAirportExists(2L);
    }

    @Test
    void update_whenFlightNotFound_throws() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.update(1L, flightDto, null, null));
        verify(flightRepository).findById(1L);
        verify(flightRepository, never()).save(any(Flight.class));
        verify(airlineVerifier, never()).ensureAirlineExists(anyLong());
        verify(airportVerifier, never()).ensureAirportExists(anyLong());
    }

    @Test
    void getById_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(flightRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_throws() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.getById(1L));
        verify(flightRepository).findById(1L);
    }

    @Test
    void delete_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        flightService.delete(1L, null, null);

        verify(flightRepository).delete(flight);
    }

    @Test
    void getInfiniteScroll_returnsList() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));

        when(flightRepository.findAllByOrderByDepartureTimeAsc(pageable)).thenReturn(flightPage);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        List<FlightDto> result = flightService.getInfiniteScroll(0, 20);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(flightRepository).findAllByOrderByDepartureTimeAsc(pageable);
    }
}
