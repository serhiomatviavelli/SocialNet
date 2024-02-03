package ru.socialnet.team43.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationUpdateDto {
    //description: Dto для установки настроек оповещений
    private boolean enable; //Разрешить оповещение для данного типа событий
    private String notificationType; //Тип события
}
