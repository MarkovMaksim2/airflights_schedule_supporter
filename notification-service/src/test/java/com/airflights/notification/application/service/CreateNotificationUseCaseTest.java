package com.airflights.notification.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.airflights.notification.application.dto.NotificationCommand;
import com.airflights.notification.domain.model.Notification;
import com.airflights.notification.domain.model.NotificationStatus;
import com.airflights.notification.domain.model.NotificationType;
import com.airflights.notification.domain.port.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class CreateNotificationUseCaseTest {

    @Test
    void create_whenDuplicate_skipsSave() {
        NotificationRepository repository = Mockito.mock(NotificationRepository.class);
        CreateNotificationUseCase useCase = new CreateNotificationUseCase(repository);
        when(repository.existsByEventId("evt-1")).thenReturn(true);

        useCase.create(new NotificationCommand("user@example.com", "evt-1", NotificationType.BOOKING_CREATED, "{}"));

        verify(repository, never()).save(any(Notification.class));
    }

    @Test
    void create_whenNew_savesNotification() {
        NotificationRepository repository = Mockito.mock(NotificationRepository.class);
        CreateNotificationUseCase useCase = new CreateNotificationUseCase(repository);
        when(repository.existsByEventId("evt-2")).thenReturn(false);
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.create(new NotificationCommand("user@example.com", "evt-2", NotificationType.FILE_UPLOADED, "{}"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals("user@example.com", saved.userId());
        assertEquals("evt-2", saved.eventId());
        assertEquals(NotificationType.FILE_UPLOADED, saved.type());
        assertEquals(NotificationStatus.NEW, saved.status());
        assertEquals(0, saved.retries());
        assertEquals("{}", saved.payload());
        assertNotNull(saved.createdAt());
    }
}
