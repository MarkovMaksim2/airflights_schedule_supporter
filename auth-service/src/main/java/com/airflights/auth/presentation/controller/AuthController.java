package com.airflights.auth.presentation.controller;

import com.airflights.auth.application.port.in.AuthUseCase;
import com.airflights.auth.presentation.dto.LoginRequest;
import com.airflights.auth.presentation.dto.TokenResponse;
import com.airflights.auth.presentation.mapper.AuthPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final AuthPresentationMapper authPresentationMapper;

    public AuthController(AuthUseCase authUseCase, AuthPresentationMapper authPresentationMapper) {
        this.authUseCase = authUseCase;
        this.authPresentationMapper = authPresentationMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authPresentationMapper.toResponse(
                        authUseCase.login(request.getUsername(), request.getPassword())
                )
        );
    }
}
