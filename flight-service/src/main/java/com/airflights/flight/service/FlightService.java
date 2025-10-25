package com.airflights.flight.service;

import com.airflights.flight.dto.FlightDto;
import com.airflights.flight.entity.*;
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
    private final AirlineService airlineService;
    private final AirportService airportService;

    public Page<FlightDto> getAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Transactional
    public FlightDto create(FlightDto dto) {
        Airline airline = airlineService.getByIdEntity(dto.getAirlineId());
        Airport departureAirport = airportService.getByIdEntity(dto.getDepartureAirportId());
        Airport arrivalAirport = airportService.getByIdEntity(dto.getArrivalAirportId());

        Flight flight = flightMapper.toEntity(dto, airline, departureAirport, arrivalAirport);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public void updateFlightsDueToRestriction(RestrictedZone zone) {
        // транзакционно обновляем статус рейсов, попавших в зону ограничения
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

        Airline airline = airlineService.getByIdEntity(dto.getAirlineId());
        Airport departureAirport = airportService.getByIdEntity(dto.getDepartureAirportId());
        Airport arrivalAirport = airportService.getByIdEntity(dto.getArrivalAirportId());

        Flight flight = flightMapper.toEntity(dto, airline, departureAirport, arrivalAirport);
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
}
