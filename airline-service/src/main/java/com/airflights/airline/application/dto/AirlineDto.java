package com.airflights.airline.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineDto {
    private Long id;
    private String name;
    private String contactEmail;
}
