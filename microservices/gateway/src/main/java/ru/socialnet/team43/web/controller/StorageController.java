package ru.socialnet.team43.web.controller;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.storage.StorageDto;
import ru.socialnet.team43.util.ControllerUtil;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    @Value("${max-image-size}")
    private int MAX_IMAGE_SIZE;

    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StorageDto> getStorage(@RequestBody MultipartFile file) {
        log.info("/api/v1/storage");
        if (file == null || file.getSize() >= MAX_IMAGE_SIZE) {
            log.warn(file == null
                    ? "MultipartFile is null"
                    : "MultipartFile is large. Maximum is " + MAX_IMAGE_SIZE + "B");
            return ResponseEntity.status(413).build();
        }
        return controllerUtil.createNewResponseEntity(profileClient.getStorage(file));
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handleException(FeignException ex) {
        return ResponseEntity.status(ex.status()).build();
    }
}
