package com.airflights.airport.repository;

import com.airflights.airport.entity.AirportManager;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportManagerRepository extends JpaRepository<AirportManager, Long> {
    boolean existsByUserEmailIgnoreCase(String userEmail);
    Optional<AirportManager> findByUserEmailIgnoreCase(String userEmail);
}
