package com.airflights.airline.unit;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.entity.Airline;
import com.airflights.airline.mapper.AirlineMapper;
import com.airflights.airline.repository.AirlineRepository;
import com.airflights.airline.service.AirlineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository; // REACTIVE

    @Mock
    private AirlineMapper airlineMapper;

    @InjectMocks
    private AirlineService airlineService;

    private Airline airline;
    private AirlineDto airlineDto;

    @BeforeEach
    void setUp() {
        airline = new Airline(1L, "BestAir", "ops@bestair.com");
        airlineDto = new AirlineDto(1L, "BestAir", "ops@bestair.com");
    }

    @Test
    void create_whenNameExists_throws() {
        when(airlineRepository.existsByName("BestAir")).thenReturn(Mono.just(true));

        StepVerifier.create(airlineService.create(airlineDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airlineRepository).existsByName("BestAir");
    }

    @Test
    void create_shouldSave() {
        when(airlineRepository.existsByName("BestAir")).thenReturn(Mono.just(false));
        when(airlineMapper.toEntity(airlineDto)).thenReturn(airline);
        when(airlineRepository.save(airline)).thenReturn(Mono.just(airline));
        when(airlineMapper.toDto(airline)).thenReturn(airlineDto);

        StepVerifier.create(airlineService.create(airlineDto))
                .expectNext(airlineDto)
                .verifyComplete();

        verify(airlineRepository).save(airline);
    }

    @Test
    void getById_whenMissing_throws() {
        when(airlineRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.getById(99L))
                .expectError(jakarta.persistence.EntityNotFoundException.class)
                .verify();
    }
}
