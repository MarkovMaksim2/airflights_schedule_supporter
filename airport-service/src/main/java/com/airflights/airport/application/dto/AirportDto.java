package com.airflights.airport.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportDto {
    private Long id;
    private String name;
    private String code;
    private String city;
}
