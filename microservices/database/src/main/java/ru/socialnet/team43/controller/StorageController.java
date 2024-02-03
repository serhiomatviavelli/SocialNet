package ru.socialnet.team43.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.socialnet.team43.dto.storage.StorageDto;
import ru.socialnet.team43.service.storage.StorageService;

@Slf4j
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StorageDto> getStorage(@RequestBody MultipartFile file) {
        log.info("/storage: trying to storage file {}", file.getOriginalFilename());
        return storageService.getStorage(file);
    }
}
