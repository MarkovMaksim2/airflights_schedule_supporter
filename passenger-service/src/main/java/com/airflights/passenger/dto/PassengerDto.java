package com.airflights.passenger.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @JsonAlias("first_name")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @JsonAlias("last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Email
    @NotBlank
    @JsonAlias("email")
    @JsonProperty("email")
    private String email;

    @NotBlank
    @JsonAlias("passport_number")
    @JsonProperty("passport_number")
    private String passportNumber;
}
