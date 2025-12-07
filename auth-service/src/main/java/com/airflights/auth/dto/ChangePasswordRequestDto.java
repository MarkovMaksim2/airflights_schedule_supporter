package com.airflights.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ChangePasswordRequestDto {

    @NotBlank
    @Size(min = 8, max = 32)
    @JsonProperty("new_password")
    private final String newPassword;
}
