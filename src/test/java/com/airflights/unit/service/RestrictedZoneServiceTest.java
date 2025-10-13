package com.airflights.unit.service;

import com.airflights.dto.RestrictedZoneDto;
import com.airflights.entity.RestrictedZone;
import com.airflights.mapper.RestrictedZoneMapper;
import com.airflights.repository.RestrictedZoneRepository;
import com.airflights.service.FlightService;
import com.airflights.service.RestrictedZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestrictedZoneServiceTest {

    @Mock
    private RestrictedZoneRepository restrictedZoneRepository;

    @Mock
    private RestrictedZoneMapper restrictedZoneMapper;

    @Mock
    private FlightService flightService;

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
    void create_shouldSaveAndUpdateFlights() {
        when(restrictedZoneMapper.toEntity(dto)).thenReturn(zone);
        when(restrictedZoneRepository.save(zone)).thenReturn(zone);
        when(restrictedZoneMapper.toDto(zone)).thenReturn(dto);

        RestrictedZoneDto saved = restrictedZoneService.create(dto);

        assertNotNull(saved);
        verify(restrictedZoneRepository, times(1)).save(zone);
        verify(flightService, times(1)).updateFlightsDueToRestriction(zone);
    }
}
