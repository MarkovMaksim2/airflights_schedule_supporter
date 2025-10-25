package com.airflights.service;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.entity.RestrictedZone;
import com.airflights.mapper.RestrictedZoneMapper;
import com.airflights.repository.RestrictedZoneRepository;
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
    private final FlightService flightService;

    @Transactional
    public RestrictedZoneDto create(RestrictedZoneDto dto) {
        RestrictedZone zone = restrictedZoneMapper.toEntity(dto);
        RestrictedZone saved = restrictedZoneRepository.save(zone);

        // при добавлении новой зоны обновляем рейсы
        flightService.updateFlightsDueToRestriction(zone);
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
}
