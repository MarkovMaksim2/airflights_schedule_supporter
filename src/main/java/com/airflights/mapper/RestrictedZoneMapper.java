package com.airflights.mapper;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.entity.RestrictedZone;
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
