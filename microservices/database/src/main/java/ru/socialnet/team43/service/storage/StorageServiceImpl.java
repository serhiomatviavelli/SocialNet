package ru.socialnet.team43.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.config.CloudinaryCfg;
import ru.socialnet.team43.dto.storage.StorageDto;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    private final Cloudinary cloudinary;

    public StorageServiceImpl(CloudinaryCfg config) {
        cloudinary = new Cloudinary(config.getParameters());
    }

    @Override
    public ResponseEntity<StorageDto> getStorage(MultipartFile file) {
        StorageDto dto = new StorageDto();
        String url = null;

        try {
            url = cloudinary
                    .uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap())
                    .get("url")
                    .toString();
        } catch (Exception e) {
            log.error("Work with cloudinary cannot be completed: " + e.getMessage(), e);
            return ResponseEntity.status(422).build();
        }

        log.info("Image {} successfully uploaded", file.getOriginalFilename());
        dto.setFileName(url);
        return ResponseEntity.ok(dto);
    }
}
