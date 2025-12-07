package com.restrictedzone.unit;

import com.airflights.flight.dto.RestrictedZoneDto;
import com.airflights.flight.service.FlightService;
import com.airflights.restrictedzone.entity.RestrictedZone;
import com.airflights.restrictedzone.mapper.RestrictedZoneMapper;
import com.airflights.restrictedzone.repository.RestrictedZoneRepository;
import com.airflights.restrictedzone.service.RestrictedZoneService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestrictedZoneServiceAdditionalTest {

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
    void create_whenRegionAlreadyExists_throws() {
        when(dto.getRegion()).thenReturn("region-1");
        when(restrictedZoneRepository.existsByRegion("region-1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> restrictedZoneService.create(dto));
        verify(restrictedZoneRepository).existsByRegion("region-1");
        verify(restrictedZoneRepository, never()).save(any(RestrictedZone.class));
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestrictedZone> zonePage = new PageImpl<>(List.of(zone));
        
        when(restrictedZoneRepository.findAll(pageable)).thenReturn(zonePage);
        when(restrictedZoneMapper.toDto(zone)).thenReturn(dto);

        Page<RestrictedZoneDto> result = restrictedZoneService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
        verify(restrictedZoneRepository).findAll(pageable);
    }

    @Test
    void delete_success() {
        doNothing().when(restrictedZoneRepository).deleteById(1L);

        restrictedZoneService.delete(1L);

        verify(restrictedZoneRepository).deleteById(1L);
    }

    @Test
    void getById_success() {
        when(restrictedZoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(restrictedZoneMapper.toDto(zone)).thenReturn(dto);

        RestrictedZoneDto result = restrictedZoneService.getById(1L);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(restrictedZoneRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_throws() {
        when(restrictedZoneRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restrictedZoneService.getById(1L));
        verify(restrictedZoneRepository).findById(1L);
    }
}
