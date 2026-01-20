package com.airflights.passenger.unit;

import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.application.exception.ResourceNotFoundException;
import com.airflights.passenger.application.mapper.PassengerMapper;
import com.airflights.passenger.application.port.out.PassengerRepository;
import com.airflights.passenger.application.service.PassengerService;
import com.airflights.passenger.domain.model.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceAdditionalTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerService passengerService;

    private Passenger passenger;
    private PassengerDto passengerDto;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("Ivan");
        passenger.setLastName("Petrov");
        passenger.setEmail("ivan@example.com");
        passenger.setPassportNumber("P123456");

        passengerDto = new PassengerDto();
        passengerDto.setId(1L);
        passengerDto.setFirstName("Ivan");
        passengerDto.setLastName("Petrov");
        passengerDto.setEmail("ivan@example.com");
        passengerDto.setPassportNumber("P123456");
    }

    @Test
    void create_whenPassportAlreadyExists_throws() {
        when(passengerRepository.existsByPassportNumber(passengerDto.getPassportNumber())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> passengerService.create(passengerDto));
        verify(passengerRepository).existsByPassportNumber(passengerDto.getPassportNumber());
        verify(passengerRepository, never()).existsByEmail(anyString());
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void create_whenEmailAlreadyExists_throws() {
        when(passengerRepository.existsByPassportNumber(passengerDto.getPassportNumber())).thenReturn(false);
        when(passengerRepository.existsByEmail(passengerDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> passengerService.create(passengerDto));
        verify(passengerRepository).existsByPassportNumber(passengerDto.getPassportNumber());
        verify(passengerRepository).existsByEmail(passengerDto.getEmail());
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Passenger> passengerPage = new PageImpl<>(List.of(passenger));
        
        when(passengerRepository.findAll(pageable)).thenReturn(passengerPage);
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);

        Page<PassengerDto> result = passengerService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(passengerDto, result.getContent().get(0));
        verify(passengerRepository).findAll(pageable);
    }

    @Test
    void update_success() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);

        PassengerDto result = passengerService.update(1L, passengerDto);

        assertNotNull(result);
        assertEquals(passengerDto, result);
        assertEquals("Ivan", passenger.getFirstName());
        assertEquals("Petrov", passenger.getLastName());
        assertEquals("P123456", passenger.getPassportNumber());
        verify(passengerRepository).findById(1L);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void update_whenNotFound_throws() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> passengerService.update(1L, passengerDto));
        verify(passengerRepository).findById(1L);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }
}
