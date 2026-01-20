package com.airflights.airport.infrastructure.persistence;

import com.airflights.airport.application.port.out.AirportRepository;
import com.airflights.airport.domain.model.Airport;
import com.airflights.airport.infrastructure.persistence.mapper.AirportEntityMapper;
import com.airflights.airport.infrastructure.persistence.repository.JpaAirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AirportRepositoryAdapter implements AirportRepository {
    private final JpaAirportRepository airportRepository;
    private final AirportEntityMapper airportEntityMapper;

    @Override
    public List<Airport> findAll(int page, int size) {
        return airportRepository.findAll(PageRequest.of(page, size))
                .map(airportEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Airport> findById(Long id) {
        return airportRepository.findById(id)
                .map(airportEntityMapper::toDomain);
    }

    @Override
    public Optional<Airport> findByCode(String code) {
        return airportRepository.findByCode(code)
                .map(airportEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return airportRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return airportRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return airportRepository.existsByName(name);
    }

    @Override
    public Airport save(Airport airport) {
        return airportEntityMapper.toDomain(
                airportRepository.save(airportEntityMapper.toEntity(airport))
        );
    }

    @Override
    public void deleteById(Long id) {
        airportRepository.deleteById(id);
    }

    @Override
    public long count() {
        return airportRepository.count();
    }
}
