package com.airflights.restrictedzone.mapper;

import com.airflights.restrictedzone.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.entity.RestrictedZone;
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
    public RestrictedZone toEntity(RestrictedZoneDto restrictedZoneDto) {
        return new RestrictedZone(
                restrictedZoneDto.getId(),
                restrictedZoneDto.getRegion(),
                restrictedZoneDto.getStartTime(),
                restrictedZoneDto.getEndTime()
        );
    }
}
