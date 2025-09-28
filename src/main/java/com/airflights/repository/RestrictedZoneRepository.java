package com.example.scheduler.repository;

import com.example.scheduler.entity.RestrictedZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestrictedZoneRepository extends JpaRepository<RestrictedZone, Long> {
}
