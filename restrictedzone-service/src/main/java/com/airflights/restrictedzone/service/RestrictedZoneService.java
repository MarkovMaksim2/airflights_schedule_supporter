package com.airflights.restrictedzone.service;

import com.airflights.restrictedzone.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.entity.RestrictedZone;
import com.airflights.restrictedzone.feign.FlightClient;
import com.airflights.restrictedzone.mapper.RestrictedZoneMapper;
import com.airflights.restrictedzone.repository.RestrictedZoneRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestrictedZoneService {

    private final RestrictedZoneRepository restrictedZoneRepository;
    private final RestrictedZoneMapper restrictedZoneMapper;
    private final FlightClient flightClient;

    @Transactional
    public RestrictedZoneDto create(RestrictedZoneDto dto) {
        RestrictedZone zone = restrictedZoneMapper.toEntity(dto);
        RestrictedZone saved = restrictedZoneRepository.save(zone);

        ensureChangeTimings(dto);
        return restrictedZoneMapper.toDto(saved);
    }

    public Page<RestrictedZoneDto> getAll(Pageable pageable) {
        return restrictedZoneRepository.findAll(pageable)
                .map(restrictedZoneMapper::toDto);
    }

    @Transactional
    public void delete(Long id) {
        restrictedZoneRepository.deleteById(id);
    }

    public RestrictedZoneDto getById(Long id) {
        return restrictedZoneRepository.findById(id)
                .map(restrictedZoneMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Restricted zone not found"));
    }

    @CircuitBreaker(name = "flightClient")
    void ensureChangeTimings(RestrictedZoneDto dto) { flightClient.updateFlightsDueToRestriction(dto); }
}
