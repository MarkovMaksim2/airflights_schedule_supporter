package com.airflights.airline.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirlineResponse {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact_email")
    private String contactEmail;
}
