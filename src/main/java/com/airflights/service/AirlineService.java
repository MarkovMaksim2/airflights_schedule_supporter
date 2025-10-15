package com.airflights.service;

import com.airflights.dto.AirlineDto;
import com.airflights.entity.Airline;
import com.airflights.mapper.AirlineMapper;
import com.airflights.repository.AirlineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    public Page<AirlineDto> getAll(Pageable pageable) {
        return airlineRepository.findAll(pageable)
                .map(airlineMapper::toDto);
    }

    public AirlineDto getById(Long id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found: " + id));
        return airlineMapper.toDto(airline);
    }

    public Airline getByIdEntity(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found: " + id));
    }

    @Transactional
    public AirlineDto create(AirlineDto dto) {
        if (dto.getName() != null && airlineRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Airline with name '" + dto.getName() + "' already exists");
        }

        Airline entity = airlineMapper.toEntity(dto, new ArrayList<>());
        Airline saved = airlineRepository.save(entity);
        return airlineMapper.toDto(saved);
    }

    @Transactional
    public AirlineDto update(Long id, AirlineDto dto) {
        Airline existing = airlineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found: " + id));

        // простое обновление полей (без замены связей)
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            if (airlineRepository.existsByName(dto.getName())) {
                throw new IllegalArgumentException("Airline with name '" + dto.getName() + "' already exists");
            }
            existing.setName(dto.getName());
        }

        if (dto.getContactEmail() != null) {
            existing.setContactEmail(dto.getContactEmail());
        }

        Airline saved = airlineRepository.save(existing);
        return airlineMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!airlineRepository.existsById(id)) {
            throw new EntityNotFoundException("Airline not found: " + id);
        }
        airlineRepository.deleteById(id);
    }
}
