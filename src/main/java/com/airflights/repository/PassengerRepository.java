package com.example.scheduler.repository;

import com.example.scheduler.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByPassportNumber(String passportNumber);
}
