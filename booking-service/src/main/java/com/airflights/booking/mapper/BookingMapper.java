package com.airflights.booking.mapper;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
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

    public Booking toEntity(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getPassengerId(),
                bookingDto.getFlightId(),
                bookingDto.getBookingTime()
        );
    }
}
