package ru.socialnet.team43.service.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.TableField;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.dto.enums.NotificationType;
import ru.socialnet.team43.dto.notifications.NotificationUpdateDto;
import ru.socialnet.team43.repository.NotificationSettingRepository;
import jooq.db.tables.records.NotificationSettingRecord;
import jooq.db.Tables;
import ru.socialnet.team43.repository.UserAuthRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingDBServiceImpl implements NotificationSettingDBService {
    private final NotificationSettingRepository settingRepository;
    private final UserAuthRepository userAuthRepository;

    @Override
    public Optional<NotificationSettingRecord> getSettings(String email) {
        log.info("setting up notifications for the user: {}", email);
        Optional<UserAuthDto> userAuthDto = userAuthRepository.getUserByEmail(email);
        if (userAuthDto.isPresent()) {
            return settingRepository.getNotificationSettingById(userAuthDto.get().getId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<NotificationSettingRecord> updateSetting(
            NotificationUpdateDto updateDto,
            String email) {
        log.info("Update notification settings for user: {}", email);
        NotificationType type = NotificationType.valueOf(updateDto.getNotificationType());
        TableField<NotificationSettingRecord, Boolean> tableField = getTableField(type);
        Optional<NotificationSettingRecord> record = getSettings(email);
        if (record.isPresent()) {
            record.get().set(tableField, updateDto.isEnable());

            return settingRepository.updateNotificationSettingRecord(record.get());
        }
        return Optional.empty();
    }

    @Override
    public Optional<NotificationSettingRecord> createSetting(Long userId) {
        log.info("create a notification setting for the user: {}", userId);
        return settingRepository.createNotificationSettingByRecord(setNewNotificationSettingRecord(userId));
    }

    private TableField<NotificationSettingRecord, Boolean> getTableField(
            NotificationType notificationType) {
        return switch (notificationType) {
            case LIKE -> Tables.NOTIFICATION_SETTING.ENABLE_LIKE;
            case POST -> Tables.NOTIFICATION_SETTING.ENABLE_POST;
            case POST_COMMENT -> Tables.NOTIFICATION_SETTING.ENABLE_POST_COMMENT;
            case COMMENT_COMMENT -> Tables.NOTIFICATION_SETTING.ENABLE_COMMENT_COMMENT;
            case MESSAGE -> Tables.NOTIFICATION_SETTING.ENABLE_MESSAGE;
            case FRIEND_BIRTHDAY -> Tables.NOTIFICATION_SETTING.ENABLE_FRIEND_BIRTHDAY;
            case FRIEND_REQUEST -> Tables.NOTIFICATION_SETTING.ENABLE_FRIEND_REQUEST;
            case SEND_EMAIL_MESSAGE -> Tables.NOTIFICATION_SETTING.ENABLE_SEND_EMAIL_MESSAGE;
            case FRIEND_APPROVE, FRIEND_BLOCKED, FRIEND_UNBLOCKED,
                    FRIEND_SUBSCRIBE, USER_BIRTHDAY -> null;
        };
    }

    private NotificationSettingRecord setNewNotificationSettingRecord(Long userId) {
        return new NotificationSettingRecord(userId,
                true, true,
                true, true,
                true, true,
                true, true);
    }
}

