package com.airflights.airport.application.port.out;

import com.airflights.airport.domain.model.Airport;

import java.util.List;
import java.util.Optional;

public interface AirportRepository {
    List<Airport> findAll(int page, int size);
    Optional<Airport> findById(Long id);
    Optional<Airport> findByCode(String code);
    boolean existsById(Long id);
    boolean existsByCode(String code);
    boolean existsByName(String name);
    Airport save(Airport airport);
    void deleteById(Long id);
    long count();
}
