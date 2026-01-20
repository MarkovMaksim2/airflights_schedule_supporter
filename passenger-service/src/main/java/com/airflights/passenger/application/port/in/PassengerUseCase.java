package com.airflights.passenger.application.port.in;

import com.airflights.passenger.application.dto.PassengerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PassengerUseCase {
    Page<PassengerDto> getAll(Pageable pageable);
    PassengerDto create(PassengerDto dto);
    PassengerDto getById(Long id);
    PassengerDto getByEmail(String email);
    PassengerDto update(Long id, PassengerDto dto);
    void delete(Long id);
}
