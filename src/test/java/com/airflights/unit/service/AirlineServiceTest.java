package com.airflights.unit.service;

import com.airflights.dto.AirlineDto;
import com.airflights.entity.Airline;
import com.airflights.mapper.AirlineMapper;
import com.airflights.repository.AirlineRepository;
import com.airflights.service.AirlineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirlineMapper airlineMapper;

    @InjectMocks
    private AirlineService airlineService;

    private Airline airline;
    private AirlineDto airlineDto;

    @BeforeEach
    void setUp() {
        airline = new Airline();
        airline.setId(1L);
        airline.setName("BestAir");
        airline.setContactEmail("ops@bestair.com");
        airline.setFlights(new ArrayList<>());

        airlineDto = new AirlineDto();
        airlineDto.setId(1L);
        airlineDto.setName("BestAir");
        airlineDto.setContactEmail("ops@bestair.com");
    }

    @Test
    void create_whenNameExists_throws() {
        when(airlineRepository.existsByName("BestAir")).thenReturn(true);

        airlineDto.setName("BestAir");
        assertThrows(IllegalArgumentException.class, () -> airlineService.create(airlineDto));
        verify(airlineRepository, times(1)).existsByName("BestAir");
    }

    @Test
    void create_shouldSave() {
        when(airlineRepository.existsByName("BestAir")).thenReturn(false);
        when(airlineMapper.toEntity(airlineDto, new ArrayList<>())).thenReturn(airline);
        when(airlineRepository.save(airline)).thenReturn(airline);
        when(airlineMapper.toDto(airline)).thenReturn(airlineDto);

        AirlineDto created = airlineService.create(airlineDto);
        assertEquals("BestAir", created.getName());
        verify(airlineRepository, times(1)).save(airline);
    }

    @Test
    void getById_whenMissing_throwsEntityNotFound() {
        when(airlineRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(javax.persistence.EntityNotFoundException.class, () -> airlineService.getById(99L));
    }
}
