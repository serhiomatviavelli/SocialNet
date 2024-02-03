package ru.socialnet.team43.service.notifications;

import ru.socialnet.team43.dto.notifications.NotificationUpdateDto;
import jooq.db.tables.records.NotificationSettingRecord;

import java.util.Optional;

public interface NotificationSettingDBService {

    Optional<NotificationSettingRecord> getSettings(String email);

    Optional<NotificationSettingRecord> updateSetting(NotificationUpdateDto notificationUpdateDto, String email);

    Optional<NotificationSettingRecord> createSetting(Long userId);

}
