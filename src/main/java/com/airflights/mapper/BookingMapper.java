package com.airflights.mapper;

import com.airflights.dto.BookingDto;
import com.airflights.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "passenger.id", target = "passengerId")
    @Mapping(source = "flight.id", target = "flightId")
    BookingDto toDto(Booking booking);

    @Mapping(source = "passengerId", target = "passenger.id")
    @Mapping(source = "flightId", target = "flight.id")
    Booking toEntity(BookingDto bookingDto);
}
