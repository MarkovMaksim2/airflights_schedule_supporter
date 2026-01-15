package com.airflights.booking.service;

import com.airflights.booking.dto.BookingDto;
import com.airflights.booking.dto.PassengerSummary;
import com.airflights.booking.domain.event.BookingCreatedEvent;
import com.airflights.booking.domain.port.BookingEventPublisher;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final FlightVerifier flightVerifier;
    private final PassengerVerifier passengerVerifier;
    private final BookingEventPublisher bookingEventPublisher;

    @Transactional
    public BookingDto bookFlight(Long passengerId, Long flightId) {
        ensurePassengerExists(passengerId);
        ensureFlightExists(flightId);
        Booking booking = new Booking();
        booking.setPassengerId(passengerId);
        booking.setFlightId(flightId);
        booking.setBookingTime(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);
        publishBookingCreated(saved, resolvePassengerEmail(passengerId, null, null));
        return bookingMapper.toDto(saved);
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
        Booking saved = bookingRepository.save(booking);
        String passengerEmail = resolvePassengerEmail(passengerId, userEmail, rolesHeader);
        publishBookingCreated(saved, passengerEmail);
        return bookingMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id, String rolesHeader, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (hasRole(rolesHeader)) {
            resolvePassengerIdForUser(userEmail, booking.getPassengerId());
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
            resolvePassengerIdForUser(userEmail, booking.getPassengerId());
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

    private String resolvePassengerEmail(Long passengerId, String userEmail, String rolesHeader) {
        if (hasRole(rolesHeader) && userEmail != null && !userEmail.isBlank()) {
            return userEmail;
        }
        PassengerSummary passenger = passengerVerifier.getPassengerById(passengerId);
        return passenger.getEmail();
    }

    private void publishBookingCreated(Booking booking, String passengerEmail) {
        BookingCreatedEvent event = new BookingCreatedEvent(
                UUID.randomUUID().toString(),
                booking.getId(),
                booking.getPassengerId(),
                booking.getFlightId(),
                booking.getBookingTime(),
                passengerEmail
        );
        bookingEventPublisher.publishBookingCreated(event);
    }

    private boolean hasRole(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return false;
        }
        return hasSupervisorRole(rolesHeader) || Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_PASSENGER"));
    }

    private boolean hasSupervisorRole(String rolesHeader) {
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_SUPERVISOR"));
    }
}
