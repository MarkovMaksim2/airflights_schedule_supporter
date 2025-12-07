package com.airflights.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class UserDto {
    private final Long id;
    private final String username;
    private final Set<String> roles;
    private final Long employeeId;
    private final Set<Long> managedDeptIds;
    private final boolean enabled;

}