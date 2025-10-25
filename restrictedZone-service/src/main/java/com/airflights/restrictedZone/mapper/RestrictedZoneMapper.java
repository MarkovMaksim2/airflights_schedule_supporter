package com.airflights.restrictedZone.mapper;

import com.airflights.restrictedZone.dto.RestrictedZoneDto;
import com.airflights.restrictedZone.entity.RestrictedZone;
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
