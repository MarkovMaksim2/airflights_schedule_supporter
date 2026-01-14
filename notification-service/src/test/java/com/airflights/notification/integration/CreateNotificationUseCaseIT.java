package com.airflights.notification.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.notification.application.dto.NotificationCommand;
import com.airflights.notification.application.usecase.CreateNotificationUseCase;
import com.airflights.notification.domain.model.Notification;
import com.airflights.notification.domain.model.NotificationType;
import com.airflights.notification.domain.port.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CreateNotificationUseCaseIT.TestConfig.class)
class CreateNotificationUseCaseIT {

    @Configuration
    static class TestConfig {
        @Bean
        CreateNotificationUseCase createNotificationUseCase(NotificationRepository repository) {
            return new CreateNotificationUseCase(repository);
        }
    }

    @MockBean
    private NotificationRepository notificationRepository;

    @Autowired
    private CreateNotificationUseCase createNotificationUseCase;

    @Test
    void create_wiresDependencies() {
        when(notificationRepository.existsByEventId("evt-1")).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        createNotificationUseCase.create(
                new NotificationCommand("user@example.com", "evt-1", NotificationType.BOOKING_CREATED, "{}")
        );

        verify(notificationRepository).save(any(Notification.class));
    }
}
