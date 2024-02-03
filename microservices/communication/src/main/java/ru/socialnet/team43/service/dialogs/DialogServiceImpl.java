package ru.socialnet.team43.service.dialogs;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.dialogs.*;

@Service
@AllArgsConstructor
public class DialogServiceImpl implements DialogService {

    private final DatabaseClient databaseClient;

    @Override
    public ResponseEntity<Page<DialogDto>> getDialogs(String email, Pageable page) {
        return ResponseEntity.ok(databaseClient.getDialogs(email, page));
    }

    @Override
    public ResponseEntity<UnreadCountDto> getCountUnreadDialogs(String email) {
        UnreadCountDto dto = new UnreadCountDto(databaseClient.getCountUnreadDialogs(email));
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<DialogDto> getDialogByRecipientId(Long id, String email) {
        return ResponseEntity.ok(databaseClient.getDialogByRecipientId(id, email));
    }

    @Override
    public boolean putDialog(Long dialogId, String email) {
        return databaseClient.putDialog(dialogId, email);
    }

    @Override
    public ResponseEntity<Page<MessageShortDto>> getMessagesByRecipientId(
            Long recipientId, String email, Pageable page) {
        return ResponseEntity.ok(databaseClient.getMessagesByRecipientId(recipientId, email, page));
    }

    @Override
    public boolean saveMessage(MessageDto messageDto) {
        return databaseClient.saveMessage(messageDto);
    }
}
