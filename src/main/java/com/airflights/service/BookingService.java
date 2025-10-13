package com.airflights.service;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.mapper.BookingMapper;
import com.airflights.repository.BookingRepository;
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
    private final PassengerService passengerService;
    private final FlightService flightService;

    @Transactional
    public BookingDto bookFlight(Long passengerId, Long flightId) {
        Passenger passenger = passengerService.getByIdEntity(passengerId);

        Flight flight = flightService.getByIdEntity(flightId);

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setFlight(flight);
        booking.setBookingTime(LocalDateTime.now());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto create(@Valid BookingDto dto) {
        Passenger passenger = passengerService.getByIdEntity(dto.getPassengerId());

        Flight flight = flightService.getByIdEntity(dto.getFlightId());

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
