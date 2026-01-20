package com.airport.unit;

import com.airflights.airport.application.dto.AirportManagerDto;
import com.airflights.airport.application.exception.ResourceNotFoundException;
import com.airflights.airport.application.mapper.AirportManagerMapper;
import com.airflights.airport.application.port.out.AirportManagerRepository;
import com.airflights.airport.application.port.out.AirportRepository;
import com.airflights.airport.application.service.AirportManagerService;
import com.airflights.airport.domain.model.AirportManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportManagerServiceTest {

    @Mock
    private AirportManagerRepository airportManagerRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private AirportManagerService airportManagerService;

    @Mock
    private AirportManagerMapper airportManagerMapper;

    private AirportManagerDto airportManagerDto;
    private AirportManager airportManager;

    @BeforeEach
    void setUp() {
        airportManagerDto = new AirportManagerDto(null, 1L, "manager@airport.com");
        airportManager = new AirportManager(10L, 1L, "manager@airport.com");
    }

    @Test
    void create_whenAirportIdNull_throws() {
        AirportManagerDto invalid = new AirportManagerDto(null, null, "manager@airport.com");
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(invalid))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(airportRepository, airportManagerRepository);
    }

    @Test
    void create_whenUserEmailNull_throws() {
        AirportManagerDto invalid = new AirportManagerDto(null, 1L, null);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(invalid))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(airportRepository, airportManagerRepository);
    }

    @Test
    void create_whenUserEmailBlank_throws() {
        AirportManagerDto invalid = new AirportManagerDto(null, 1L, "   ");
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(invalid))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(airportRepository, airportManagerRepository);
    }

    @Test
    void create_whenAirportMissing_throws() {
        when(airportRepository.existsById(1L)).thenReturn(false);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(airportManagerDto))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(airportRepository).existsById(1L);
        verify(airportManagerRepository, never()).save(any(AirportManager.class));
    }

    @Test
    void create_whenManagerExists_throws() {
        when(airportRepository.existsById(1L)).thenReturn(true);
        when(airportManagerRepository.existsByUserEmailIgnoreCase("manager@airport.com"))
                .thenReturn(true);
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(airportManagerDto))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(airportManagerRepository, never()).save(any(AirportManager.class));
    }

    @Test
    void create_shouldSave() {
        when(airportRepository.existsById(1L)).thenReturn(true);
        when(airportManagerRepository.existsByUserEmailIgnoreCase("manager@airport.com"))
                .thenReturn(false);
        when(airportManagerMapper.toDomain(airportManagerDto)).thenReturn(airportManager);
        when(airportManagerRepository.save(any(AirportManager.class))).thenReturn(airportManager);
        when(airportManagerMapper.toDto(airportManager))
                .thenReturn(new AirportManagerDto(10L, 1L, "manager@airport.com"));
        when(transactionTemplate.execute(Mockito.<TransactionCallback<?>>any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    return callback.doInTransaction(null);
                });

        StepVerifier.create(airportManagerService.create(airportManagerDto))
                .expectNext(new AirportManagerDto(10L, 1L, "manager@airport.com"))
                .verifyComplete();

        verify(airportManagerRepository).save(any(AirportManager.class));
    }

    @Test
    void getByEmail_shouldReturnDto() {
        when(airportManagerRepository.findByUserEmailIgnoreCase("manager@airport.com"))
                .thenReturn(Optional.of(airportManager));
        when(airportManagerMapper.toDto(airportManager))
                .thenReturn(new AirportManagerDto(10L, 1L, "manager@airport.com"));

        StepVerifier.create(airportManagerService.getByEmail("manager@airport.com"))
                .expectNext(new AirportManagerDto(10L, 1L, "manager@airport.com"))
                .verifyComplete();
    }

    @Test
    void getByEmail_whenMissing_throws() {
        when(airportManagerRepository.findByUserEmailIgnoreCase("missing@airport.com"))
                .thenReturn(Optional.empty());

        StepVerifier.create(airportManagerService.getByEmail("missing@airport.com"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void delete_whenMissing_throws() {
        when(airportManagerRepository.existsById(5L)).thenReturn(false);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        StepVerifier.create(airportManagerService.delete(5L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(airportManagerRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenExists_deletes() {
        when(airportManagerRepository.existsById(10L)).thenReturn(true);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        StepVerifier.create(airportManagerService.delete(10L))
                .verifyComplete();

        verify(airportManagerRepository).deleteById(10L);
    }
}
