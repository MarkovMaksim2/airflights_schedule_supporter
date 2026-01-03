package com.airflights.auth.dto;

public record TokenResponse(String token, String tokenType, long expiresIn) {
}
