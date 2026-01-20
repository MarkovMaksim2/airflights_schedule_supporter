package com.airflights.restrictedzone.application.service;

import com.airflights.restrictedzone.application.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.application.exception.ResourceNotFoundException;
import com.airflights.restrictedzone.application.mapper.RestrictedZoneMapper;
import com.airflights.restrictedzone.application.port.in.RestrictedZoneUseCase;
import com.airflights.restrictedzone.application.port.out.RestrictedZoneRepository;
import com.airflights.restrictedzone.domain.model.RestrictedZone;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestrictedZoneService implements RestrictedZoneUseCase {

    private final RestrictedZoneRepository restrictedZoneRepository;
    private final RestrictedZoneMapper restrictedZoneMapper;

    @Transactional
    @Override
    public RestrictedZoneDto create(RestrictedZoneDto dto) {
        if (dto.getRegion() != null && restrictedZoneRepository.existsByRegion(dto.getRegion())) {
            throw new IllegalArgumentException("RestrictedZone with region '" + dto.getRegion() + "' already exists");
        }

        RestrictedZone zone = restrictedZoneMapper.toDomain(dto);
        RestrictedZone saved = restrictedZoneRepository.save(zone);

        return restrictedZoneMapper.toDto(saved);
    }

    @Override
    public Page<RestrictedZoneDto> getAll(Pageable pageable) {
        return restrictedZoneRepository.findAll(pageable)
                .map(restrictedZoneMapper::toDto);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        restrictedZoneRepository.deleteById(id);
    }

    @Override
    public RestrictedZoneDto getById(Long id) {
        return restrictedZoneRepository.findById(id)
                .map(restrictedZoneMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Restricted zone not found"));
    }
}
