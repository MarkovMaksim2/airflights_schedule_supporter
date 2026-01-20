package com.airflights.booking.application.mapper;

import com.airflights.booking.application.dto.BookingDto;
import com.airflights.booking.domain.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getPassengerId(),
                booking.getFlightId(),
                booking.getBookingTime()
        );
    }

    public Booking toDomain(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getPassengerId(),
                bookingDto.getFlightId(),
                bookingDto.getBookingTime()
        );
    }
}
