package com.airflights.booking.service;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.entity.Booking;
import com.airflights.flight.entity.Flight;
import com.airflights.passenger.entity.Passenger;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("Booking not found")));
    }
}
