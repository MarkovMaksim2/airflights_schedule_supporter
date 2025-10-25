package com.airflights.restrictedzone.repository;

import com.airflights.restrictedzone.entity.RestrictedZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictedZoneRepository extends JpaRepository<RestrictedZone, Long> {
}
