package com.airflights.passenger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String passportNumber;
}
