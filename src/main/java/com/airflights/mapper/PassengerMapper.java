package com.airflights.mapper;

import com.airflights.dto.PassengerDto;
import com.airflights.entity.Passenger;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerDto toDto(Passenger passenger);
    Passenger toEntity(PassengerDto passengerDto);
}
