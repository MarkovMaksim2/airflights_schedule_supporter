package com.airflights.auth.presentation.dto;

public record TokenResponse(String token, String tokenType, long expiresIn) {
}
