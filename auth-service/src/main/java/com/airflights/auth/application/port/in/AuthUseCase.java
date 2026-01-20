package com.airflights.auth.application.port.in;

import com.airflights.auth.application.dto.TokenDto;

public interface AuthUseCase {
    TokenDto login(String username, String password);
}
