package com.airflights.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Data
@Builder
@Jacksonized
public class UpdateUserRolesRequestDto {
    private final Set<String> roles;
}