package ru.socialnet.team43.dto.dialogs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.ReadStatus;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private ZonedDateTime time;
    private Long conversationPartner1; // author
    private Long conversationPartner2; // recipient
    private String messageText;
    private ReadStatus readStatus;
    private Long dialogId;
}
