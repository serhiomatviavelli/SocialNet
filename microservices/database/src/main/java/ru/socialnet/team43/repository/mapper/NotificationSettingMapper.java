package ru.socialnet.team43.repository.mapper;

import org.mapstruct.Mapper;
import jooq.db.tables.records.NotificationSettingRecord;
import ru.socialnet.team43.dto.notifications.NotificationSettingDto;

@Mapper(componentModel = "spring")
public interface NotificationSettingMapper {
    NotificationSettingDto notificationSettingToDtoMapper(NotificationSettingRecord record);
}
