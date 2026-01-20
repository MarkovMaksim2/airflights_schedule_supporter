package com.airport.unit;

import com.airflights.airport.application.dto.AirportDto;
import com.airflights.airport.application.exception.ResourceNotFoundException;
import com.airflights.airport.application.mapper.AirportMapper;
import com.airflights.airport.application.port.out.AirportRepository;
import com.airflights.airport.application.service.AirportService;
import com.airflights.airport.domain.model.Airport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
    void getAll_shouldReturnPagedResults() {
        Airport airport2 = new Airport();
        airport2.setId(2L);
        airport2.setName("Domodedovo");
        airport2.setCode("DME");
        airport2.setCity("Moscow");

        when(airportRepository.findAll(0, 2))
                .thenReturn(List.of(airport, airport2));
        when(airportMapper.toDto(airport)).thenReturn(airportDto);
        when(airportMapper.toDto(airport2)).thenReturn(
                new AirportDto(2L, "Domodedovo", "DME", "Moscow")
        );
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.getAll(0, 2))
                .expectNext(airportDto)
                .expectNext(new AirportDto(2L, "Domodedovo", "DME", "Moscow"))
                .verifyComplete();

        verify(airportRepository).findAll(0, 2);
    }

    @Test
    void getById_shouldReturnDto() {
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportMapper.toDto(airport)).thenReturn(airportDto);

        StepVerifier.create(airportService.getById(1L))
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

    @Test
    void create_whenCodeExists_throws() {
        when(airportRepository.existsByCode("SVO")).thenReturn(true);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.create(airportDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airportRepository).existsByCode("SVO");
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void create_whenNameExists_throws() {
        when(airportRepository.existsByCode("SVO")).thenReturn(false);
        when(airportRepository.existsByName("Sheremetyevo")).thenReturn(true);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.create(airportDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airportRepository).existsByName("Sheremetyevo");
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void create_shouldSave() {
        when(airportRepository.existsByCode("SVO")).thenReturn(false);
        when(airportMapper.toDomain(airportDto)).thenReturn(airport);
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
    void create_whenNameNull_skipsNameCheck() {
        AirportDto noName = new AirportDto(1L, null, "SVO", "Moscow");
        when(airportRepository.existsByCode("SVO")).thenReturn(false);
        when(airportMapper.toDomain(noName)).thenReturn(airport);
        when(airportRepository.save(airport)).thenReturn(airport);
        when(airportMapper.toDto(airport)).thenReturn(noName);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.create(noName))
                .expectNext(noName)
                .verifyComplete();

        verify(airportRepository, never()).existsByName(anyString());
    }

    @Test
    void update_whenMissing_throws() {
        when(airportRepository.findById(77L)).thenReturn(Optional.empty());
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.update(77L, airportDto))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void update_whenCodeExists_throws() {
        AirportDto update = new AirportDto(1L, "Sheremetyevo", "NEW", "Moscow");
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportRepository.existsByCode("NEW")).thenReturn(true);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.update(1L, update))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void update_whenNameExists_throws() {
        AirportDto update = new AirportDto(1L, "NewName", "SVO", "Moscow");
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportRepository.existsByName("NewName")).thenReturn(true);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.update(1L, update))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void update_whenFieldsChange_updatesEntity() {
        AirportDto update = new AirportDto(1L, "Updated", "DME", "Saint Petersburg");
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportRepository.existsByCode("DME")).thenReturn(false);
        when(airportRepository.existsByName("Updated")).thenReturn(false);
        when(airportRepository.save(any(Airport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(airportMapper.toDto(any(Airport.class))).thenAnswer(invocation -> {
            Airport saved = invocation.getArgument(0);
            return new AirportDto(saved.getId(), saved.getName(), saved.getCode(), saved.getCity());
        });
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.update(1L, update))
                .expectNext(new AirportDto(1L, "Updated", "DME", "Saint Petersburg"))
                .verifyComplete();

        verify(airportRepository).save(any(Airport.class));
    }

    @Test
    void update_whenCityNull_skipsCityUpdate() {
        AirportDto update = new AirportDto(1L, "Sheremetyevo", "SVO", null);
        when(airportRepository.findById(1L)).thenReturn(Optional.of(airport));
        when(airportRepository.save(any(Airport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(airportMapper.toDto(any(Airport.class))).thenAnswer(invocation -> {
            Airport saved = invocation.getArgument(0);
            return new AirportDto(saved.getId(), saved.getName(), saved.getCode(), saved.getCity());
        });
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportService.update(1L, update))
                .expectNext(new AirportDto(1L, "Sheremetyevo", "SVO", "Moscow"))
                .verifyComplete();
    }

    @Test
    void delete_whenMissing_throws() {
        when(airportRepository.existsById(9L)).thenReturn(false);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        StepVerifier.create(airportService.delete(9L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(airportRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenExists_deletes() {
        when(airportRepository.existsById(1L)).thenReturn(true);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        StepVerifier.create(airportService.delete(1L))
                .verifyComplete();

        verify(airportRepository).deleteById(1L);
    }

    @Test
    void count_shouldReturnCount() {
        when(airportRepository.count()).thenReturn(5L);

        StepVerifier.create(airportService.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void findByCode_shouldReturnDto() {
        when(airportRepository.findByCode("SVO")).thenReturn(Optional.of(airport));
        when(airportMapper.toDto(airport)).thenReturn(airportDto);

        StepVerifier.create(airportService.findByCode("SVO"))
                .expectNext(airportDto)
                .verifyComplete();
    }

    @Test
    void findByCode_whenMissing_throws() {
        when(airportRepository.findByCode("XXX")).thenReturn(Optional.empty());

        StepVerifier.create(airportService.findByCode("XXX"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
