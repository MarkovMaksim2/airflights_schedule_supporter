package com.airflights.flight.service;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.entity.Flight;
import com.airflights.flight.feign.AirlineVerifier;
import com.airflights.flight.feign.AirportVerifier;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineVerifier airlineVerifier;
    private final AirportVerifier airportVerifier;

    public Page<FlightDto> getAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Transactional
    public FlightDto create(FlightDto dto) {
        ensureAirlineExists(dto.getAirlineId());
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
    public FlightDto update(Long id, @Valid FlightDto dto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));

        ensureAirlineExists(dto.getAirlineId());
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
                .orElseThrow(() -> new EntityNotFoundException("Flight not found")));
    }

    @Transactional
    public void delete(Long id) {
        flightRepository.deleteById(id);
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
}
