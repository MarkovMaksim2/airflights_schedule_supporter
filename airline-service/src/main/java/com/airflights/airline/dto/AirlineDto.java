package com.airflights.airline.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineDto {
    @JsonAlias("id")
    @JsonProperty("id")
    private Long id;

    @NotBlank
    @JsonAlias("name")
    @JsonProperty("name")
    private String name;

    @Email
    @NotBlank
    @JsonAlias("contact_email")
    @JsonProperty("contact_email")
    private String contactEmail;
}
