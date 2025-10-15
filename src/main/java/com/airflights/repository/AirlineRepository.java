package com.airflights.repository;

import com.airflights.entity.Airline;
import com.airflights.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    boolean existsByName(String name);
}
