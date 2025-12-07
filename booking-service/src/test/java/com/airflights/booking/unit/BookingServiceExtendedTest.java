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
class BookingServiceExtendedTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private FlightVerifier flightVerifier;

    @Mock
    private PassengerVerifier passengerVerifier;

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
        booking.setBookingTime(LocalDateTime.now());

        bookingDto = new BookingDto();
        bookingDto.setId(100L);
        bookingDto.setPassengerId(1L);
        bookingDto.setFlightId(10L);
        bookingDto.setBookingTime(booking.getBookingTime());
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

    @Test
    void create_success() {
        when(bookingMapper.toEntity(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        doNothing().when(passengerVerifier).ensurePassengerExists(1L);
        doNothing().when(flightVerifier).ensureFlightExists(10L);

        BookingDto created = bookingService.create(bookingDto);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(bookingRepository).save(booking);
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier).ensureFlightExists(10L);
    }

    @Test
    void create_whenPassengerMissing_throws() {
        doThrow(new IllegalArgumentException("Passenger not found")).when(passengerVerifier).ensurePassengerExists(1L);

        assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingDto));
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier, never()).ensureFlightExists(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void delete_success() {
        doNothing().when(bookingRepository).deleteById(100L);

        bookingService.delete(100L);

        verify(bookingRepository).deleteById(100L);
    }

    @Test
    void getAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        Page<BookingDto> result = bookingService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(100L, result.getContent().get(0).getId());
        verify(bookingRepository).findAll(pageable);
    }

    @Test
    void getById_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(bookingRepository).findById(100L);
    }

    @Test
    void getById_whenNotFound_throws() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(100L));
        verify(bookingRepository).findById(100L);
    }
}
