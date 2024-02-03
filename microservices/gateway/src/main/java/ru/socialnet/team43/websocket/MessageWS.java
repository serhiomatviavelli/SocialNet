package ru.socialnet.team43.websocket;

import lombok.Data;
import ru.socialnet.team43.dto.dialogs.MessageDto;

@Data
public class MessageWS {
    // DTO для пересылки по веб-сокетам.
    private Long recipientId;
    private String type;
    private MessageDto data;
}
