package com.airflights.passenger.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String passportNumber;
}
