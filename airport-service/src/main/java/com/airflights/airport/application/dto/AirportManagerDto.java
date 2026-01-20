package com.airflights.airport.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManagerDto {
    private Long id;
    private Long airportId;
    private String userEmail;
}
