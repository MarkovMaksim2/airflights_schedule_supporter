package com.airflights.mapper;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.entity.RestrictedZone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestrictedZoneMapper {
    RestrictedZoneDto toDto(RestrictedZone restrictedZone);
    RestrictedZone toEntity(RestrictedZoneDto restrictedZoneDto);
}
