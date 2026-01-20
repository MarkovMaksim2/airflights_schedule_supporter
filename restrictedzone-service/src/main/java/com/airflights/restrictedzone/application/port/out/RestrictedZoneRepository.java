package com.airflights.restrictedzone.application.port.out;

import com.airflights.restrictedzone.domain.model.RestrictedZone;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestrictedZoneRepository {
    Page<RestrictedZone> findAll(Pageable pageable);
    Optional<RestrictedZone> findById(Long id);
    RestrictedZone save(RestrictedZone zone);
    void deleteById(Long id);
    boolean existsByRegion(String region);
}
