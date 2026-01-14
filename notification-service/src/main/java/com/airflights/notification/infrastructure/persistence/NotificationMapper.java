package com.airflights.notification.infrastructure.persistence;

import com.airflights.notification.domain.model.Notification;
import com.airflights.notification.domain.model.NotificationStatus;
import com.airflights.notification.domain.model.NotificationType;

public final class NotificationMapper {
    private NotificationMapper() {}

    public static NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.id());
        entity.setUserId(notification.userId());
        entity.setEventId(notification.eventId());
        entity.setType(notification.type().name());
        entity.setPayload(notification.payload());
        entity.setStatus(notification.status().name());
        entity.setRetries(notification.retries());
        entity.setCreatedAt(notification.createdAt());
        return entity;
    }

    public static Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUserId(),
                entity.getEventId(),
                NotificationType.valueOf(entity.getType()),
                entity.getPayload(),
                NotificationStatus.valueOf(entity.getStatus()),
                entity.getRetries(),
                entity.getCreatedAt()
        );
    }
}
