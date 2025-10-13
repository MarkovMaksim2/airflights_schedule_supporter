package com.airflights.unit.service;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.mapper.BookingMapper;
import com.airflights.repository.BookingRepository;
import com.airflights.service.BookingService;
import com.airflights.service.FlightService;
import com.airflights.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PassengerService passengerService;

    @Mock
    private FlightService flightService;

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
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        when(passengerService.getByIdEntity(1L)).thenReturn(passenger);
        when(flightService.getByIdEntity(10L)).thenReturn(flight);

        BookingDto created = bookingService.bookFlight(1L, 10L);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookFlight_whenPassengerMissing_throws() {
        when(passengerService.getByIdEntity(1L)).thenThrow(new IllegalArgumentException("Passenger not found"));

        assertThrows(IllegalArgumentException.class, () -> bookingService.bookFlight(1L, 10L));
    }
}
