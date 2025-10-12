package com.airflights.service;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.mapper.BookingMapper;
import com.airflights.repository.BookingRepository;
import com.airflights.repository.FlightRepository;
import com.airflights.repository.PassengerRepository;
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

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto bookFlight(Long passengerId, Long flightId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto create(@Valid BookingDto dto) {
        Passenger passenger = passengerRepository.findById(dto.getPassengerId()).
                orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        Flight flight = flightRepository.findById(dto.getFlightId()).
                orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        Booking booking = bookingMapper.toEntity(dto, passenger, flight);
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
                .orElseThrow(() -> new IllegalArgumentException("Booking not found")));
    }
}
