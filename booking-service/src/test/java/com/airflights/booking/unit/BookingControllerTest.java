package com.airflights.booking.unit;

import com.airflights.booking.application.dto.BookingDto;
import com.airflights.booking.application.port.in.BookingUseCase;
import com.airflights.booking.presentation.controller.BookingController;
import com.airflights.booking.presentation.dto.BookingRequest;
import com.airflights.booking.presentation.dto.BookingResponse;
import com.airflights.booking.presentation.mapper.BookingPresentationMapper;
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
class BookingControllerTest {

    @Mock
    private BookingUseCase bookingUseCase;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;
    @Spy
    private BookingPresentationMapper bookingPresentationMapper = new BookingPresentationMapper();

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

        when(bookingUseCase.getAll(pageable, null, null)).thenReturn(page);

        ResponseEntity<Page<BookingResponse>> response = bookingController.getAll(pageable, null, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1L, response.getBody().getContent().get(0).getId());
        assertEquals("1", response.getHeaders().getFirst("X-Total-Count"));
        verify(bookingUseCase).getAll(pageable, null, null);
    }

    @Test
    void getAll_withLargePageSize_throws() {
        Pageable pageable = PageRequest.of(0, 100); // больше 50

        assertThrows(IllegalArgumentException.class, () -> bookingController.getAll(pageable, null, null));
        verify(bookingUseCase, never()).getAll(any(), any(), any());
    }

    @Test
    void getById_success() {
        when(bookingUseCase.getById(1L, null, null)).thenReturn(bookingDto);

        ResponseEntity<BookingResponse> response = bookingController.getById(1L, null, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingUseCase).getById(1L, null, null);
    }

    @Test
    void create_success() {
        when(bookingUseCase.create(any(BookingDto.class), any(), any())).thenReturn(bookingDto);

        BookingRequest request = new BookingRequest(1L, 10L);
        ResponseEntity<BookingResponse> response = bookingController.create(request, null, null);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingUseCase).create(any(BookingDto.class), any(), any());
    }

    @Test
    void delete_success() {
        doNothing().when(bookingUseCase).delete(1L, null, null);

        ResponseEntity<Void> response = bookingController.delete(1L, null, null);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(bookingUseCase).delete(1L, null, null);
    }
}
