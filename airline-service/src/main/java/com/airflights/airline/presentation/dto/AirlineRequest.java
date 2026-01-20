package com.airflights.airline.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineRequest {
    @NotBlank
    @JsonAlias("name")
    @JsonProperty("name")
    private String name;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(value = "contact_email", access = JsonProperty.Access.READ_ONLY)
    private String contactEmail;
}
