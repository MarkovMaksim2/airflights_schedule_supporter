package com.airflights.service;

import com.airflights.dto.FlightDto;
import com.airflights.entity.Flight;
import com.airflights.entity.RestrictedZone;
import com.airflights.mapper.FlightMapper;
import com.airflights.repository.FlightRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public Page<FlightDto> getAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightMapper::toDto);
    }

    @Transactional
    public FlightDto create(FlightDto dto) {
        Flight flight = flightMapper.toEntity(dto);
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
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));
        flightMapper.updateEntityFromDto(dto, flight);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Transactional
    public FlightDto getById(Long id) {
        return flightMapper.toDto(flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found")));
    }
}
