package com.airflights.auth.application.mapper;

import com.airflights.auth.application.dto.UserCreateDto;
import com.airflights.auth.application.dto.UserDto;
import com.airflights.auth.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

    public User toDomain(UserCreateDto createDto) {
        User user = new User();
        user.setUsername(createDto.getUsername());
        user.setEmail(createDto.getEmail());
        user.setRoles(createDto.getRoles());
        return user;
    }
}
