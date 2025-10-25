package com.airflights.restrictedZone.mapper;

import com.airflights.restrictedZone.dto.RestrictedZoneDto;
import com.airflights.restrictedZone.entity.RestrictedZone;
import org.springframework.stereotype.Component;

@Component
public class RestrictedZoneMapper {
    public com.airflights.restrictedZone.dto.RestrictedZoneDto toDto(com.airflights.restrictedZone.entity.RestrictedZone restrictedZone) {
        return new com.airflights.restrictedZone.dto.RestrictedZoneDto(
                restrictedZone.getId(),
                restrictedZone.getRegion(),
                restrictedZone.getStartTime(),
                restrictedZone.getEndTime()
        );
    }
    public com.airflights.restrictedZone.entity.RestrictedZone toEntity(com.airflights.restrictedZone.dto.RestrictedZoneDto restrictedZoneDto) {
        return new com.airflights.restrictedZone.entity.RestrictedZone(
                restrictedZoneDto.getId(),
                restrictedZoneDto.getRegion(),
                restrictedZoneDto.getStartTime(),
                restrictedZoneDto.getEndTime()
        );
    }
}
