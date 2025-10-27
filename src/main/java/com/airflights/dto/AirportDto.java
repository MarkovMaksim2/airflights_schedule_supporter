package com.airflights.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

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
