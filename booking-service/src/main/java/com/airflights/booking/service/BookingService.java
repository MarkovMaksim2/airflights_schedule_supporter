package com.airflights.booking.service;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.feign.FlightClient;
import com.airflights.booking.feign.PassengerClient;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final FlightClient flightClient;
    private final PassengerClient passengerClient;

    @Transactional
    public BookingDto bookFlight(Long passengerId, Long flightId) {
        ensurePassengerExists(passengerId);
        ensureFlightExists(flightId);
        Booking booking = new Booking();
        booking.setPassengerId(passengerId);
        booking.setFlightId(flightId);
        booking.setBookingTime(LocalDateTime.now());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto create(@Valid BookingDto dto) {
        ensurePassengerExists(dto.getPassengerId());
        ensureFlightExists(dto.getFlightId());

        Booking booking = bookingMapper.toEntity(dto);
        booking.setBookingTime(LocalDateTime.now());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    public Page<BookingDto> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    public BookingDto getById(Long id) {
        return bookingMapper.toDto(bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found")));
    }

    @CircuitBreaker(name = "flightClient")
    void ensureFlightExists(Long id) { flightClient.flightExists(id); }

    @CircuitBreaker(name = "passengerClient")
    void ensurePassengerExists(Long id)   { passengerClient.passengerExists(id); }
}
