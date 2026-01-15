package com.airflights.flight.service;

import com.airflights.flight.dto.AirlineDto;
import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.entity.Flight;
import com.airflights.flight.feign.AirlineVerifier;
import com.airflights.flight.feign.AirportVerifier;
import com.airflights.flight.messaging.dto.FlightAction;
import com.airflights.flight.messaging.dto.FlightActionRequestedEvent;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {

    private static final String STATUS_WAITING_APPROVAL = "WAITING_APPROVAL";
    private static final String STATUS_APPROVED_DEPARTURE = "APPROVED_DEPARTURE";
    private static final String STATUS_APPROVED_ARRIVAL = "APPROVED_ARRIVAL";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_DEPARTED = "DEPARTED";
    private static final String STATUS_ARRIVED = "ARRIVED";
    private static final String FLIGHT_NOT_FOUND = "Flight not found";

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineVerifier airlineVerifier;
    private final AirportVerifier airportVerifier;

    public Page<FlightDto> getAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Transactional
    public FlightDto create(FlightDto dto, String rolesHeader, String userEmail) {
        if (hasRole(rolesHeader)) {
            ensureAirlineOwnership(dto.getAirlineId(), userEmail);
            dto.setStatus(STATUS_WAITING_APPROVAL);
        } else {
            ensureAirlineExists(dto.getAirlineId());
            if (dto.getStatus() == null || dto.getStatus().isBlank()) {
                dto.setStatus(STATUS_WAITING_APPROVAL);
            }
        }
        ensureAirportExists(dto.getDepartureAirportId());
        ensureAirportExists(dto.getArrivalAirportId());

        Flight flight = flightMapper.toEntity(dto);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public void updateFlightsDueToRestriction(RestrictedZoneDto zone) {
        flightRepository.findAll().stream()
                .filter(f -> f.getDepartureTime().isAfter(zone.getStartTime()) &&
                        f.getDepartureTime().isBefore(zone.getEndTime()))
                .forEach(flight -> {
                    flight.setStatus("CANCELED");
                    flightRepository.save(flight);
                });
    }

    @Transactional
    public FlightDto update(Long id, @Valid FlightDto dto, String rolesHeader, String userEmail) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND));

        if (hasRole(rolesHeader)) {
            ensureAirlineOwnership(flight.getAirlineId(), userEmail);
            if (!flight.getAirlineId().equals(dto.getAirlineId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Airline change not allowed");
            }
        } else {
            ensureAirlineExists(dto.getAirlineId());
        }
        ensureAirportExists(dto.getDepartureAirportId());
        ensureAirportExists(dto.getArrivalAirportId());

        flight.setAirlineId(dto.getAirlineId());
        flight.setDepartureAirportId(dto.getDepartureAirportId());
        flight.setArrivalAirportId(dto.getArrivalAirportId());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setStatus(dto.getStatus());

        return flightMapper.toDto(flightRepository.save(flight));
    }

    public FlightDto getById(Long id) {
        return flightMapper.toDto(flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND)));
    }

    @Transactional
    public void delete(Long id, String rolesHeader, String userEmail) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND));
        if (hasRole(rolesHeader)) {
            ensureAirlineOwnership(flight.getAirlineId(), userEmail);
        }
        flightRepository.delete(flight);
    }

    @Transactional
    public void applyAirportAction(FlightActionRequestedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Flight action event is required");
        }
        if (event.flightId() == null || event.airportId() == null || event.action() == null) {
            throw new IllegalArgumentException("Flight action event is missing required fields");
        }
        FlightAction action = parseAction(event.action());
        switch (action) {
            case APPROVE -> approveByAirport(event.flightId(), event.airportId());
            case DEPART -> departByAirport(event.flightId(), event.airportId());
            case ARRIVE -> arriveByAirport(event.flightId(), event.airportId());
        }
        log.info("Applied flight action {} for flight {}", action, event.flightId());
    }

    @Transactional
    public FlightDto approveByAirport(Long id, Long airportId) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND));
        applyApprovalStatus(flight, airportId);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto departByAirport(Long id, Long airportId) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND));
        if (!flight.getDepartureAirportId().equals(airportId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Departure airport mismatch");
        }
        if (!STATUS_APPROVED.equals(flight.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Flight is not approved");
        }
        flight.setStatus(STATUS_DEPARTED);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto arriveByAirport(Long id, Long airportId) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FLIGHT_NOT_FOUND));
        if (!flight.getArrivalAirportId().equals(airportId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Arrival airport mismatch");
        }
        if (!STATUS_DEPARTED.equals(flight.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Flight has not departed");
        }
        flight.setStatus(STATUS_ARRIVED);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional(readOnly = true)
    public List<FlightDto> getInfiniteScroll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Flight> flights = flightRepository.findAllByOrderByDepartureTimeAsc(pageable);
        return flights.stream()
                .map(flightMapper::toDto)
                .toList();
    }

    void ensureAirlineExists(Long id) { airlineVerifier.ensureAirlineExists(id); }

    void ensureAirportExists(Long id)   { airportVerifier.ensureAirportExists(id); }

    private void ensureAirlineOwnership(Long airlineId, String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User email required");
        }
        AirlineDto airline = airlineVerifier.getAirline(airlineId);
        if (airline.getContactEmail() == null
                || !airline.getContactEmail().equalsIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Airline access denied");
        }
    }

    private void applyApprovalStatus(Flight flight, Long airportId) {
        String current = flight.getStatus();
        if (STATUS_APPROVED.equals(current) || STATUS_DEPARTED.equals(current) || STATUS_ARRIVED.equals(current)) {
            return;
        }
        boolean isDeparture = flight.getDepartureAirportId().equals(airportId);
        boolean isArrival = flight.getArrivalAirportId().equals(airportId);
        if (!isDeparture && !isArrival) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Airport mismatch");
        }
        if (STATUS_WAITING_APPROVAL.equals(current) || current == null || current.isBlank()) {
            flight.setStatus(isDeparture ? STATUS_APPROVED_DEPARTURE : STATUS_APPROVED_ARRIVAL);
            return;
        }
        if (STATUS_APPROVED_DEPARTURE.equals(current)) {
            if (isArrival) {
                flight.setStatus(STATUS_APPROVED);
            }
            return;
        }
        if (STATUS_APPROVED_ARRIVAL.equals(current)) {
            if (isDeparture) {
                flight.setStatus(STATUS_APPROVED);
            }
            return;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Flight not eligible for approval");
    }

    private boolean hasRole(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return false;
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .anyMatch(value -> value.equals("ROLE_AIRLINE_COMPANY"));
    }

    private FlightAction parseAction(String action) {
        try {
            return FlightAction.valueOf(action);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown flight action: " + action, ex);
        }
    }
}
