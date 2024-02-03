package ru.socialnet.team43.repository;

import jooq.db.Tables;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import jooq.db.tables.records.NotificationSettingRecord;
import ru.socialnet.team43.dto.enums.NotificationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class NotificationSettingRepository {
    private final DSLContext dsl;

    public Optional<NotificationSettingRecord> getNotificationSettingById(Long userId) {
        return dsl
                .selectFrom(Tables.NOTIFICATION_SETTING)
                .where(Tables.NOTIFICATION_SETTING.ID.eq(userId))
                .fetchOptional();
    }

    public int deleteNotificationSettingById(Long userId) {
        return dsl
                .deleteFrom(Tables.NOTIFICATION_SETTING)
                .where(Tables.NOTIFICATION_SETTING.ID.eq(userId))
                .execute();
    }

    public Optional<NotificationSettingRecord> updateNotificationSettingRecord(
            NotificationSettingRecord notificationSettingRecord) {
        return dsl
                .update(Tables.NOTIFICATION_SETTING)
                .set(notificationSettingRecord)
                .where(Tables.NOTIFICATION_SETTING.ID.eq(notificationSettingRecord.getId()))
                .returning()
                .fetchOptional();
    }

    public Optional<NotificationSettingRecord> createNotificationSettingByRecord(
            NotificationSettingRecord notificationSettingRecord) {
        return dsl
                .insertInto(Tables.NOTIFICATION_SETTING)
                .set(notificationSettingRecord)
                .returning()
                .fetchOptional();
    }

    public List<Integer> getListNotificationType(Long userId) {
        List<Integer> list = new ArrayList<>(0);
        Optional<NotificationSettingRecord> record = getNotificationSettingById(userId);

        if (record.isPresent()) {
            if (record.get().getEnableLike()) {
                list.add(NotificationType.LIKE.getId());
            }
            if (record.get().getEnablePost()) {
                list.add(NotificationType.POST.getId());
            }
            if (record.get().getEnablePostComment()) {
                list.add(NotificationType.POST_COMMENT.getId());
            }
            if (record.get().getEnableMessage()) {
                list.add(NotificationType.MESSAGE.getId());
            }
            if (record.get().getEnableCommentComment()) {
                list.add(NotificationType.COMMENT_COMMENT.getId());
            }
            if (record.get().getEnableFriendRequest()) {
                list.add(NotificationType.FRIEND_REQUEST.getId());
            }
            if (record.get().getEnableFriendBirthday()) {
                list.add(NotificationType.FRIEND_BIRTHDAY.getId());
            }
            if (record.get().getEnableSendEmailMessage()) {
                list.add(NotificationType.SEND_EMAIL_MESSAGE.getId());
            }
        }
        return list;
    }

    public String findEmailByPersonIdIfSendEmail(Long personId) {
        return dsl
                .select()
                .from(Tables.NOTIFICATION_SETTING)
                .join(Tables.PERSON)
                .on(Tables.PERSON.USER_ID.eq(Tables.NOTIFICATION_SETTING.ID))
                .join(Tables.USER_AUTH)
                .on(Tables.USER_AUTH.ID.eq(Tables.NOTIFICATION_SETTING.ID))
                .where(Tables.PERSON.ID.eq(personId)
                        .and(Tables.NOTIFICATION_SETTING.ENABLE_SEND_EMAIL_MESSAGE.eq(true)))
                .fetchOne(Tables.USER_AUTH.EMAIL);
    }
}
