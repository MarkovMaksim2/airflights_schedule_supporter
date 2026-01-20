package com.airflights.file.application.service;

import com.airflights.file.application.exception.ResourceNotFoundException;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final FileRepository fileRepository;
    private final ObjectStorage objectStorage;

    public FileDownloadResult download(String fileId) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        InputStream stream = objectStorage.getObject(metadata.bucket(), metadata.objectKey());
        return new FileDownloadResult(metadata, stream);
    }

    public record FileDownloadResult(FileMetadata metadata, InputStream content) {}
}
