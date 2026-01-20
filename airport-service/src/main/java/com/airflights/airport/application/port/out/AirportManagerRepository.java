package com.airflights.airport.application.port.out;

import com.airflights.airport.domain.model.AirportManager;

import java.util.Optional;

public interface AirportManagerRepository {
    boolean existsById(Long id);
    boolean existsByUserEmailIgnoreCase(String userEmail);
    Optional<AirportManager> findByUserEmailIgnoreCase(String email);
    AirportManager save(AirportManager airportManager);
    void deleteById(Long id);
}
