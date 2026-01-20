package com.airflights.restrictedzone.application.port.in;

import com.airflights.restrictedzone.application.dto.RestrictedZoneDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestrictedZoneUseCase {
    Page<RestrictedZoneDto> getAll(Pageable pageable);
    RestrictedZoneDto getById(Long id);
    RestrictedZoneDto create(RestrictedZoneDto dto);
    void delete(Long id);
}
