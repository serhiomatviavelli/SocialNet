package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;

import java.util.List;

@FeignClient(name = "notificationsClient", url = "${database.url}" + "/notifications")
public interface NotificationsClient {
    @GetMapping("/settings")
    NotificationSettingDto getSettings(@RequestParam("email") String email);

    @PutMapping("/settings")
    NotificationSettingDto updateSetting(
            @RequestBody NotificationUpdateDto notificationUpdateDto,
            @RequestParam("email") String email);

    @PostMapping("/settings{id}")
    NotificationSettingDto createSettings(@PathVariable Long id);

    @GetMapping
    List<NotificationsDto> getNotifications(
            @RequestParam("email") String email,
            Pageable pageable);

    @GetMapping("/count")
    CountDto getNotificationCount(@RequestParam("email") String email);

    @PutMapping("/readed")
    void setIsRead(@RequestParam("email") String email);
}
