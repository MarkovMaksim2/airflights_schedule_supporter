package com.airflights.booking.repository;

import com.airflights.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findAllByFlightId(Long Id);
    Optional<List<Booking>> findAllByPassengerId(Long Id);
}
