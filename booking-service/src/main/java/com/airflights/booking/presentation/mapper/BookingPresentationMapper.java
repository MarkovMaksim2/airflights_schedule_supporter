package com.airflights.booking.presentation.mapper;

import com.airflights.booking.application.dto.BookingDto;
import com.airflights.booking.presentation.dto.BookingRequest;
import com.airflights.booking.presentation.dto.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingPresentationMapper {
    public BookingDto toDto(BookingRequest request) {
        return new BookingDto(
                null,
                request.getPassengerId(),
                request.getFlightId(),
                null
        );
    }

    public BookingResponse toResponse(BookingDto dto) {
        return new BookingResponse(
                dto.getId(),
                dto.getPassengerId(),
                dto.getFlightId(),
                dto.getBookingTime()
        );
    }
}
