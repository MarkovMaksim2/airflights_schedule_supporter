package com.airflights.auth.application.dto;

import com.airflights.auth.domain.model.Role;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}
