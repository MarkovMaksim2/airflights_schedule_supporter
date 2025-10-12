package com.airflights.mapper;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
