package com.airflights.file.infrastructure.persistence;

import com.airflights.file.domain.model.FileMetadata;
import com.airflights.file.domain.port.FileRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepository {

    private final FileJpaRepository fileJpaRepository;

    @Override
    public FileMetadata save(FileMetadata metadata) {
        FileEntity saved = fileJpaRepository.save(FileMapper.toEntity(metadata));
        return FileMapper.toDomain(saved);
    }

    @Override
    public Optional<FileMetadata> findById(String fileId) {
        return fileJpaRepository.findById(fileId).map(FileMapper::toDomain);
    }

    @Override
    public boolean existsById(String fileId) {
        return fileJpaRepository.existsById(fileId);
    }

    @Override
    public void deleteById(String fileId) {
        fileJpaRepository.deleteById(fileId);
    }
}
