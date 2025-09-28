package com.example.scheduler.repository;

import com.example.scheduler.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByCode(String code);
}
