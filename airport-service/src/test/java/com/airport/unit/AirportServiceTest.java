package com.airport.unit;

import com.airflights.airport.dto.AirportDto;
import com.airflights.airport.entity.Airport;
import com.airflights.airport.exception.ResourceNotFoundException;
import com.airflights.airport.mapper.AirportMapper;
import com.airflights.airport.repository.AirportRepository;
import com.airflights.airport.service.AirportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirportMapper airportMapper;

    @Mock
    private TransactionTemplate transactionTemplate;

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
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenThrow(new IllegalArgumentException("Airport with code 'SVO' already exists"));

        StepVerifier.create(airportService.create(airportDto))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void create_shouldSave() {
        when(airportRepository.existsByCode("SVO")).thenReturn(false);
        when(airportMapper.toEntity(airportDto)).thenReturn(airport);
        when(airportRepository.save(airport)).thenReturn(airport);
        when(airportMapper.toDto(airport)).thenReturn(airportDto);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.create(airportDto))
                .expectNext(airportDto)
                .verifyComplete();
    }

    @Test
    void getById_whenMissing_throwsEntityNotFound() {
        when(airportRepository.findById(999L)).thenReturn(Optional.empty());

        StepVerifier.create(airportService.getById(999L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}