package com.airflights.passenger.application.service;

import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.application.exception.ResourceNotFoundException;
import com.airflights.passenger.application.mapper.PassengerMapper;
import com.airflights.passenger.application.port.in.PassengerUseCase;
import com.airflights.passenger.application.port.out.PassengerRepository;
import com.airflights.passenger.domain.model.Passenger;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerService implements PassengerUseCase {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private static final String PASSENGER_NOT_FOUND = "Passenger not found";

    @Override
    public Page<PassengerDto> getAll(Pageable pageable) {
        return passengerRepository.findAll(pageable)
                .map(passengerMapper::toDto);
    }

    @Transactional
    @Override
    public PassengerDto create(PassengerDto dto) {
        if (passengerRepository.existsByPassportNumber(dto.getPassportNumber())) {
            throw new IllegalArgumentException("Passenger with this passport already exists");
        }
        if (passengerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Passenger with this email already exists");
        }

        Passenger passenger = passengerMapper.toDomain(dto);
        return passengerMapper.toDto(passengerRepository.save(passenger));
    }


    @Override
    public PassengerDto getById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PASSENGER_NOT_FOUND));
        return passengerMapper.toDto(passenger);
    }

    @Override
    public PassengerDto getByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(PASSENGER_NOT_FOUND));
        return passengerMapper.toDto(passenger);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        passengerRepository.deleteById(id);
    }

    @Transactional
    @Override
    public PassengerDto update(Long id, PassengerDto dto) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PASSENGER_NOT_FOUND));
        passenger.setFirstName(dto.getFirstName());
        passenger.setLastName(dto.getLastName());
        passenger.setPassportNumber(dto.getPassportNumber());
        return passengerMapper.toDto(passengerRepository.save(passenger));
    }
}
