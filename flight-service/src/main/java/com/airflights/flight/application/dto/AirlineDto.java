package com.airflights.flight.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirlineDto {
    private Long id;
    private String name;

    @JsonProperty("contact_email")
    @JsonAlias("contact_email")
    private String contactEmail;
}
