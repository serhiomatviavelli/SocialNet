package ru.socialnet.team43.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.storage.StorageDto;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final DatabaseClient databaseClient;

    @Override
    public ResponseEntity<StorageDto> getStorage(MultipartFile file) {
        if (file.isEmpty()
                || !Objects.requireNonNull(file.getContentType()).matches("^image/(png|jpg|jpeg)")) {
            log.warn(
                    "File {} has the non-correct type {}. Correct type is image/png or image/jpg",
                    file.getOriginalFilename(),
                    file.getContentType());
            return ResponseEntity.status(415).build();
        }
        return databaseClient.getStorage(file);
    }
}
