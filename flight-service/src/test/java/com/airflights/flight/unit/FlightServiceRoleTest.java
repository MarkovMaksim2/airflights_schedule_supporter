package com.airflights.flight.unit;

import com.airflights.flight.application.dto.AirlineDto;
import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.exception.BadRequestException;
import com.airflights.flight.application.exception.ForbiddenException;
import com.airflights.flight.application.mapper.FlightMapper;
import com.airflights.flight.application.port.out.AirlineVerifierPort;
import com.airflights.flight.application.port.out.AirportVerifierPort;
import com.airflights.flight.application.port.out.FlightRepository;
import com.airflights.flight.application.service.FlightService;
import com.airflights.flight.domain.model.Flight;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private AirlineVerifierPort airlineVerifier;

    @Mock
    private AirportVerifierPort airportVerifier;

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
        when(flightMapper.toDomain(flightDto)).thenReturn(flight);
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
        assertThrows(ForbiddenException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", " "));

        verifyNoInteractions(airlineVerifier, airportVerifier, flightRepository);
    }

    @Test
    void create_withAirlineRole_mismatchEmail_throws() {
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "other@example.com"));

        assertThrows(ForbiddenException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));
        verifyNoInteractions(airportVerifier, flightRepository);
    }

    @Test
    void create_withAirlineRole_missingContactEmail_throws() {
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", null));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> flightService.create(flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));

        assertEquals("Airline access denied", ex.getMessage());
        verifyNoInteractions(airportVerifier, flightRepository);
    }

    @Test
    void update_withAirlineRole_mismatchAirline_throws() {
        flightDto.setAirlineId(2L);
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(airlineVerifier.getAirline(1L))
                .thenReturn(new AirlineDto(1L, "Air", "airline@example.com"));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> flightService.update(10L, flightDto, "ROLE_AIRLINE_COMPANY", "airline@example.com"));

        assertEquals("Airline change not allowed", ex.getMessage());
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

        assertThrows(ForbiddenException.class,
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
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.approveByAirport(10L, 1L);

        assertNotNull(result);
        verify(flightRepository).save(argThat(saved -> "APPROVED_DEPARTURE".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedDeparture_arrival_setsApproved() {
        flight.setStatus("APPROVED_DEPARTURE");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 2L);

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedDeparture_departure_keepsStatus() {
        flight.setStatus("APPROVED_DEPARTURE");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 1L);

        verify(flightRepository).save(argThat(saved -> "APPROVED_DEPARTURE".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedArrival_departure_setsApproved() {
        flight.setStatus("APPROVED_ARRIVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 1L);

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenApprovedArrival_arrival_keepsStatus() {
        flight.setStatus("APPROVED_ARRIVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 2L);

        verify(flightRepository).save(argThat(saved -> "APPROVED_ARRIVAL".equals(saved.getStatus())));
    }

    @Test
    void approve_whenAlreadyApproved_keepsStatus() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 1L);

        verify(flightRepository).save(argThat(saved -> "APPROVED".equals(saved.getStatus())));
    }

    @Test
    void approve_whenStatusNull_setsApprovedArrival() {
        flight.setStatus(null);
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.approveByAirport(10L, 2L);

        verify(flightRepository).save(argThat(saved -> "APPROVED_ARRIVAL".equals(saved.getStatus())));
    }

    @Test
    void approve_whenAirportMismatch_throws() {
        flight.setStatus("WAITING_APPROVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> flightService.approveByAirport(10L, 99L));

        assertEquals("Airport mismatch", ex.getMessage());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void approve_whenInvalidStatus_throws() {
        flight.setStatus("UNKNOWN");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> flightService.approveByAirport(10L, 1L));

        assertEquals("Flight not eligible for approval", ex.getMessage());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void depart_whenApproved_departureMatches_setsDeparted() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.departByAirport(10L, 1L);

        verify(flightRepository).save(argThat(saved -> "DEPARTED".equals(saved.getStatus())));
    }

    @Test
    void depart_whenAirportMismatch_throws() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> flightService.departByAirport(10L, 2L));

        assertEquals("Departure airport mismatch", ex.getMessage());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void depart_whenNotApproved_throws() {
        flight.setStatus("WAITING_APPROVAL");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> flightService.departByAirport(10L, 1L));

        assertEquals("Flight is not approved", ex.getMessage());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void arrive_whenDeparted_arrivalMatches_setsArrived() {
        flight.setStatus("DEPARTED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.arriveByAirport(10L, 2L);

        verify(flightRepository).save(argThat(saved -> "ARRIVED".equals(saved.getStatus())));
    }

    @Test
    void arrive_whenAirportMismatch_throws() {
        flight.setStatus("DEPARTED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> flightService.arriveByAirport(10L, 1L));

        assertEquals("Arrival airport mismatch", ex.getMessage());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void arrive_whenNotDeparted_throws() {
        flight.setStatus("APPROVED");
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> flightService.arriveByAirport(10L, 2L));

        assertEquals("Flight has not departed", ex.getMessage());
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
