package ru.socialnet.team43.service.notifications;

import org.springframework.data.domain.Pageable;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.NotificationsDto;

import java.util.List;

public interface NotificationDBService {

    List<NotificationsDto> findByPersonId(String email, Pageable pageable);

    CountDto getNotificationCount(String email);

    void setIsRead(String email);

    void addNewEvent(Long entityId, int typeId);

    void addNewFriendEvent(Long dstPersonId, Long srcPersonId, int typeId);
}
