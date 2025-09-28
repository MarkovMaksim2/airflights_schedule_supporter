package com.airflights.service;

import com.airflights.dto.PassengerDto;
import com.airflights.entity.Passenger;
import com.airflights.mapper.PassengerMapper;
import com.airflights.repository.PassengerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    public Page<PassengerDto> getAll(Pageable pageable) {
        return passengerRepository.findAll(pageable)
                .map(passengerMapper::toDto);
    }

    @Transactional
    public PassengerDto create(PassengerDto dto) {
        if (passengerRepository.existsByPassportNumber(dto.getPassportNumber())) {
            throw new IllegalArgumentException("Passenger with this passport already exists");
        }
        Passenger passenger = passengerMapper.toEntity(dto);
        return passengerMapper.toDto(passengerRepository.save(passenger));
    }

    public PassengerDto getById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));
        return passengerMapper.toDto(passenger);
    }

    @Transactional
    public void delete(Long id) {
        passengerRepository.deleteById(id);
    }

    @Transactional
    public PassengerDto update(Long id, @Valid PassengerDto dto) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));
        passenger.setFirstName(dto.getFirstName());
        passenger.setLastName(dto.getLastName());
        passenger.setPassportNumber(dto.getPassportNumber());
        return passengerMapper.toDto(passengerRepository.save(passenger));
    }
}
