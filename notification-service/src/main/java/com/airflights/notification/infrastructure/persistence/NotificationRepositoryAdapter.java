package com.airflights.notification.infrastructure.persistence;

import com.airflights.notification.domain.model.Notification;
import com.airflights.notification.domain.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = NotificationMapper.toEntity(notification);
        NotificationEntity saved = notificationJpaRepository.save(entity);
        return NotificationMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEventId(String eventId) {
        return notificationJpaRepository.existsByEventId(eventId);
    }
}
