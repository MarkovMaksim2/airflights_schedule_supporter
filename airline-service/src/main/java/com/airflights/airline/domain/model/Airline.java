package com.airflights.airline.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airline {
    private Long id;
    private String name;
    private String contactEmail;
}
