package ru.socialnet.team43.service.dialogs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;

public interface DialogService {
    Page<DialogDto> getDialogs(String email, Pageable page);

    Integer getCountUnreadDialogs(String email);

    DialogDto getDialogByRecipientId(Long recipientId, String email);

    Page<MessageShortDto> getMessagesByRecipientId(Long recipientId, String email, Pageable page);

    boolean saveMessage(MessageDto messageDto);

    boolean updateMessageStatuses(Long dialogId, String email);
}
