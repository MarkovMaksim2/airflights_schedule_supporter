package com.airflights.airport.infrastructure.persistence.repository;

import com.airflights.airport.infrastructure.persistence.entity.AirportManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAirportManagerRepository extends JpaRepository<AirportManagerEntity, Long> {
    boolean existsByUserEmailIgnoreCase(String userEmail);
    Optional<AirportManagerEntity> findByUserEmailIgnoreCase(String userEmail);
}
