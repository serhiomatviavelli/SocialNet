package ru.socialnet.team43.repository.mapper;

import org.mapstruct.Mapper;
import jooq.db.tables.records.NotificationRecord;
import org.mapstruct.Mapping;
import ru.socialnet.team43.dto.notifications.NotificationDBDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    List<NotificationRecord> mapToList(List<NotificationDBDto> notificationDBDtoList);

    @Mapping(target = "id", ignore = true)
    NotificationRecord notificationDBDtoToNotificationRecord(NotificationDBDto notificationDBDto);
}
