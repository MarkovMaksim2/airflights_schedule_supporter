package com.airflights.booking.infrastructure.persistence;

import com.airflights.booking.application.port.out.BookingRepository;
import com.airflights.booking.domain.model.Booking;
import com.airflights.booking.infrastructure.persistence.mapper.BookingEntityMapper;
import com.airflights.booking.infrastructure.persistence.repository.JpaBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements BookingRepository {
    private final JpaBookingRepository bookingRepository;
    private final BookingEntityMapper bookingEntityMapper;

    @Override
    public Booking save(Booking booking) {
        return bookingEntityMapper.toDomain(
                bookingRepository.save(bookingEntityMapper.toEntity(booking))
        );
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingEntityMapper::toDomain);
    }

    @Override
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingEntityMapper::toDomain);
    }

    @Override
    public Page<Booking> findAllByPassengerId(Long passengerId, Pageable pageable) {
        return bookingRepository.findAllByPassengerId(passengerId, pageable)
                .map(bookingEntityMapper::toDomain);
    }

    @Override
    public void delete(Booking booking) {
        bookingRepository.delete(bookingEntityMapper.toEntity(booking));
    }
}
