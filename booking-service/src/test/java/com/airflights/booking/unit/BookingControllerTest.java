package com.airflights.booking.unit;

import com.airflights.booking.controller.BookingController;
import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.service.BookingService;
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
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setPassengerId(1L);
        bookingDto.setFlightId(10L);
        bookingDto.setBookingTime(LocalDateTime.now());
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookingDto> page = new PageImpl<>(List.of(bookingDto));

        when(bookingService.getAll(pageable)).thenReturn(page);

        ResponseEntity<Page<BookingDto>> response = bookingController.getAll(pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        verify(bookingService).getAll(pageable);
    }

    @Test
    void getAll_withLargePageSize_throws() {
        Pageable pageable = PageRequest.of(0, 100); // больше 50

        assertThrows(IllegalArgumentException.class, () -> bookingController.getAll(pageable));
        verify(bookingService, never()).getAll(any());
    }

    @Test
    void getById_success() {
        when(bookingService.getById(1L)).thenReturn(bookingDto);

        ResponseEntity<BookingDto> response = bookingController.getById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingService).getById(1L);
    }

    @Test
    void create_success() {
        when(bookingService.create(bookingDto)).thenReturn(bookingDto);

        ResponseEntity<BookingDto> response = bookingController.create(bookingDto);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingService).create(bookingDto);
    }

    @Test
    void delete_success() {
        doNothing().when(bookingService).delete(1L);

        ResponseEntity<Void> response = bookingController.delete(1L);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(bookingService).delete(1L);
    }
}
