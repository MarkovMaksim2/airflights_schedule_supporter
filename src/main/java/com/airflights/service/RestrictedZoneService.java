package com.airflights.service;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.entity.RestrictedZone;
import com.airflights.mapper.RestrictedZoneMapper;
import com.airflights.repository.RestrictedZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<RestrictedZoneDto> getAll() {
        return restrictedZoneRepository.findAll()
                .stream()
                .map(restrictedZoneMapper::toDto)
                .toList();
    }

    public void delete(Long id) {
        restrictedZoneRepository.deleteById(id);
    }
}
