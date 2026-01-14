package com.airflights.file.application.usecase;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GetFileMetaUseCase {

    private final FileRepository fileRepository;

    public FileMetadata getById(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }
}
