package com.airflights.file.domain.port;

import com.airflights.file.domain.model.FileMetadata;
import java.util.Optional;

public interface FileRepository {
    FileMetadata save(FileMetadata metadata);
    Optional<FileMetadata> findById(String fileId);
    boolean existsById(String fileId);
    void deleteById(String fileId);
}
