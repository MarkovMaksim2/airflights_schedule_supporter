package com.airflights.repository;

import com.airflights.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
    <T> Optional<T> findAllByOrderByDepartureTimeAsc(int offset, int limit);
}
