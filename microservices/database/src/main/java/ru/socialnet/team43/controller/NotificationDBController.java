package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;
import ru.socialnet.team43.repository.mapper.NotificationSettingMapper;
import ru.socialnet.team43.service.notifications.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationDBController {
    private final NotificationSettingDBService settingService;
    private final NotificationDBService notificationDBService;
    private final NotificationSettingMapper mapper;

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getSettings(@RequestParam("email") String email) {
        //description: Получение настроек оповещений
        return settingService
                .getSettings(email)
                .map(settingRecord -> ResponseEntity
                        .ok(mapper.notificationSettingToDtoMapper(settingRecord))
                )
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateSetting(
            @RequestBody NotificationUpdateDto notificationUpdateDto,
            @RequestParam("email") String email) {
        //description: Коррекция настроек оповещений
        return settingService
                .updateSetting(notificationUpdateDto, email)
                .map(settingRecord -> ResponseEntity
                        .ok(mapper.notificationSettingToDtoMapper(settingRecord))
                )
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("/settings{id}")
    public ResponseEntity<NotificationSettingDto> createSettings(@PathVariable Long id) {
        //description: Создание настроек оповещений
        return settingService
                .createSetting(id)
                .map(settingRecord -> ResponseEntity
                        .ok(mapper.notificationSettingToDtoMapper(settingRecord))
                )
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping
    public ResponseEntity<List<NotificationsDto>> getNotifications(
            @RequestParam("email") String email,
            Pageable pageable) {
        //description: Получение событий
        try {
            List<NotificationsDto> pages = notificationDBService
                    .findByPersonId(email, pageable);
            return ResponseEntity.ok(pages);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<CountDto> getNotificationCount(
            @RequestParam("email") String email) {
        //description: Получить счетчик количества событий
        try {
            CountDto countDto = notificationDBService.getNotificationCount(email);
            return ResponseEntity.ok(countDto);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/readed")
    public ResponseEntity<String> setIsRead(
            @RequestParam("email") String email) {
        //description: Отметить все события, как прочитанные
        try {
            notificationDBService.setIsRead(email);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
