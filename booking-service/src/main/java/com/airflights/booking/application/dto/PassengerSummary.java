package com.airflights.booking.application.dto;

public record PassengerSummary(Long id, String email) {
    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
}
