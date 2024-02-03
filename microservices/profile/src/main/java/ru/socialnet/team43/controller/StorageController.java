package ru.socialnet.team43.controller;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.dto.storage.StorageDto;
import ru.socialnet.team43.service.storage.StorageService;
import ru.socialnet.team43.util.ControllerUtil;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    public final StorageService storageService;
    private final ControllerUtil controllerUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StorageDto> getStorage(@RequestBody MultipartFile file) {
        log.info("/api/v1/storage: file {}", file.getOriginalFilename());
        return controllerUtil.createNewResponseEntity(storageService.getStorage(file));
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handleException(FeignException ex) {
        return ResponseEntity.status(ex.status()).build();
    }
}
