package com.airflights.unit.service;

import com.airflights.dto.PassengerDto;
import com.airflights.entity.Passenger;
import com.airflights.mapper.PassengerMapper;
import com.airflights.repository.PassengerRepository;
import com.airflights.service.PassengerService;

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
        when(passengerMapper.toEntity(passengerDto)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toDto(passenger)).thenReturn(passengerDto);
        when(passengerRepository.existsByPassportNumber(passengerDto.getPassportNumber())).thenReturn(false);
        // when(bookingRepository.findAllByPassenger_Id(passengerDto.getId())).thenReturn(Optional.of(new ArrayList<>()));

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

        assertThrows(IllegalArgumentException.class, () -> passengerService.getById(2L));
        verify(passengerRepository, times(1)).findById(2L);
    }

    @Test
    void delete_callsRepository() {
        passengerService.delete(1L);
        verify(passengerRepository, times(1)).deleteById(1L);
    }
}
