package com.airflights.booking.application.port.out;

import com.airflights.booking.domain.model.Booking;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    Page<Booking> findAll(Pageable pageable);
    Page<Booking> findAllByPassengerId(Long passengerId, Pageable pageable);
    void delete(Booking booking);
}
