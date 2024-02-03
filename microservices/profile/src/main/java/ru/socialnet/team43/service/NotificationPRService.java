package ru.socialnet.team43.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;

public interface NotificationPRService {
    NotificationSettingDto getSettings(String email);

    NotificationSettingDto updateSetting(NotificationUpdateDto notificationUpdateDto, String email);

    NotificationSettingDto createSetting(Long id);

    Page<NotificationsDto> getAll(String email, String sort, int page, int size, Pageable pageable);

    CountDto getNotificationCount(String email);

    void setIsRead(String email);
}
