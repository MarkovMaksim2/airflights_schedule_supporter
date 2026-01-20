package com.airflights.passenger.application.port.out;

import com.airflights.passenger.domain.model.Passenger;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PassengerRepository {
    Page<Passenger> findAll(Pageable pageable);
    Optional<Passenger> findById(Long id);
    Optional<Passenger> findByEmail(String email);
    boolean existsByPassportNumber(String passportNumber);
    boolean existsByEmail(String email);
    Passenger save(Passenger passenger);
    void deleteById(Long id);
}
