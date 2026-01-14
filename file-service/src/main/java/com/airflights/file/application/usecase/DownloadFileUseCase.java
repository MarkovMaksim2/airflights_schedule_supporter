package com.airflights.file.application.usecase;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import com.airflights.file.domain.port.ObjectStorage;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final FileRepository fileRepository;
    private final ObjectStorage objectStorage;

    public FileDownloadResult download(String fileId) {
        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        InputStream stream = objectStorage.getObject(metadata.bucket(), metadata.objectKey());
        return new FileDownloadResult(metadata, stream);
    }

    public record FileDownloadResult(FileMetadata metadata, InputStream content) {}
}
