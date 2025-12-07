package com.airflights.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class LoginRequestDto {

    @NotBlank
    @Size(min = 3, max = 32)
    private final String username;

    @NotBlank
    @Size(min = 8, max = 32)
    private final String password;
}
