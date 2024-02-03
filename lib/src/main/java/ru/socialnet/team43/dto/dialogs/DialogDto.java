package ru.socialnet.team43.dto.dialogs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DialogDto {
    private Long id;
    private Integer unreadCount;
    private Long conversationPartner1; // собеседник
    private Long conversationPartner2; // пользователь
    private List<MessageDto> lastMessage;
}
