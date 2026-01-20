package com.airflights.restrictedzone.infrastructure.persistence.repository;

import com.airflights.restrictedzone.infrastructure.persistence.entity.RestrictedZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRestrictedZoneRepository extends JpaRepository<RestrictedZoneEntity, Long> {
    boolean existsByRegion(String region);
}
