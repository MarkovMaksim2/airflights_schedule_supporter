package com.airflights.booking.mapper;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
import com.airflights.flight.entity.Flight;
import com.airflights.passenger.entity.Passenger;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getPassenger().getId(),
                booking.getFlight().getId(),
                booking.getBookingTime()
        );
    }

    public Booking toEntity(BookingDto bookingDto, Passenger passenger, Flight flight) {
        return new Booking(
                bookingDto.getId(),
                passenger,
                flight,
                bookingDto.getBookingTime()
        );
    }
}
