package com.airflights.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AirlineDto {
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String contactEmail;
}
