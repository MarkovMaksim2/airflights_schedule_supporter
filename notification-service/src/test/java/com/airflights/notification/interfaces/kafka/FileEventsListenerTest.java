package com.airflights.notification.interfaces.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.airflights.notification.application.usecase.CreateNotificationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FileEventsListenerTest {

    @Test
    void onFileUploaded_createsNotification() throws Exception {
        CreateNotificationUseCase useCase = Mockito.mock(CreateNotificationUseCase.class);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        FileEventsListener listener = new FileEventsListener(useCase, objectMapper);

        String payload = """
                {
                  "eventId": "evt-1",
                  "eventType": "FileUploaded",
                  "fileId": "file-1",
                  "ownerId": "user@example.com",
                  "bucket": "airflights-files",
                  "objectKey": "user-uploads/user@example.com/file-1/passport.pdf",
                  "contentType": "application/pdf",
                  "sizeBytes": 123
                }
                """;

        listener.onFileUploaded(payload);

        verify(useCase).create(any());
    }

    @Test
    void onFileUploadFailed_createsNotification() throws Exception {
        CreateNotificationUseCase useCase = Mockito.mock(CreateNotificationUseCase.class);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        FileEventsListener listener = new FileEventsListener(useCase, objectMapper);

        String payload = """
                {
                  "eventId": "evt-2",
                  "eventType": "FileUploadFailed",
                  "fileId": "file-2",
                  "ownerId": "user@example.com",
                  "bucket": "airflights-files",
                  "objectKey": "user-uploads/user@example.com/file-2/passport.pdf",
                  "contentType": "application/pdf",
                  "sizeBytes": 123,
                  "reason": "S3 down"
                }
                """;

        listener.onFileUploaded(payload);

        verify(useCase).create(any());
    }

    @Test
    void onFileDeleted_createsNotification() throws Exception {
        CreateNotificationUseCase useCase = Mockito.mock(CreateNotificationUseCase.class);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        FileEventsListener listener = new FileEventsListener(useCase, objectMapper);

        String payload = """
                {
                  "eventId": "evt-3",
                  "eventType": "FileDeleted",
                  "fileId": "file-3",
                  "ownerId": "user@example.com",
                  "bucket": "airflights-files",
                  "objectKey": "user-uploads/user@example.com/file-3/passport.pdf"
                }
                """;

        listener.onFileUploaded(payload);

        verify(useCase).create(any());
    }
}
