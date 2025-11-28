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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Airline saved = new Airline(1L, "BestAir", "ops@bestair.com");

        when(airlineRepository.existsByName(anyString())).thenReturn(Mono.just(false));
        when(airlineRepository.existsByContactEmail(anyString())).thenReturn(Mono.just(false));
        when(airlineMapper.toEntity(any(AirlineDto.class)))
                .thenAnswer(invocation -> {
                    AirlineDto dto = invocation.getArgument(0);
                    return new Airline(null, dto.getName(), dto.getContactEmail());
                });
        when(airlineRepository.save(any(Airline.class))).thenReturn(Mono.just(saved));
        when(airlineMapper.toDto(any(Airline.class)))
                .thenAnswer(invocation -> {
                    Airline a = invocation.getArgument(0);
                    return new AirlineDto(a.getId(), a.getName(), a.getContactEmail());
                });

        StepVerifier.create(airlineService.create(new AirlineDto(null, "BestAir", "ops@bestair.com")))
                .assertNext(dto -> {
                    assertEquals(1L, dto.getId());
                    assertEquals("BestAir", dto.getName());
                    assertEquals("ops@bestair.com", dto.getContactEmail());
                })
                .verifyComplete();

        verify(airlineRepository).save(any(Airline.class));
    }
}
