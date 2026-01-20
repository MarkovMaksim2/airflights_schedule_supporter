package com.airflights.booking.infrastructure.persistence.repository;

import com.airflights.booking.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookingRepository extends JpaRepository<BookingEntity, Long> {
    Page<BookingEntity> findAllByPassengerId(Long passengerId, Pageable pageable);
}
