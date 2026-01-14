package com.airflights.booking.unit;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.dto.PassengerSummary;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.feign.FlightVerifier;
import com.airflights.booking.feign.PassengerVerifier;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
import com.airflights.booking.service.BookingService;
import com.airflights.booking.domain.port.BookingEventPublisher;
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

    @Mock
    private BookingEventPublisher bookingEventPublisher;

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
    void getById_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(100L, null, null);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(bookingRepository).findById(100L);
    }

    @Test
    void getById_whenNotFound_throws() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(100L, null, null));
        verify(bookingRepository).findById(100L);
    }

    @Test
    void getById_withPassengerRole_mismatch_throws() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(passengerVerifier.getPassengerByEmail("user@example.com"))
                .thenReturn(new PassengerSummary(2L, "user@example.com"));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.getById(100L, "ROLE_PASSENGER", "user@example.com"));
        verify(bookingMapper, never()).toDto(any(Booking.class));
    }

    @Test
    void getById_withPassengerRole_match_returnsDto() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(passengerVerifier.getPassengerByEmail("user@example.com"))
                .thenReturn(new PassengerSummary(1L, "user@example.com"));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(100L, "ROLE_PASSENGER", "user@example.com");

        assertNotNull(result);
        verify(passengerVerifier).getPassengerByEmail("user@example.com");
        verify(bookingMapper).toDto(booking);
    }

    @Test
    void delete_success_withoutPassengerRole() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        bookingService.delete(100L, null, null);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void delete_whenNotFound_throws() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.delete(100L, null, null));
        verify(bookingRepository, never()).delete(any(Booking.class));
    }

    @Test
    void delete_withPassengerRole_mismatch_throws() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(passengerVerifier.getPassengerByEmail("user@example.com"))
                .thenReturn(new PassengerSummary(2L, "user@example.com"));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.delete(100L, "ROLE_PASSENGER", "user@example.com"));
        verify(bookingRepository, never()).delete(any(Booking.class));
    }

    @Test
    void delete_withPassengerRole_match_deletes() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(passengerVerifier.getPassengerByEmail("user@example.com"))
                .thenReturn(new PassengerSummary(1L, "user@example.com"));

        bookingService.delete(100L, "ROLE_PASSENGER", "user@example.com");

        verify(bookingRepository).delete(booking);
    }
}
