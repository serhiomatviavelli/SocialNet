package ru.socialnet.team43.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.NotificationType;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNotificationDto {
    private Long authorId;
    private Long receiverId;
    private NotificationType notificationType;
    private String content;
    private OffsetDateTime sentTime;
}
