package com.airflights.flight.unit;

import com.airflights.flight.dto.AirlineDto;
import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.entity.Flight;
import com.airflights.flight.feign.AirlineVerifier;
import com.airflights.flight.feign.AirportManagerVerifier;
import com.airflights.flight.feign.AirportVerifier;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import com.airflights.flight.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceRoleTest {

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
        flight = new Flight();
        flight.setId(10L);
        flight.setAirlineId(1L);
        flight.setDepartureAirportId(1L);
        flight.setArrivalAirportId(2L);
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setStatus("WAITING_APPROVAL");

        flightDto = new FlightDto();
        flightDto.setId(10L);
        flightDto.setAirlineId(1L);
        flightDto.setDepartureAirportId(1L);
        flightDto.setArrivalAirportId(2L);
        flightDto.setDepartureTime(flight.getDepartureTime());
        flightDto.setArrivalTime(flight.getArrivalTime());
        flightDto.setStatus("APPROVED");
    }

    @Test
    void create_withAirlineRole_setsWaitingApproval() {
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "airline@example.com"));
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);
        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto created = flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com");

        assertNotNull(created);
        assertEquals("WAITING_APPROVAL", flightDto.getStatus());
        verify(airlineVerifier).getAirline(1L);
        verify(airlineVerifier, never()).ensureAirlineExists(anyLong());
    }

    @Test
    void create_withAirlineRole_missingEmail_throws() {
        assertThrows(ResponseStatusException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", " "));

        verifyNoInteractions(airlineVerifier, airportVerifier, flightRepository);
    }

    @Test
    void create_withAirlineRole_mismatchEmail_throws() {
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "other@example.com"));

        assertThrows(ResponseStatusException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));
        verifyNoInteractions(airportVerifier, flightRepository);
    }

    @Test
    void create_withAirlineRole_missingContactEmail_throws() {
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", null));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));

        assertEquals("Airline access denied", ex.getReason());
        verifyNoInteractions(airportVerifier, flightRepository);
    }

    @Test
    void update_withAirlineRole_mismatchAirline_throws() {
        flightDto.setAirlineId(2L);
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "airline@example.com"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.update(10L, flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));

        assertEquals("Airline change not allowed", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
        verifyNoInteractions(airportVerifier);
    }

    @Test
    void update_withAirlineRole_sameAirline_updates() {
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "airline@example.com"));
        doNothing().when(airportVerifier).ensureAirportExists(1L);
        doNothing().when(airportVerifier).ensureAirportExists(2L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto updated = flightService.update(10L, flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com");

        assertNotNull(updated);
        verify(flightRepository).save(flight);
        verify(airlineVerifier).getAirline(1L);
        verify(airlineVerifier, never()).ensureAirlineExists(anyLong());
    }

    @Test
    void delete_withAirlineRole_mismatchEmail_throws() {
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "other@example.com"));

        assertThrows(ResponseStatusException.class,
                () -> flightService.delete(10L, "ROLE_AIRLINE_COMPANY", "airline@example.com"));
        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    void delete_withAirlineRole_matches_deletes() {
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "airline@example.com"));

        flightService.delete(10L, "ROLE_AIRLINE_COMPANY", "airline@example.com");

        verify(flightRepository).delete(flight);
    }

    @Test
    void approve_whenWaitingApproval_departure_setsApprovedDeparture() {
        flight.setStatus("WAITING_APPROVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.approve(10L, "manager@example.com");

        assertNotNull(result);
        verify(flightRepository).save(argThat(saved -> "APPROVED_DEPARTURE".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedDeparture_arrival_setsApproved() {
        flight.setStatus("APPROVED_DEPARTURE");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedDeparture_departure_keepsStatus() {
        flight.setStatus("APPROVED_DEPARTURE");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED_DEPARTURE".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedArrival_departure_setsApproved() {
        flight.setStatus("APPROVED_ARRIVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedArrival_arrival_keepsStatus() {
        flight.setStatus("APPROVED_ARRIVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED_ARRIVAL".equals(saved.getStatus())));
    }

    @Test
    void approve_whenAlreadyApproved_keepsStatus() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenStatusNull_setsApprovedArrival() {
        flight.setStatus(null);
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approve(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "APPROVED_ARRIVAL".equals(saved.getStatus())));
    }

    @Test
    void approve_whenAirportMismatch_throws() {
        flight.setStatus("WAITING_APPROVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(99L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.approve(10L, "manager@example.com"));

        assertEquals("Airport mismatch", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void approve_whenInvalidStatus_throws() {
        flight.setStatus("UNKNOWN");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.approve(10L, "manager@example.com"));

        assertEquals("Flight not eligible for approval", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void approve_whenMissingEmail_throws() {
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.approve(10L, " "));

        assertEquals("User email required", ex.getReason());
        verifyNoInteractions(airportManagerVerifier);
    }

    @Test
    void depart_whenApproved_departureMatches_setsDeparted() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.depart(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "DEPARTED".equals(saved.getStatus())));
    }

    @Test
    void depart_whenAirportMismatch_throws() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.depart(10L, "manager@example.com"));

        assertEquals("Departure airport mismatch", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void depart_whenNotApproved_throws() {
        flight.setStatus("WAITING_APPROVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.depart(10L, "manager@example.com"));

        assertEquals("Flight is not approved", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void arrive_whenDeparted_arrivalMatches_setsArrived() {
        flight.setStatus("DEPARTED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.arrive(10L, "manager@example.com");

        verify(flightRepository).save(argThat(saved -> "ARRIVED".equals(saved.getStatus())));
    }

    @Test
    void arrive_whenAirportMismatch_throws() {
        flight.setStatus("DEPARTED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.arrive(10L, "manager@example.com"));

        assertEquals("Arrival airport mismatch", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void arrive_whenNotDeparted_throws() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airportManagerVerifier.getAirportIdByEmail("manager@example.com")).thenReturn(2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> flightService.arrive(10L, "manager@example.com"));

        assertEquals("Flight has not departed", ex.getReason());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void delete_whenFlightMissing_throws() {
        when(flightRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> flightService.delete(10L, null, null));
        verify(flightRepository, never()).delete(any(Flight.class));
    }
}
