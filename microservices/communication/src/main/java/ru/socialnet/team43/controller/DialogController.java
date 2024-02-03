package ru.socialnet.team43.controller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;
import ru.socialnet.team43.dto.dialogs.UnreadCountDto;
import ru.socialnet.team43.service.dialogs.DialogService;

@Slf4j
@RestController
@RequestMapping("/api/v1/dialogs")
@AllArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @GetMapping
    public ResponseEntity<Page<DialogDto>> getDialogs(@RequestParam String email, Pageable page) {
        log.info("{}: /dialogs", email);
        return dialogService.getDialogs(email, page);
    }

    @GetMapping("/messages")
    ResponseEntity<Page<MessageShortDto>> getMessagesByRecipientId(
            @RequestParam Long recipientId, @RequestParam String email, Pageable page) {

        log.info("{}: /dialogs/messages/{}", email, recipientId);
        return dialogService.getMessagesByRecipientId(recipientId, email, page);
    }

    @GetMapping("/unread")
    public ResponseEntity<UnreadCountDto> getCountUnreadDialogs(@RequestParam String email) {
        log.info("{}: /dialogs/unread", email);
        return dialogService.getCountUnreadDialogs(email);
    }

    @GetMapping("/recipientId")
    public ResponseEntity<DialogDto> getDialogByRecipientId(
            @RequestParam Long id, @RequestParam String email) {
        log.info("{}: /dialog/recipientId/{}", email, id);
        return dialogService.getDialogByRecipientId(id, email);
    }

    @PutMapping("/{dialogId}")
    boolean putDialog(@PathVariable Long dialogId, @RequestParam String email) {

        log.info("{}: /dialogs/{}", email, dialogId);
        return dialogService.putDialog(dialogId, email);
    }

    @PostMapping("/messages/save")
    public boolean saveMessage(@RequestBody MessageDto messageDto) {
        log.info("/dialogs/messages/save");
        return dialogService.saveMessage(messageDto);
    }
}