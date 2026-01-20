package com.airflights.auth.application.dto;

public record TokenDto(String token, String tokenType, long expiresIn) {
}
