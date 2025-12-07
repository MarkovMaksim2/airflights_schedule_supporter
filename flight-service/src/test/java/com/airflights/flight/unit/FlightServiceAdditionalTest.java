package com.airflights.flight.unit;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.entity.Flight;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import com.airflights.flight.service.FlightService;
import com.airflights.flight.feign.AirlineVerifier;
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
class FlightServiceAdditionalTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private AirlineVerifier airlineVerifier;

    @Mock
    private AirportVerifier airportVerifier;

    @InjectMocks
    private FlightService flightService;

    private Flight flight;
    private FlightDto flightDto;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setId(1L);
        flight.setAirlineId(1L);
        flight.setDepartureAirportId(1L);
        flight.setArrivalAirportId(2L);
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setStatus("SCHEDULED");

        flightDto = new FlightDto();
        flightDto.setId(1L);
        flightDto.setAirlineId(1L);
        flightDto.setDepartureAirportId(1L);
        flightDto.setArrivalAirportId(2L);
        flightDto.setDepartureTime(flight.getDepartureTime());
        flightDto.setArrivalTime(flight.getArrivalTime());
        flightDto.setStatus("SCHEDULED");
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));
        
        when(flightRepository.findAll(pageable)).thenReturn(flightPage);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        Page<FlightDto> result = flightService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(flightDto, result.getContent().get(0));
        verify(flightRepository).findAll(pageable);
    }

    @Test
    void getById_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.getById(1L);

        assertNotNull(result);
        assertEquals(flightDto, result);
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
        doNothing().when(flightRepository).deleteById(1L);

        flightService.delete(1L);

        verify(flightRepository).deleteById(1L);
    }

    @Test
    void update_success() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);

        FlightDto result = flightService.update(1L, flightDto);

        assertNotNull(result);
        assertEquals(flightDto, result);
        verify(flightRepository).findById(1L);
        verify(flightRepository).save(flight);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(airportVerifier).ensureAirportExists(2L);
    }

    @Test
    void update_whenNotFound_throws() {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.update(1L, flightDto));
        verify(flightRepository).findById(1L);
        verify(flightRepository, never()).save(any(Flight.class));
        verify(airlineVerifier, never()).ensureAirlineExists(anyLong());
        verify(airportVerifier, never()).ensureAirportExists(anyLong());
    }

    @Test
    void update_whenAirlineMissing_throws() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        doThrow(new IllegalArgumentException("Airline not found")).when(airlineVerifier).ensureAirlineExists(1L);

        assertThrows(IllegalArgumentException.class, () -> flightService.update(1L, flightDto));
        verify(flightRepository).findById(1L);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(flightRepository, never()).save(any(Flight.class));
        verify(airportVerifier, never()).ensureAirportExists(anyLong());
    }

    @Test
    void update_whenDepartureAirportMissing_throws() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        doNothing().when(airlineVerifier).ensureAirlineExists(1L);
        doThrow(new IllegalArgumentException("Airport not found")).when(airportVerifier).ensureAirportExists(1L);

        assertThrows(IllegalArgumentException.class, () -> flightService.update(1L, flightDto));
        verify(flightRepository).findById(1L);
        verify(airlineVerifier).ensureAirlineExists(1L);
        verify(airportVerifier).ensureAirportExists(1L);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void getInfiniteScroll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Flight> flightPage = new PageImpl<>(List.of(flight));
        
        when(flightRepository.findAllByOrderByDepartureTimeAsc(pageable)).thenReturn(flightPage);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        List<FlightDto> result = flightService.getInfiniteScroll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flightDto, result.get(0));
        verify(flightRepository).findAllByOrderByDepartureTimeAsc(pageable);
    }

}
