package com.airflights.auth.presentation.dto;

import com.airflights.auth.domain.model.Role;

import java.util.Set;

public record UserResponse(Long id, String username, String email, Set<Role> roles) {
}
