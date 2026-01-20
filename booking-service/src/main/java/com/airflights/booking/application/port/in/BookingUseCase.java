package com.airflights.booking.application.port.in;

import com.airflights.booking.application.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingUseCase {
    BookingDto bookFlight(Long passengerId, Long flightId);
    BookingDto create(BookingDto dto, String rolesHeader, String userEmail);
    void delete(Long id, String rolesHeader, String userEmail);
    Page<BookingDto> getAll(Pageable pageable, String rolesHeader, String userEmail);
    BookingDto getById(Long id, String rolesHeader, String userEmail);
}
