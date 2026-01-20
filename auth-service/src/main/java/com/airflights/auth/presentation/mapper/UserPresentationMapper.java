package com.airflights.auth.presentation.mapper;

import com.airflights.auth.application.dto.UserCreateDto;
import com.airflights.auth.application.dto.UserDto;
import com.airflights.auth.presentation.dto.UserCreateRequest;
import com.airflights.auth.presentation.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserPresentationMapper {
    public UserCreateDto toCreateDto(UserCreateRequest request) {
        return new UserCreateDto(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRoles()
        );
    }

    public UserResponse toResponse(UserDto dto) {
        return new UserResponse(dto.getId(), dto.getUsername(), dto.getEmail(), dto.getRoles());
    }
}
