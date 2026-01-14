package com.airflights.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    boolean existsByEventId(String eventId);
}
