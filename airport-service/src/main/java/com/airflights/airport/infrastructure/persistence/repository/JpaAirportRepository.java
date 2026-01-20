package com.airflights.airport.infrastructure.persistence.repository;

import com.airflights.airport.infrastructure.persistence.entity.AirportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAirportRepository extends JpaRepository<AirportEntity, Long> {
    boolean existsByCode(String code);
    boolean existsByName(String name);
    Optional<AirportEntity> findByCode(String code);
}
