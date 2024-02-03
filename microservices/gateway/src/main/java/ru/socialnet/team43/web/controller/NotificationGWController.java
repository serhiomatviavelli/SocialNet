package ru.socialnet.team43.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;
import ru.socialnet.team43.util.ControllerUtil;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationGWController {
    private final ProfileClient profileClient;
    private final ControllerUtil controllerUtil;

    @GetMapping("/count")
    public ResponseEntity<CountDto> getNotificationCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("Count notifications for the user: {}", email);
        return controllerUtil.createNewResponseEntity(
                profileClient.getNotificationCount(email));
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getSettings(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("setting up notifications for the user: {}", email);
        return controllerUtil.createNewResponseEntity(
                profileClient.getSettings(email));
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateSetting(
            @RequestBody NotificationUpdateDto notificationUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        log.info("Update notification settings for email: {}", email);

        ResponseEntity<NotificationSettingDto> inputResponseEntity =
                profileClient.updateSetting(notificationUpdateDto, email);

        HttpStatusCode statusCode = inputResponseEntity.getStatusCode();

        if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))) {
            return ResponseEntity.badRequest().build();
        }

        return controllerUtil.createNewResponseEntity(inputResponseEntity);
    }

    @PostMapping("/settings{id}")
    public ResponseEntity<NotificationSettingDto> createSettings(@PathVariable Long id) {
        log.info("create a notification setting for the user: {}", id);
        return controllerUtil.createNewResponseEntity(profileClient.createSettings(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationsDto>> getAll(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        String email = userDetails.getUsername();
        log.info("receiving all notifications for the user: {}; pageable: {}; pageable: first {}; getOffset {}; getPageSize {};" +
                        " getSort {}; getPageNumber {}; hasPrevious {}; " +
                        " isPage {}; isUnpaged {}; next {}; " +
                        " previousOrFirst {};",
                email, pageable, pageable.first(), pageable.getOffset(), pageable.getPageSize(),
                pageable.getSort(), pageable.getPageNumber(), pageable.hasPrevious(),
                pageable.isPaged(), pageable.isUnpaged(), pageable.next(),
                pageable.previousOrFirst());
        return controllerUtil.createNewResponseEntity(
                profileClient.getAll(email, pageable));
    }

    @PutMapping("/readed")
    public ResponseEntity<String> setIsRead(
            @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        log.info("all notifications for user: {} are marked as read", email);
        return controllerUtil.createNewResponseEntity(profileClient.setIsRead(email));
    }
}
