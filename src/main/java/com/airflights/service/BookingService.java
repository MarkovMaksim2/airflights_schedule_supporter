package com.airflights.service;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import com.airflights.entity.Flight;
import com.airflights.entity.Passenger;
import com.airflights.mapper.BookingMapper;
import com.airflights.repository.BookingRepository;
import com.airflights.repository.FlightRepository;
import com.airflights.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return bookingMapper.toDto(bookingRepository.save(booking));
    }
}
