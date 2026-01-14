package com.airflights.notification.domain.port;

import com.airflights.notification.domain.model.Notification;

public interface NotificationRepository {
    Notification save(Notification notification);
    boolean existsByEventId(String eventId);
}
