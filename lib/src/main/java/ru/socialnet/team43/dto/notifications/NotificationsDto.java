package ru.socialnet.team43.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationsDto {
    private OffsetDateTime timeStamp;
    private EventNotificationDto data;
}
