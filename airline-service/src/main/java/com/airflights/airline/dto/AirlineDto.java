package com.airflights.airline.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@AllArgsConstructor
public class AirlineDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @JsonAlias("name")
    @JsonProperty("name")
    private String name;

    @Email
    @NotBlank
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonAlias("contact_email")
    @JsonProperty(value = "contact_email", access = JsonProperty.Access.READ_ONLY)
    private String contactEmail;
}
