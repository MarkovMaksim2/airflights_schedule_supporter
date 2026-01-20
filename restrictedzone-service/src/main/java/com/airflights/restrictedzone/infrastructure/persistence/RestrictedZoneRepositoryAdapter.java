package com.airflights.restrictedzone.infrastructure.persistence;

import com.airflights.restrictedzone.application.port.out.RestrictedZoneRepository;
import com.airflights.restrictedzone.domain.model.RestrictedZone;
import com.airflights.restrictedzone.infrastructure.persistence.mapper.RestrictedZoneEntityMapper;
import com.airflights.restrictedzone.infrastructure.persistence.repository.JpaRestrictedZoneRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestrictedZoneRepositoryAdapter implements RestrictedZoneRepository {
    private final JpaRestrictedZoneRepository restrictedZoneRepository;
    private final RestrictedZoneEntityMapper restrictedZoneEntityMapper;

    @Override
    public Page<RestrictedZone> findAll(Pageable pageable) {
        return restrictedZoneRepository.findAll(pageable)
                .map(restrictedZoneEntityMapper::toDomain);
    }

    @Override
    public Optional<RestrictedZone> findById(Long id) {
        return restrictedZoneRepository.findById(id)
                .map(restrictedZoneEntityMapper::toDomain);
    }

    @Override
    public RestrictedZone save(RestrictedZone zone) {
        return restrictedZoneEntityMapper.toDomain(
                restrictedZoneRepository.save(restrictedZoneEntityMapper.toEntity(zone))
        );
    }

    @Override
    public void deleteById(Long id) {
        restrictedZoneRepository.deleteById(id);
    }

    @Override
    public boolean existsByRegion(String region) {
        return restrictedZoneRepository.existsByRegion(region);
    }
}
