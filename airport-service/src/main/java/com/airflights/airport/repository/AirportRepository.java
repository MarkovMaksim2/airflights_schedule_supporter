package com.airflights.airport.repository;

import com.airflights.airport.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    boolean existsByCode(String code);  // Это синхронный метод

    Optional<Airport> findByCode(String code);  // Это синхронный метод

    @Async
    CompletableFuture<Optional<Airport>> findByIdAsync(Long id);  // Асинхронный метод с возвратом Optional

    @Async
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Airport a WHERE a.code = :code")
    CompletableFuture<Boolean> existsByCodeAsync(@Param("code") String code);  // Асинхронный метод с явным запросом

    @Async
    @Query("SELECT a FROM Airport a WHERE a.code = :code")
    CompletableFuture<Optional<Airport>> findByCodeAsync(@Param("code") String code);  // Асинхронный метод с явным запросом
}
