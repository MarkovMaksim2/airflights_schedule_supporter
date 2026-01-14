package com.airflights.notification.interfaces.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.airflights.notification.application.usecase.CreateNotificationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BookingEventsListenerTest {

    @Test
    void onBookingCreated_createsNotification() throws Exception {
        CreateNotificationUseCase useCase = Mockito.mock(CreateNotificationUseCase.class);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        BookingEventsListener listener = new BookingEventsListener(useCase, objectMapper);

        String payload = """
                {
                  "eventId": "evt-1",
                  "bookingId": 1,
                  "passengerId": 2,
                  "flightId": 3,
                  "bookingTime": "2025-01-01T10:00:00",
                  "passengerEmail": "user@example.com"
                }
                """;

        listener.onBookingCreated(payload);

        verify(useCase).create(any());
    }
}
