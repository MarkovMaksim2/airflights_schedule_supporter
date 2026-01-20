package com.airflights.notification.application.service;

import com.airflights.notification.application.dto.NotificationCommand;
import com.airflights.notification.domain.model.Notification;
import com.airflights.notification.domain.model.NotificationStatus;
import com.airflights.notification.domain.port.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateNotificationUseCase {

    private final NotificationRepository notificationRepository;

    public void create(NotificationCommand command) {
        if (notificationRepository.existsByEventId(command.eventId())) {
            return;
        }
        Notification notification = new Notification(
                null,
                command.userId(),
                command.eventId(),
                command.type(),
                command.payload(),
                NotificationStatus.NEW,
                0,
                LocalDateTime.now()
        );
        notificationRepository.save(notification);
    }
}
