package com.airflights.booking.unit;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
import com.airflights.booking.service.BookingService;
import com.airflights.booking.feign.FlightVerifier;
import com.airflights.booking.feign.PassengerVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PassengerVerifier passengerVerifier;

    @Mock
    private FlightVerifier flightVerifier;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(100L);
        booking.setPassengerId(1L);
        booking.setFlightId(10L);

        bookingDto = new BookingDto();
        bookingDto.setId(100L);
        bookingDto.setPassengerId(1L);
        bookingDto.setFlightId(10L);
    }

    @Test
    void bookFlight_success() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        doNothing().when(passengerVerifier).ensurePassengerExists(1L);
        doNothing().when(flightVerifier).ensureFlightExists(10L);

        BookingDto created = bookingService.bookFlight(1L, 10L);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(bookingRepository).save(any(Booking.class));
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier).ensureFlightExists(10L);
    }

    @Test
    void bookFlight_whenPassengerMissing_throws() {
        doThrow(new IllegalArgumentException("Passenger not found")).when(passengerVerifier).ensurePassengerExists(1L);

        assertThrows(IllegalArgumentException.class, () -> bookingService.bookFlight(1L, 10L));
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier, never()).ensureFlightExists(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookFlight_whenFlightMissing_throws() {
        doNothing().when(passengerVerifier).ensurePassengerExists(1L);
        doThrow(new IllegalArgumentException("Flight not found")).when(flightVerifier).ensureFlightExists(10L);

        assertThrows(IllegalArgumentException.class, () -> bookingService.bookFlight(1L, 10L));
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier).ensureFlightExists(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
