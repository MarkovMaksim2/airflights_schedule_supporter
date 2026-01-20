package com.airflights.passenger.presentation.mapper;

import com.airflights.passenger.application.dto.PassengerDto;
import com.airflights.passenger.presentation.dto.PassengerRequest;
import com.airflights.passenger.presentation.dto.PassengerResponse;
import org.springframework.stereotype.Component;

@Component
public class PassengerPresentationMapper {
    public PassengerDto toDto(PassengerRequest request) {
        return new PassengerDto(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassportNumber()
        );
    }

    public PassengerResponse toResponse(PassengerDto dto) {
        return new PassengerResponse(
                dto.getId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPassportNumber()
        );
    }
}
