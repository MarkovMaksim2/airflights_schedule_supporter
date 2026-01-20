package com.airflights.flight.unit;

import com.airflights.flight.application.dto.FlightDto;
import com.airflights.flight.application.port.in.FlightUseCase;
import com.airflights.flight.presentation.controller.FlightController;
import com.airflights.flight.presentation.dto.FlightRequest;
import com.airflights.flight.presentation.dto.FlightResponse;
import com.airflights.flight.presentation.dto.RestrictedZoneRequest;
import com.airflights.flight.presentation.mapper.FlightPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightUseCase flightUseCase;

    @InjectMocks
    private FlightController flightController;

    private FlightDto flightDto;
    @Spy
    private FlightPresentationMapper flightPresentationMapper = new FlightPresentationMapper();

    @BeforeEach
    void setUp() {
        flightDto = new FlightDto();
        flightDto.setId(1L);
        flightDto.setAirlineId(1L);
        flightDto.setDepartureAirportId(1L);
        flightDto.setArrivalAirportId(2L);
        flightDto.setDepartureTime(LocalDateTime.now().plusDays(1));
        flightDto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flightDto.setStatus("SCHEDULED");
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FlightDto> page = new PageImpl<>(List.of(flightDto));

        when(flightUseCase.getAll(pageable)).thenReturn(page);

        ResponseEntity<Page<FlightResponse>> response = flightController.getAll(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        verify(flightUseCase).getAll(pageable);
    }

    @Test
    void getAll_withLargePageSize_throws() {
        Pageable pageable = PageRequest.of(0, 100); // больше 50

        assertThrows(IllegalArgumentException.class, () -> flightController.getAll(pageable));
        verify(flightUseCase, never()).getAll(any());
    }

    @Test
    void getById_success() {
        when(flightUseCase.getById(1L)).thenReturn(flightDto);

        ResponseEntity<FlightResponse> response = flightController.getById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightUseCase).getById(1L);
    }

    @Test
    void create_success() {
        when(flightUseCase.create(any(FlightDto.class), any(), any())).thenReturn(flightDto);

        FlightRequest request = new FlightRequest(
                flightDto.getAirlineId(),
                flightDto.getDepartureAirportId(),
                flightDto.getArrivalAirportId(),
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getStatus()
        );
        ResponseEntity<FlightResponse> response = flightController.create(request, null, null);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightUseCase).create(any(FlightDto.class), any(), any());
    }

    @Test
    void update_success() {
        when(flightUseCase.update(1L, any(FlightDto.class), any(), any())).thenReturn(flightDto);

        FlightRequest request = new FlightRequest(
                flightDto.getAirlineId(),
                flightDto.getDepartureAirportId(),
                flightDto.getArrivalAirportId(),
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getStatus()
        );
        ResponseEntity<FlightResponse> response = flightController.update(1L, request, null, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightUseCase).update(eq(1L), any(FlightDto.class), any(), any());
    }

    @Test
    void delete_success() {
        doNothing().when(flightUseCase).delete(1L, null, null);

        ResponseEntity<Void> response = flightController.delete(1L, null, null);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(flightUseCase).delete(1L, null, null);
    }

    @Test
    void infiniteScroll_success() {
        when(flightUseCase.getInfiniteScroll(0, 20)).thenReturn(List.of(flightDto));

        ResponseEntity<Iterable<FlightResponse>> response = flightController.infiniteScroll(0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().iterator().hasNext());
        assertEquals(1L, response.getBody().iterator().next().getId());
        verify(flightUseCase).getInfiniteScroll(0, 20);
    }

    @Test
    void updateDueToRestriction_success() {
        RestrictedZoneRequest request = new RestrictedZoneRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusDays(1));

        doNothing().when(flightUseCase).updateFlightsDueToRestriction(any());

        ResponseEntity<Void> response = flightController.updateDueToRestriction(request);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(flightUseCase).updateFlightsDueToRestriction(any());
    }
}
