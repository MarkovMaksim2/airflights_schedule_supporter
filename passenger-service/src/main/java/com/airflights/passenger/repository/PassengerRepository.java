package com.airflights.passenger.repository;

import com.airflights.passenger.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByPassportNumber(String passportNumber);
}
