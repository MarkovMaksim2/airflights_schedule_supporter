package com.airflights.unit.service;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.mapper.BookingMapper;
import com.airflights.repository.BookingRepository;
import com.airflights.repository.FlightRepository;
import com.airflights.repository.PassengerRepository;
import com.airflights.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private Passenger passenger;
    private Flight flight;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("Egor");
        passenger.setLastName("Aganin");

        flight = new Flight();
        flight.setId(10L);
        flight.setStatus("SCHEDULED");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));

        booking = new Booking();
        booking.setId(100L);
        booking.setPassenger(passenger);
        booking.setFlight(flight);

        bookingDto = new BookingDto();
        bookingDto.setId(100L);
        bookingDto.setPassengerId(1L);
        bookingDto.setFlightId(10L);
    }

    @Test
    void bookFlight_success() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto created = bookingService.bookFlight(1L, 10L);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookFlight_whenPassengerMissing_throws() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookingService.bookFlight(1L, 10L));
    }
}
