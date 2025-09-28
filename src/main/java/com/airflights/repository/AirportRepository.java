package com.airflights.repository;

import com.airflights.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByCode(String code);
}
