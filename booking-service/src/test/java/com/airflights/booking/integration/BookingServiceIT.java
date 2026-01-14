package com.airflights.booking.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.booking.domain.port.BookingEventPublisher;
import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.dto.PassengerSummary;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.feign.FlightVerifier;
import com.airflights.booking.feign.PassengerVerifier;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
import com.airflights.booking.service.BookingService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BookingServiceIT.TestConfig.class)
class BookingServiceIT {

    @Configuration
    static class TestConfig {
        @Bean
        BookingService bookingService(
                BookingMapper bookingMapper,
                BookingRepository bookingRepository,
                FlightVerifier flightVerifier,
                PassengerVerifier passengerVerifier,
                BookingEventPublisher bookingEventPublisher
        ) {
            return new BookingService(bookingMapper, bookingRepository, flightVerifier, passengerVerifier, bookingEventPublisher);
        }
    }

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private FlightVerifier flightVerifier;

    @MockBean
    private PassengerVerifier passengerVerifier;

    @MockBean
    private BookingEventPublisher bookingEventPublisher;

    @Autowired
    private BookingService bookingService;

    @Test
    void create_withPassengerRole_usesSpringContext() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPassengerId(1L);
        booking.setFlightId(10L);
        booking.setBookingTime(LocalDateTime.now());
        BookingDto bookingDto = new BookingDto(1L, 1L, 10L, booking.getBookingTime());

        when(passengerVerifier.getPassengerByEmail("user@example.com"))
                .thenReturn(new PassengerSummary(1L, "user@example.com"));
        when(bookingMapper.toEntity(any(BookingDto.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.create(bookingDto, "ROLE_PASSENGER", "user@example.com");

        verify(bookingEventPublisher).publishBookingCreated(any());
    }
}
