package com.airflights.repository;

import com.airFlights.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    boolean existsByName(String name);
}
