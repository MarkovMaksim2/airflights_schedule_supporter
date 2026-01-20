package com.airflights.flight.infrastructure.persistence;

import com.airflights.flight.application.port.out.FlightRepository;
import com.airflights.flight.domain.model.Flight;
import com.airflights.flight.infrastructure.persistence.mapper.FlightEntityMapper;
import com.airflights.flight.infrastructure.persistence.repository.JpaFlightRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FlightRepositoryAdapter implements FlightRepository {
    private final JpaFlightRepository flightRepository;
    private final FlightEntityMapper flightEntityMapper;

    @Override
    public Flight save(Flight flight) {
        return flightEntityMapper.toDomain(
                flightRepository.save(flightEntityMapper.toEntity(flight))
        );
    }

    @Override
    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id)
                .map(flightEntityMapper::toDomain);
    }

    @Override
    public Page<Flight> findAll(Pageable pageable) {
        return flightRepository.findAll(pageable)
                .map(flightEntityMapper::toDomain);
    }

    @Override
    public List<Flight> findAll() {
        return flightRepository.findAll().stream()
                .map(flightEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<List<Flight>> findAllByAirlineId(Long id) {
        return flightRepository.findAllByAirlineId(id)
                .map(list -> list.stream().map(flightEntityMapper::toDomain).toList());
    }

    @Override
    public Page<Flight> findAllByOrderByDepartureTimeAsc(Pageable pageable) {
        return flightRepository.findAllByOrderByDepartureTimeAsc(pageable)
                .map(flightEntityMapper::toDomain);
    }

    @Override
    public void delete(Flight flight) {
        flightRepository.delete(flightEntityMapper.toEntity(flight));
    }
}
