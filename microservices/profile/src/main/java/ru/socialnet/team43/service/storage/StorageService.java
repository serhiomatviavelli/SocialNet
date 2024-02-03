package ru.socialnet.team43.service.storage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.dto.storage.StorageDto;

public interface StorageService {
    ResponseEntity<StorageDto> getStorage(MultipartFile file);
}
