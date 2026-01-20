package com.airflights.airport.infrastructure.persistence;

import com.airflights.airport.application.port.out.AirportManagerRepository;
import com.airflights.airport.domain.model.AirportManager;
import com.airflights.airport.infrastructure.persistence.mapper.AirportManagerEntityMapper;
import com.airflights.airport.infrastructure.persistence.repository.JpaAirportManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AirportManagerRepositoryAdapter implements AirportManagerRepository {
    private final JpaAirportManagerRepository airportManagerRepository;
    private final AirportManagerEntityMapper airportManagerEntityMapper;

    @Override
    public boolean existsById(Long id) {
        return airportManagerRepository.existsById(id);
    }

    @Override
    public boolean existsByUserEmailIgnoreCase(String userEmail) {
        return airportManagerRepository.existsByUserEmailIgnoreCase(userEmail);
    }

    @Override
    public Optional<AirportManager> findByUserEmailIgnoreCase(String email) {
        return airportManagerRepository.findByUserEmailIgnoreCase(email)
                .map(airportManagerEntityMapper::toDomain);
    }

    @Override
    public AirportManager save(AirportManager airportManager) {
        return airportManagerEntityMapper.toDomain(
                airportManagerRepository.save(airportManagerEntityMapper.toEntity(airportManager))
        );
    }

    @Override
    public void deleteById(Long id) {
        airportManagerRepository.deleteById(id);
    }
}
