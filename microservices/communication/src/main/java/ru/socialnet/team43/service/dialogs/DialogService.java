package ru.socialnet.team43.service.dialogs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;
import ru.socialnet.team43.dto.dialogs.UnreadCountDto;

public interface DialogService {

    ResponseEntity<Page<DialogDto>> getDialogs(String email, Pageable page);

    ResponseEntity<Page<MessageShortDto>> getMessagesByRecipientId(Long recipientId, String email, Pageable page);

    ResponseEntity<UnreadCountDto> getCountUnreadDialogs(String email);

    ResponseEntity<DialogDto> getDialogByRecipientId(Long id, String email);

    boolean putDialog(Long dialogId, String email);

    boolean saveMessage(MessageDto messageDto);
}
