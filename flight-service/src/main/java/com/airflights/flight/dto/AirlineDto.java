package com.airflights.flight.dto;

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

    @JsonAlias("name")
    @JsonProperty("name")
    private String name;

    @JsonAlias("contact_email")
    @JsonProperty("contact_email")
    private String contactEmail;
}
