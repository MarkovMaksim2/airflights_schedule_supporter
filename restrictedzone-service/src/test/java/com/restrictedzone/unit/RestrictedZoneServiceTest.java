package com.restrictedzone.unit;

import com.airflights.restrictedzone.dto.RestrictedZoneDto;
import com.airflights.restrictedzone.entity.RestrictedZone;
import com.airflights.restrictedzone.mapper.RestrictedZoneMapper;
import com.airflights.restrictedzone.repository.RestrictedZoneRepository;
import com.airflights.restrictedzone.service.RestrictedZoneService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestrictedZoneServiceTest {

    @Mock
    private RestrictedZoneRepository restrictedZoneRepository;

    @Mock
    private RestrictedZoneMapper restrictedZoneMapper;

    @InjectMocks
    private RestrictedZoneService restrictedZoneService;

    private RestrictedZone zone;
    private RestrictedZoneDto dto;

    @BeforeEach
    void setUp() {
        zone = new RestrictedZone();
        zone.setId(1L);
        zone.setRegion("region-1");
        zone.setStartTime(LocalDateTime.now());
        zone.setEndTime(LocalDateTime.now().plusHours(4));

        dto = new RestrictedZoneDto();
        dto.setId(1L);
        dto.setRegion("region-1");
        dto.setStartTime(zone.getStartTime());
        dto.setEndTime(zone.getEndTime());
    }

    @Test
    void create_shouldSave() {
        when(restrictedZoneMapper.toEntity(dto)).thenReturn(zone);
        when(restrictedZoneRepository.save(zone)).thenReturn(zone);
        when(restrictedZoneMapper.toDto(zone)).thenReturn(dto);

        RestrictedZoneDto saved = restrictedZoneService.create(dto);

        assertNotNull(saved);
        verify(restrictedZoneRepository).save(zone);
    }
}
