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
public class NotificationDBDto {
    private Long id;
    private int typeId;
    private OffsetDateTime sentTime;
    private Long personId;
    private Long entityId;
    private String contact;
    private Boolean isRead;
}
