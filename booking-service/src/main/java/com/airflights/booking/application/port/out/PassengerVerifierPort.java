package com.airflights.booking.application.port.out;

import com.airflights.booking.application.dto.PassengerSummary;

public interface PassengerVerifierPort {
    void ensurePassengerExists(Long passengerId);
    PassengerSummary getPassengerById(Long id);
    PassengerSummary getPassengerByEmail(String email);
}
