package com.airflights.airline.unit;

import com.airflights.airline.dto.AirlineDto;
import com.airflights.airline.entity.Airline;
import com.airflights.airline.exception.ResourceNotFoundException;
import com.airflights.airline.mapper.AirlineMapper;
import com.airflights.airline.repository.AirlineRepository;
import com.airflights.airline.service.AirlineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
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

    private AirlineDto airlineDto;

    @BeforeEach
    void setUp() {
        airlineDto = new AirlineDto(1L, "BestAir", "ops@bestair.com");
    }

    @Test
    void getAll_shouldApplyPaging() {
        when(airlineRepository.findAll()).thenReturn(Flux.just(
                new Airline(1L, "A1", "a1@mail.com"),
                new Airline(2L, "A2", "a2@mail.com"),
                new Airline(3L, "A3", "a3@mail.com"),
                new Airline(4L, "A4", "a4@mail.com")
        ));
        when(airlineMapper.toDto(any(Airline.class)))
                .thenAnswer(invocation -> {
                    Airline airline = invocation.getArgument(0);
                    return new AirlineDto(airline.getId(), airline.getName(), airline.getContactEmail());
                });

        StepVerifier.create(airlineService.getAll(PageRequest.of(1, 2)))
                .assertNext(dto -> assertEquals(3L, dto.getId()))
                .assertNext(dto -> assertEquals(4L, dto.getId()))
                .verifyComplete();

        verify(airlineRepository).findAll();
        verify(airlineMapper, times(2)).toDto(any(Airline.class));
    }

    @Test
    void getById_shouldReturnDto() {
        Airline airline = new Airline(1L, "BestAir", "ops@bestair.com");
        when(airlineRepository.findById(1L)).thenReturn(Mono.just(airline));
        when(airlineMapper.toDto(airline)).thenReturn(airlineDto);

        StepVerifier.create(airlineService.getById(1L))
                .expectNext(airlineDto)
                .verifyComplete();

        verify(airlineRepository).findById(1L);
        verify(airlineMapper).toDto(airline);
    }

    @Test
    void getById_whenMissing_throws() {
        when(airlineRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.getById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
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
    void create_whenContactEmailExists_throws() {
        when(airlineRepository.existsByName("BestAir")).thenReturn(Mono.just(false));
        when(airlineRepository.existsByContactEmail("ops@bestair.com")).thenReturn(Mono.just(true));

        StepVerifier.create(airlineService.create(airlineDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airlineRepository).existsByContactEmail("ops@bestair.com");
        verify(airlineRepository, never()).save(any(Airline.class));
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

    @Test
    void create_whenNameAndContactNull_skipsUniquenessChecks() {
        AirlineDto empty = new AirlineDto(null, null, null);
        Airline saved = new Airline(5L, null, null);

        when(airlineMapper.toEntity(any(AirlineDto.class))).thenReturn(new Airline(null, null, null));
        when(airlineRepository.save(any(Airline.class))).thenReturn(Mono.just(saved));
        when(airlineMapper.toDto(any(Airline.class))).thenReturn(new AirlineDto(5L, null, null));

        StepVerifier.create(airlineService.create(empty))
                .assertNext(dto -> assertEquals(5L, dto.getId()))
                .verifyComplete();

        verify(airlineRepository, never()).existsByName(anyString());
        verify(airlineRepository, never()).existsByContactEmail(anyString());
    }

    @Test
    void update_whenMissing_throws() {
        when(airlineRepository.findById(44L)).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.update(44L, airlineDto))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void update_whenNameExists_throws() {
        Airline existing = new Airline(1L, "Old", "old@mail.com");
        AirlineDto update = new AirlineDto(null, "New", "new@mail.com");

        when(airlineRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(airlineRepository.existsByName("New")).thenReturn(Mono.just(true));

        StepVerifier.create(airlineService.update(1L, update))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void update_whenNameChanges_updatesFields() {
        Airline existing = new Airline(1L, "Old", "old@mail.com");
        AirlineDto update = new AirlineDto(null, "New", "new@mail.com");

        when(airlineRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(airlineRepository.existsByName("New")).thenReturn(Mono.just(false));
        when(airlineRepository.save(any(Airline.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(airlineMapper.toDto(any(Airline.class)))
                .thenAnswer(invocation -> {
                    Airline airline = invocation.getArgument(0);
                    return new AirlineDto(airline.getId(), airline.getName(), airline.getContactEmail());
                });

        StepVerifier.create(airlineService.update(1L, update))
                .assertNext(dto -> {
                    assertEquals("New", dto.getName());
                    assertEquals("new@mail.com", dto.getContactEmail());
                })
                .verifyComplete();

        verify(airlineRepository).save(any(Airline.class));
    }

    @Test
    void update_whenNameUnchanged_updatesContactOnly() {
        Airline existing = new Airline(2L, "Same", "old@mail.com");
        AirlineDto update = new AirlineDto(null, "Same", "new@mail.com");

        when(airlineRepository.findById(2L)).thenReturn(Mono.just(existing));
        when(airlineRepository.save(any(Airline.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(airlineMapper.toDto(any(Airline.class)))
                .thenAnswer(invocation -> {
                    Airline airline = invocation.getArgument(0);
                    return new AirlineDto(airline.getId(), airline.getName(), airline.getContactEmail());
                });

        StepVerifier.create(airlineService.update(2L, update))
                .assertNext(dto -> assertEquals("new@mail.com", dto.getContactEmail()))
                .verifyComplete();

        verify(airlineRepository, never()).existsByName(anyString());
    }

    @Test
    void delete_whenMissing_throws() {
        when(airlineRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(airlineService.delete(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(airlineRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenExists_deletes() {
        when(airlineRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(airlineRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.delete(1L))
                .verifyComplete();

        verify(airlineRepository).deleteById(1L);
    }
}
