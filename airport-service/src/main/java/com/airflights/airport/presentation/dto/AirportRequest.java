package com.airflights.airport.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportRequest {
    @NotBlank
    @JsonAlias("name")
    @JsonProperty("name")
    private String name;

    @NotBlank
    @JsonAlias("code")
    @JsonProperty("code")
    private String code;

    @NotBlank
    @JsonAlias("city")
    @JsonProperty("city")
    private String city;
}
