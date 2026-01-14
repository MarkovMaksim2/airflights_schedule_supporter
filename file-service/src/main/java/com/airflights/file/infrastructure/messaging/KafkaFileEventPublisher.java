package com.airflights.file.infrastructure.messaging;

import com.airflights.file.domain.event.FileDeletedEvent;
import com.airflights.file.domain.event.FileUploadFailedEvent;
import com.airflights.file.domain.event.FileUploadedEvent;
import com.airflights.file.domain.port.FileEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaFileEventPublisher implements FileEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${file-events.topic:file.events}")
    private String fileEventsTopic;

    @Override
    public void publishFileUploaded(FileUploadedEvent event) {
        kafkaTemplate.send(fileEventsTopic, event.eventId(), event);
    }

    @Override
    public void publishFileUploadFailed(FileUploadFailedEvent event) {
        kafkaTemplate.send(fileEventsTopic, event.eventId(), event);
    }

    @Override
    public void publishFileDeleted(FileDeletedEvent event) {
        kafkaTemplate.send(fileEventsTopic, event.eventId(), event);
    }
}
