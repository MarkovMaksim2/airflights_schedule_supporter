package com.airflights.flight.application.port.out;

import com.airflights.flight.domain.model.Flight;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightRepository {
    Flight save(Flight flight);
    Optional<Flight> findById(Long id);
    Page<Flight> findAll(Pageable pageable);
    List<Flight> findAll();
    Optional<List<Flight>> findAllByAirlineId(Long id);
    Page<Flight> findAllByOrderByDepartureTimeAsc(Pageable pageable);
    void delete(Flight flight);
}
