package com.airflights.repository;

import com.airflights.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByPassportNumber(String passportNumber);
}
