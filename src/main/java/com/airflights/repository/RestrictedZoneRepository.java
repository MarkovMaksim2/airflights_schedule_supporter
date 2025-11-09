package com.airflights.repository;

import com.airflights.entity.RestrictedZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictedZoneRepository extends JpaRepository<RestrictedZone, Long> {
    boolean existsByRegion(String region);
}
