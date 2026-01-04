package com.airflights.booking.service;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.dto.PassengerSummary;
import com.airflights.booking.entity.Booking;
import com.airflights.booking.feign.FlightVerifier;
import com.airflights.booking.feign.PassengerVerifier;
import com.airflights.booking.mapper.BookingMapper;
import com.airflights.booking.repository.BookingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final FlightVerifier flightVerifier;
    private final PassengerVerifier passengerVerifier;

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
    public BookingDto create(@Valid BookingDto dto, String rolesHeader, String userEmail) {
        Long passengerId = dto.getPassengerId();
        if (hasRole(rolesHeader)) {
            passengerId = resolvePassengerIdForUser(userEmail, passengerId);
        }
        ensurePassengerExists(passengerId);
        ensureFlightExists(dto.getFlightId());

        Booking booking = bookingMapper.toEntity(dto);
        booking.setPassengerId(passengerId);
        booking.setBookingTime(LocalDateTime.now());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void delete(Long id, String rolesHeader, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (hasRole(rolesHeader)) {
            Long passengerId = resolvePassengerIdForUser(userEmail, booking.getPassengerId());
            if (!booking.getPassengerId().equals(passengerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking access denied");
            }
        }
        bookingRepository.delete(booking);
    }

    public Page<BookingDto> getAll(Pageable pageable, String rolesHeader, String userEmail) {
        if (hasRole(rolesHeader)) {
            Long passengerId = resolvePassengerIdForUser(userEmail, null);
            return bookingRepository.findAllByPassengerId(passengerId, pageable)
                    .map(bookingMapper::toDto);
        }
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    public BookingDto getById(Long id, String rolesHeader, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (hasRole(rolesHeader)) {
            Long passengerId = resolvePassengerIdForUser(userEmail, booking.getPassengerId());
            if (!booking.getPassengerId().equals(passengerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking access denied");
            }
        }
        return bookingMapper.toDto(booking);
    }

    void ensureFlightExists(Long id) { flightVerifier.ensureFlightExists(id); }

    void ensurePassengerExists(Long id)   { passengerVerifier.ensurePassengerExists(id); }

    private Long resolvePassengerIdForUser(String userEmail, Long requestedPassengerId) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User email required");
        }
        PassengerSummary passenger = passengerVerifier.getPassengerByEmail(userEmail);
        if (requestedPassengerId != null && !requestedPassengerId.equals(passenger.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Passenger mismatch");
        }
        return passenger.getId();
    }

    private boolean hasRole(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return false;
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_PASSENGER"));
    }
}
