package com.airflights.auth.presentation.controller;

import com.airflights.auth.application.port.in.UserUseCase;
import com.airflights.auth.application.security.UserPrincipal;
import com.airflights.auth.presentation.dto.UserCreateRequest;
import com.airflights.auth.presentation.dto.UserResponse;
import com.airflights.auth.presentation.mapper.UserPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserPresentationMapper userPresentationMapper;

    public UserController(UserUseCase userUseCase, UserPresentationMapper userPresentationMapper) {
        this.userUseCase = userUseCase;
        this.userPresentationMapper = userPresentationMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(
                userPresentationMapper.toResponse(
                        userUseCase.create(userPresentationMapper.toCreateDto(request))
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                userPresentationMapper.toResponse(
                        userUseCase.getByUsername(principal.getUsername())
                )
        );
    }
}
