package com.airflights.passenger.infrastructure.persistence;

import com.airflights.passenger.application.port.out.PassengerRepository;
import com.airflights.passenger.domain.model.Passenger;
import com.airflights.passenger.infrastructure.persistence.mapper.PassengerEntityMapper;
import com.airflights.passenger.infrastructure.persistence.repository.JpaPassengerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PassengerRepositoryAdapter implements PassengerRepository {
    private final JpaPassengerRepository passengerRepository;
    private final PassengerEntityMapper passengerEntityMapper;

    @Override
    public Page<Passenger> findAll(Pageable pageable) {
        return passengerRepository.findAll(pageable)
                .map(passengerEntityMapper::toDomain);
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        return passengerRepository.findById(id)
                .map(passengerEntityMapper::toDomain);
    }

    @Override
    public Optional<Passenger> findByEmail(String email) {
        return passengerRepository.findByEmail(email)
                .map(passengerEntityMapper::toDomain);
    }

    @Override
    public boolean existsByPassportNumber(String passportNumber) {
        return passengerRepository.existsByPassportNumber(passportNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return passengerRepository.existsByEmail(email);
    }

    @Override
    public Passenger save(Passenger passenger) {
        return passengerEntityMapper.toDomain(
                passengerRepository.save(passengerEntityMapper.toEntity(passenger))
        );
    }

    @Override
    public void deleteById(Long id) {
        passengerRepository.deleteById(id);
    }
}
