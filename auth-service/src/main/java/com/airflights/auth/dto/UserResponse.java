package com.airflights.auth.dto;

import com.airflights.auth.entity.Role;

import java.util.Set;

public record UserResponse(Long id, String username, String email, Set<Role> roles) {
}
