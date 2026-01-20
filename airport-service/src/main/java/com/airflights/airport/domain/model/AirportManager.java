package com.airflights.airport.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportManager {
    private Long id;
    private Long airportId;
    private String userEmail;
}
