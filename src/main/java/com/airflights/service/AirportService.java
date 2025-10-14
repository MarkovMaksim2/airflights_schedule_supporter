package com.airflights.service;

import com.airflights.dto.AirportDto;
import com.airflights.entity.Airport;
import com.airflights.mapper.AirportMapper;
import com.airflights.repository.AirportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public Page<AirportDto> getAll(Pageable pageable) {
        return airportRepository.findAll(pageable)
                .map(airportMapper::toDto);
    }

    public AirportDto getById(Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found: " + id));
        return airportMapper.toDto(airport);
    }

    public Airport getByIdEntity(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found: " + id));
    }

    @Transactional
    public AirportDto create(AirportDto dto) {
        if (dto.getCode() != null && airportRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Airport with code '" + dto.getCode() + "' already exists");
        }

        Airport entity = airportMapper.toEntity(dto);
        Airport saved = airportRepository.save(entity);
        return airportMapper.toDto(saved);
    }

    @Transactional
    public AirportDto update(Long id, AirportDto dto) {
        Airport existing = airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found: " + id));

        if (dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {
            if (airportRepository.existsByCode(dto.getCode())) {
                throw new IllegalArgumentException("Airport with code '" + dto.getCode() + "' already exists");
            }
            existing.setCode(dto.getCode());
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCity() != null) existing.setCity(dto.getCity());

        Airport saved = airportRepository.save(existing);
        return airportMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new EntityNotFoundException("Airport not found: " + id);
        }
        airportRepository.deleteById(id);
    }
}
