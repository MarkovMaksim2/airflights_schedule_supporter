package com.airflights.notification.infrastructure.messaging;

import com.airflights.notification.application.dto.NotificationCommand;
import com.airflights.notification.application.service.CreateNotificationUseCase;
import com.airflights.notification.domain.model.NotificationType;
import com.airflights.notification.application.event.FileDeletedEvent;
import com.airflights.notification.application.event.FileUploadFailedEvent;
import com.airflights.notification.application.event.FileUploadedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileEventsListener {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${notifications.topics.files}")
    public void onFileUploaded(String payload) {
        JsonNode root = parse(payload);
        String ownerId = textOrNull(root, "ownerId");
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("File owner is required for notification");
        }
        String eventType = textOrNull(root, "eventType");
        if ("FileUploadFailed".equals(eventType)) {
            FileUploadFailedEvent event = fromJson(payload, FileUploadFailedEvent.class);
            createNotificationUseCase.create(new NotificationCommand(
                    ownerId,
                    event.eventId(),
                    NotificationType.FILE_UPLOAD_FAILED,
                    toJson(event)
            ));
        } else if ("FileDeleted".equals(eventType)) {
            FileDeletedEvent event = fromJson(payload, FileDeletedEvent.class);
            createNotificationUseCase.create(new NotificationCommand(
                    ownerId,
                    event.eventId(),
                    NotificationType.FILE_DELETED,
                    toJson(event)
            ));
        } else {
            FileUploadedEvent event = fromJson(payload, FileUploadedEvent.class);
            createNotificationUseCase.create(new NotificationCommand(
                    ownerId,
                    event.eventId(),
                    NotificationType.FILE_UPLOADED,
                    toJson(event)
            ));
        }
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize file event payload", ex);
        }
    }

    private <T> T fromJson(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid event payload", ex);
        }
    }

    private JsonNode parse(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid event payload", ex);
        }
    }

    private String textOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value.isBlank() ? null : value;
    }
}
