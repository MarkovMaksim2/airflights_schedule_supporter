package com.airflights.unit.service;

import com.airflights.dto.AirportDto;
import com.airflights.entity.Airport;
import com.airflights.mapper.AirportMapper;
import com.airflights.repository.AirportRepository;
import com.airflights.service.AirportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityNotFoundException.*;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirportMapper airportMapper;

    @InjectMocks
    private AirportService airportService;

    private Airport airport;
    private AirportDto airportDto;

    @BeforeEach
    void setUp() {
        airport = new Airport();
        airport.setId(1L);
        airport.setName("Sheremetyevo");
        airport.setCode("SVO");
        airport.setCity("Moscow");

        airportDto = new AirportDto();
        airportDto.setId(1L);
        airportDto.setName("Sheremetyevo");
        airportDto.setCode("SVO");
        airportDto.setCity("Moscow");
    }

    @Test
    void create_whenCodeExists_throws() {
        when(airportRepository.existsByCode("SVO")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> airportService.create(airportDto));
        verify(airportRepository, times(1)).existsByCode("SVO");
    }

    @Test
    void create_shouldSave() {
        when(airportRepository.existsByCode("SVO")).thenReturn(false);
        when(airportMapper.toEntity(airportDto, new ArrayList<>(), new ArrayList<>())).thenReturn(airport);
        when(airportRepository.save(airport)).thenReturn(airport);
        when(airportMapper.toDto(airport)).thenReturn(airportDto);

        AirportDto created = airportService.create(airportDto);
        assertEquals("SVO", created.getCode());
        verify(airportRepository, times(1)).save(airport);
    }

    @Test
    void getById_whenMissing_throwsEntityNotFound() {
        when(airportRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> airportService.getById(999L));
    }
}
