package com.airflights.flight.infrastructure.persistence.repository;

import com.airflights.flight.infrastructure.persistence.entity.FlightEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface JpaFlightRepository extends JpaRepository<FlightEntity, Long>, JpaSpecificationExecutor<FlightEntity> {
    Optional<List<FlightEntity>> findAllByAirlineId(Long id);

    Page<FlightEntity> findAllByOrderByDepartureTimeAsc(Pageable pageable);
}
