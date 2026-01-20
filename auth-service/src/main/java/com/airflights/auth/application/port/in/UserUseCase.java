package com.airflights.auth.application.port.in;

import com.airflights.auth.application.dto.UserCreateDto;
import com.airflights.auth.application.dto.UserDto;

public interface UserUseCase {
    UserDto create(UserCreateDto request);
    UserDto getByUsername(String username);
}
