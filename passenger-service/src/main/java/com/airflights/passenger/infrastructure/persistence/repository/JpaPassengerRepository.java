package com.airflights.passenger.infrastructure.persistence.repository;

import com.airflights.passenger.infrastructure.persistence.entity.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPassengerRepository extends JpaRepository<PassengerEntity, Long> {
    boolean existsByPassportNumber(String passportNumber);
    boolean existsByEmail(String email);
    java.util.Optional<PassengerEntity> findByEmail(String email);
}
