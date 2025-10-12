package com.airflights.repository;

import com.airflights.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
    Optional<List<Flight>> findAllByAirline_Id(Long id);

    Page<Flight> findAllByOrderByDepartureTimeAsc(Pageable pageable);
}
