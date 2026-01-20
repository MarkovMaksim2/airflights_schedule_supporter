package com.airflights.booking.unit;

import com.airflights.booking.application.dto.BookingDto;
import com.airflights.booking.application.dto.PassengerSummary;
import com.airflights.booking.application.event.BookingCreatedEvent;
import com.airflights.booking.application.mapper.BookingMapper;
import com.airflights.booking.application.port.out.BookingEventPublisher;
import com.airflights.booking.application.port.out.BookingRepository;
import com.airflights.booking.application.port.out.FlightVerifierPort;
import com.airflights.booking.application.port.out.PassengerVerifierPort;
import com.airflights.booking.application.service.BookingService;
import com.airflights.booking.domain.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceAdditionalTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private PassengerVerifierPort passengerVerifier;

    @Mock
    private FlightVerifierPort flightVerifier;

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
    void create_success_withoutPassengerRole() {
        when(bookingMapper.toDomain(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        doNothing().when(passengerVerifier).ensurePassengerExists(1L);
        doNothing().when(flightVerifier).ensureFlightExists(10L);
        when(passengerVerifier.getPassengerById(1L)).thenReturn(new PassengerSummary(1L, "user@example.com"));

        BookingDto created = bookingService.create(bookingDto, null, null);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(passengerVerifier).ensurePassengerExists(1L);
        verify(flightVerifier).ensureFlightExists(10L);
        ArgumentCaptor<BookingCreatedEvent> eventCaptor = ArgumentCaptor.forClass(BookingCreatedEvent.class);
        verify(bookingEventPublisher).publishBookingCreated(eventCaptor.capture());
        assertEquals("user@example.com", eventCaptor.getValue().passengerEmail());
    }

    @Test
    void create_withPassengerRole_resolvesPassenger() {
        PassengerSummary passenger = new PassengerSummary(5L, "user@example.com");
        bookingDto.setPassengerId(5L);

        when(passengerVerifier.getPassengerByEmail("user@example.com")).thenReturn(passenger);
        doNothing().when(passengerVerifier).ensurePassengerExists(5L);
        doNothing().when(flightVerifier).ensureFlightExists(10L);
        when(bookingMapper.toDomain(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto created = bookingService.create(bookingDto, "ROLE_PASSENGER", "user@example.com");

        assertNotNull(created);
        verify(bookingRepository).save(argThat(saved -> saved.getPassengerId().equals(5L)));
        verify(passengerVerifier, never()).getPassengerById(anyLong());
        ArgumentCaptor<BookingCreatedEvent> eventCaptor = ArgumentCaptor.forClass(BookingCreatedEvent.class);
        verify(bookingEventPublisher).publishBookingCreated(eventCaptor.capture());
        assertEquals("user@example.com", eventCaptor.getValue().passengerEmail());
    }

    @Test
    void create_withPassengerRole_missingEmail_throws() {
        assertThrows(ResponseStatusException.class,
                () -> bookingService.create(bookingDto, "ROLE_PASSENGER", ""));
        verifyNoInteractions(passengerVerifier, flightVerifier, bookingRepository);
        verifyNoInteractions(bookingEventPublisher);
    }

    @Test
    void create_withPassengerRole_mismatch_throws() {
        PassengerSummary passenger = new PassengerSummary(2L, "user@example.com");
        bookingDto.setPassengerId(1L);
        when(passengerVerifier.getPassengerByEmail("user@example.com")).thenReturn(passenger);

        assertThrows(ResponseStatusException.class,
                () -> bookingService.create(bookingDto, "ROLE_PASSENGER", "user@example.com"));
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(bookingEventPublisher);
    }

    @Test
    void getAll_withPassengerRole_filtersByPassenger() {
        PassengerSummary passenger = new PassengerSummary(2L, "user@example.com");
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> page = new PageImpl<>(List.of(booking), pageable, 1);

        when(passengerVerifier.getPassengerByEmail("user@example.com")).thenReturn(passenger);
        when(bookingRepository.findAllByPassengerId(2L, pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        Page<BookingDto> result = bookingService.getAll(pageable, "ROLE_PASSENGER", "user@example.com");

        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByPassengerId(2L, pageable);
        verify(bookingRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAll_withoutPassengerRole_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> page = new PageImpl<>(List.of(booking), pageable, 1);

        when(bookingRepository.findAll(pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        Page<BookingDto> result = bookingService.getAll(pageable, null, null);

        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAll(pageable);
    }

    @Test
    void getAll_withNonPassengerRole_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> page = new PageImpl<>(List.of(booking), pageable, 1);

        when(bookingRepository.findAll(pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        Page<BookingDto> result = bookingService.getAll(pageable, "ROLE_ADMIN", "admin@example.com");

        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAll(pageable);
        verify(bookingRepository, never()).findAllByPassengerId(any(), any());
    }
}
