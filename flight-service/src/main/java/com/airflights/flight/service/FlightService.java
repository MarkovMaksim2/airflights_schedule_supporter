package com.airflights.flight.service;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.entity.*;
import com.airflights.flight.feign.AirlineClient;
import com.airflights.flight.feign.AirportClient;
import com.airflights.flight.mapper.FlightMapper;
import com.airflights.flight.repository.FlightRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    private final AirlineClient airlineClient;
    private final AirportClient airportClient;

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
        if (id == null || !flightRepository.existsById(id)) {
            throw new EntityNotFoundException("flight not found");
        }

        ensureAirlineExists(dto.getAirlineId());
        ensureAirportExists(dto.getDepartureAirportId());
        ensureAirportExists(dto.getArrivalAirportId());

        Flight flight = flightMapper.toEntity(dto);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    public FlightDto getById(Long id) {
        return flightMapper.toDto(flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found")));
    }

    public Flight getByIdEntity(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));
    }

    @Transactional
    public void delete(Long id) {
        flightRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FlightDto> getInfiniteScroll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit); // вычисляем номер страницы
        Page<Flight> flights = flightRepository.findAllByOrderByDepartureTimeAsc(pageable);
        return flights.stream()
                .map(flightMapper::toDto)
                .toList();
    }

    @CircuitBreaker(name = "airlineClient")
    void ensureAirlineExists(Long id) { airlineClient.airlineExists(id); }

    @CircuitBreaker(name = "airportClient")
    void ensureAirportExists(Long id)   { airportClient.airportExists(id); }
}
