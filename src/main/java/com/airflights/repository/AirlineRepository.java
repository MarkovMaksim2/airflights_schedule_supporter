package com.example.scheduler.repository;

import com.example.scheduler.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    boolean existsByName(String name);
}
