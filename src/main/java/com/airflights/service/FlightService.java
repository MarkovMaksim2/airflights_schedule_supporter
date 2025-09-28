package com.airflights.service;

import com.airflights.dto.FlightDto;
import com.airflights.entity.*;
import com.airflights.mapper.FlightMapper;
import com.airflights.repository.AirlineRepository;
import com.airflights.repository.AirportRepository;
import com.airflights.repository.BookingRepository;
import com.airflights.repository.FlightRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final BookingRepository bookingRepository;

    public Page<FlightDto> getAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Transactional
    public FlightDto create(FlightDto dto) {
        Airline airline = airlineRepository.findById(dto.getAirlineId()).
                orElseThrow(() -> new IllegalArgumentException("airline not found"));
        Airport departureAirport = airportRepository.findById(dto.getDepartureAirportId()).
                orElseThrow(() -> new IllegalArgumentException("departure airport not found"));
        Airport arrivalAirport = airportRepository.findById(dto.getDepartureAirportId()).
                orElseThrow(() -> new IllegalArgumentException("arrival airport not found"));

        List<Booking> bookingList = bookingRepository.findAllByFlight_Id(dto.getId()).
                orElseThrow(() -> new IllegalArgumentException("bookings not found"));

        Flight flight = flightMapper.toEntity(dto, airline, departureAirport, arrivalAirport, bookingList);
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
        if (id != null && flightRepository.existsById(id)) {
            throw new IllegalArgumentException("flight not found");
        }

        Airline airline = airlineRepository.findById(dto.getAirlineId()).
                orElseThrow(() -> new IllegalArgumentException("airline not found"));
        Airport departureAirport = airportRepository.findById(dto.getDepartureAirportId()).
                orElseThrow(() -> new IllegalArgumentException("departure airport not found"));
        Airport arrivalAirport = airportRepository.findById(dto.getArrivalAirportId()).
                orElseThrow(() -> new IllegalArgumentException("arrival airport not found"));
        List<Booking> bookingList = bookingRepository.findAllByFlight_Id(dto.getId()).
                orElseThrow(() -> new IllegalArgumentException("bookings not found"));

        Flight flight = flightMapper.toEntity(dto, airline, departureAirport, arrivalAirport, bookingList);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto getById(Long id) {
        return flightMapper.toDto(flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found")));
    }

    @Transactional
    public void delete(Long id) {
        flightRepository.deleteById(id);
    }

    @Transactional
    public List<FlightDto> getInfiniteScroll(int offset, int limit) {
        List<Flight> flightList =  flightRepository.findAllByOrderByDepartureTimeAsc(offset, limit).
                orElseThrow(() -> new IllegalArgumentException("flights not found"));
        return flightList.stream().map(flightMapper::toDto).toList();
    }
}
