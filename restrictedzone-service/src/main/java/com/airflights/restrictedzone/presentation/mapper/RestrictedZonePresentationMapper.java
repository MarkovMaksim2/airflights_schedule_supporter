package com.airflights.restrictedzone.presentation.mapper;

import com.airflights.restrictedzone.application.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.presentation.dto.RestrictedZoneRequest;
import com.airflights.restrictedzone.presentation.dto.RestrictedZoneResponse;
import org.springframework.stereotype.Component;

@Component
public class RestrictedZonePresentationMapper {
    public RestrictedZoneDto toDto(RestrictedZoneRequest request) {
        return new RestrictedZoneDto(
                null,
                request.getRegion(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    public RestrictedZoneResponse toResponse(RestrictedZoneDto dto) {
        return new RestrictedZoneResponse(
                dto.getId(),
                dto.getRegion(),
                dto.getStartTime(),
                dto.getEndTime()
        );
    }
}
