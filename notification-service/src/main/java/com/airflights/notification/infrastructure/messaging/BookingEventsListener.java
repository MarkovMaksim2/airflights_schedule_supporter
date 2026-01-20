package com.airflights.notification.infrastructure.messaging;

import com.airflights.notification.application.dto.NotificationCommand;
import com.airflights.notification.application.service.CreateNotificationUseCase;
import com.airflights.notification.domain.model.NotificationType;
import com.airflights.notification.application.event.BookingCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventsListener {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${notifications.topics.booking}")
    public void onBookingCreated(String payload) {
        BookingCreatedEvent event = fromJson(payload, BookingCreatedEvent.class);
        if (event.passengerEmail() == null || event.passengerEmail().isBlank()) {
            throw new IllegalArgumentException("Passenger email is required for notification");
        }
        NotificationCommand command = new NotificationCommand(
                event.passengerEmail(),
                event.eventId(),
                NotificationType.BOOKING_CREATED,
                toJson(event)
        );
        createNotificationUseCase.create(command);
    }

    private String toJson(BookingCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize booking event payload", ex);
        }
    }

    private <T> T fromJson(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid event payload", ex);
        }
    }
}
