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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

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
        passenger.setLastName("Petrob");
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
    void create_shouldSaveAndReturnDto() {
        when(passengerMapper.toDomain(passengerDto)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);
        when(passengerRepository.existsByPassportNumber(passengerDto.getPassportNumber())).thenReturn(false);
        when(passengerRepository.existsByEmail(passengerDto.getEmail())).thenReturn(false);

        PassengerDto created = passengerService.create(passengerDto);

        assertNotNull(created);
        assertEquals("Ivan", created.getFirstName());
        assertEquals("Petrov", created.getLastName());
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void getById_whenExists_returnsDto() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);

        PassengerDto found = passengerService.getById(1L);

        assertNotNull(found);
        assertEquals("ivan@example.com", found.getEmail());
        verify(passengerRepository, times(1)).findById(1L);
    }

    @Test
    void getById_whenNotFound_throws() {
        when(passengerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> passengerService.getById(2L));
        verify(passengerRepository, times(1)).findById(2L);
    }

    @Test
    void delete_callsRepository() {
        passengerService.delete(1L);
        verify(passengerRepository, times(1)).deleteById(1L);
    }

    @Test
    void getByEmail_whenExists_returnsDto() {
        when(passengerRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(passenger));
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);

        PassengerDto found = passengerService.getByEmail("ivan@example.com");

        assertNotNull(found);
        assertEquals("ivan@example.com", found.getEmail());
        verify(passengerRepository).findByEmail("ivan@example.com");
    }

    @Test
    void getByEmail_whenNotFound_throws() {
        when(passengerRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> passengerService.getByEmail("missing@example.com"));
    }
}
