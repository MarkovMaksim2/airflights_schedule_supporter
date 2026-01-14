package com.airflights.file.domain.port;

import com.airflights.file.domain.event.FileUploadedEvent;
import com.airflights.file.domain.event.FileUploadFailedEvent;
import com.airflights.file.domain.event.FileDeletedEvent;

public interface FileEventPublisher {
    void publishFileUploaded(FileUploadedEvent event);
    void publishFileUploadFailed(FileUploadFailedEvent event);
    void publishFileDeleted(FileDeletedEvent event);
}
