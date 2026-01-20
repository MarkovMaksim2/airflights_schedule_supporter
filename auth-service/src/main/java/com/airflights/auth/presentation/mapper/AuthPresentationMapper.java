package com.airflights.auth.presentation.mapper;

import com.airflights.auth.application.dto.TokenDto;
import com.airflights.auth.presentation.dto.TokenResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthPresentationMapper {
    public TokenResponse toResponse(TokenDto dto) {
        return new TokenResponse(dto.token(), dto.tokenType(), dto.expiresIn());
    }
}
