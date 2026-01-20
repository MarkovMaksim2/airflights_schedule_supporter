package com.airflights.restrictedzone.infrastructure.persistence.mapper;

import com.airflights.restrictedzone.domain.model.RestrictedZone;
import com.airflights.restrictedzone.infrastructure.persistence.entity.RestrictedZoneEntity;
import org.springframework.stereotype.Component;

@Component
public class RestrictedZoneEntityMapper {
    public RestrictedZone toDomain(RestrictedZoneEntity entity) {
        return new RestrictedZone(
                entity.getId(),
                entity.getRegion(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }

    public RestrictedZoneEntity toEntity(RestrictedZone zone) {
        return new RestrictedZoneEntity(
                zone.getId(),
                zone.getRegion(),
                zone.getStartTime(),
                zone.getEndTime()
        );
    }
}
