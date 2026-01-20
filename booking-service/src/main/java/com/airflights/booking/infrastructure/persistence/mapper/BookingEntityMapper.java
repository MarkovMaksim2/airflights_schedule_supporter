package com.airflights.booking.infrastructure.persistence.mapper;

import com.airflights.booking.domain.model.Booking;
import com.airflights.booking.infrastructure.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingEntityMapper {
    public Booking toDomain(BookingEntity entity) {
        return new Booking(
                entity.getId(),
                entity.getPassengerId(),
                entity.getFlightId(),
                entity.getBookingTime()
        );
    }

    public BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.getId(),
                booking.getPassengerId(),
                booking.getFlightId(),
                booking.getBookingTime()
        );
    }
}
