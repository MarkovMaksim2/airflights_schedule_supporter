package com.airflights.restrictedZone.repository;

import com.airflights.restrictedZone.entity.RestrictedZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictedZoneRepository extends JpaRepository<RestrictedZone, Long> {
}
