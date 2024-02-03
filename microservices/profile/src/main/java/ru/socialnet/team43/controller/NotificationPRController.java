package ru.socialnet.team43.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;
import ru.socialnet.team43.service.NotificationPRService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationPRController {

    private final NotificationPRService service;

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getSettings(@RequestParam("email") String email) {
        //description: Получение настроек оповещений
        return ResponseEntity.ok(service.getSettings(email));
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateSetting(
            @RequestBody NotificationUpdateDto notificationUpdateDto,
            @RequestParam("email") String email) {
        //description: Коррекция настроек оповещений
        return ResponseEntity.ok(service.updateSetting(notificationUpdateDto, email));
    }

    @PostMapping("/settings{id}")
    public ResponseEntity<NotificationSettingDto> createSettings(@PathVariable Long id) {
        //description: Создание настроек оповещений
        return ResponseEntity.ok(service.createSetting(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationsDto>> getAll(
            @RequestParam("email") String email,
            @RequestParam(defaultValue = "sent_time") String sort,
            @RequestParam int page,
            @RequestParam int size,
            Pageable pageable
    ) {
        //description: Получение всех оповещений
        return ResponseEntity.ok(service.getAll(email, sort, page, size, pageable));//
    }

    @GetMapping("/count")
    public ResponseEntity<CountDto> getNotificationCount(
            @RequestParam("email") String email) {
        //description: Получить счетчик количества событий
        return ResponseEntity.ok(service.getNotificationCount(email));
    }

    @PutMapping("/readed")
    public ResponseEntity<String> setIsRead(
            @RequestParam("email") String email) {
        //description: Отметить все события, как прочитанные
        service.setIsRead(email);
        return ResponseEntity.ok().build();
    }
}
