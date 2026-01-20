package com.airflights.file.application.service;

import com.airflights.file.application.exception.ResourceNotFoundException;
import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFileMetaUseCase {

    private final FileRepository fileRepository;

    public FileMetadata getById(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
    }
}
