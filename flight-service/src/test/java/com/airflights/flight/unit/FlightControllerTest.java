package com.airflights.flight.unit;

import com.airflights.flight.controller.FlightController;
import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    private FlightDto flightDto;

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

        when(flightService.getAll(pageable)).thenReturn(page);

        ResponseEntity<Page<FlightDto>> response = flightController.getAll(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        verify(flightService).getAll(pageable);
    }

    @Test
    void getAll_withLargePageSize_throws() {
        Pageable pageable = PageRequest.of(0, 100); // больше 50

        assertThrows(IllegalArgumentException.class, () -> flightController.getAll(pageable));
        verify(flightService, never()).getAll(any());
    }

    @Test
    void getById_success() {
        when(flightService.getById(1L)).thenReturn(flightDto);

        ResponseEntity<FlightDto> response = flightController.getById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightService).getById(1L);
    }

    @Test
    void create_success() {
        when(flightService.create(flightDto)).thenReturn(flightDto);

        ResponseEntity<FlightDto> response = flightController.create(flightDto);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightService).create(flightDto);
    }

    @Test
    void update_success() {
        when(flightService.update(1L, flightDto)).thenReturn(flightDto);

        ResponseEntity<FlightDto> response = flightController.update(1L, flightDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(flightService).update(1L, flightDto);
    }

    @Test
    void delete_success() {
        doNothing().when(flightService).delete(1L);

        ResponseEntity<Void> response = flightController.delete(1L);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(flightService).delete(1L);
    }

    @Test
    void infiniteScroll_success() {
        when(flightService.getInfiniteScroll(0, 20)).thenReturn(List.of(flightDto));

        ResponseEntity<Iterable<FlightDto>> response = flightController.infiniteScroll(0, 20);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().iterator().hasNext());
        assertEquals(1L, response.getBody().iterator().next().getId());
        verify(flightService).getInfiniteScroll(0, 20);
    }

    @Test
    void updateDueToRestriction_success() {
        RestrictedZoneDto zoneDto = new RestrictedZoneDto();
        zoneDto.setStartTime(LocalDateTime.now());
        zoneDto.setEndTime(LocalDateTime.now().plusDays(1));

        doNothing().when(flightService).updateFlightsDueToRestriction(zoneDto);

        ResponseEntity<Void> response = flightController.updateDueToRestriction(zoneDto);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(flightService).updateFlightsDueToRestriction(zoneDto);
    }
}
