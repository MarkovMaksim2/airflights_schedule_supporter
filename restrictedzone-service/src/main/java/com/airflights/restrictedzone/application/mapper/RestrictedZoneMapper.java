package com.airflights.restrictedzone.application.mapper;

import com.airflights.restrictedzone.application.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.domain.model.RestrictedZone;
import org.springframework.stereotype.Component;

@Component
public class RestrictedZoneMapper {
    public RestrictedZoneDto toDto(RestrictedZone restrictedZone) {
        return new RestrictedZoneDto(
                restrictedZone.getId(),
                restrictedZone.getRegion(),
                restrictedZone.getStartTime(),
                restrictedZone.getEndTime()
        );
    }
    public RestrictedZone toDomain(RestrictedZoneDto restrictedZoneDto) {
        return new RestrictedZone(
                restrictedZoneDto.getId(),
                restrictedZoneDto.getRegion(),
                restrictedZoneDto.getStartTime(),
                restrictedZoneDto.getEndTime()
        );
    }
}
