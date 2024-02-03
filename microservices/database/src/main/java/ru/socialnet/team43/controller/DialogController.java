package ru.socialnet.team43.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;
import ru.socialnet.team43.service.dialogs.DialogService;

@Slf4j
@RestController
@RequestMapping("/dialogs")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @GetMapping
    public Page<DialogDto> getDialogs(@RequestParam String email, Pageable page) {
        log.info("/dialogs {}", email);
        return dialogService.getDialogs(email, page);
    }

    @GetMapping("/unread")
    public Integer getCountUnreadDialogs(@RequestParam String email) {
        log.info("/dialogs/unread {}", email);
        return dialogService.getCountUnreadDialogs(email);
    }

    @GetMapping("/recipientId")
    public DialogDto getDialogByRecipientId(@RequestParam Long id, @RequestParam String email) {
        log.info("/dialogs/recipientId/{} {}", id, email);
        return dialogService.getDialogByRecipientId(id, email);
    }

    @GetMapping("/messages")
    public Page<MessageShortDto> getMessagesByRecipientId(
            @RequestParam Long recipientId,
            @RequestParam String email,
            Pageable page) {

        log.info("/dialogs/messages/{} {}", recipientId, email);
        return dialogService.getMessagesByRecipientId(recipientId, email, page);
    }

    @PutMapping("/{dialogId}")
    public boolean putDialog(@PathVariable Long dialogId, @RequestParam String email) {
        log.info("{}: /dialogs/{}", email, dialogId);
        return dialogService.updateMessageStatuses(dialogId, email);
    }

    @PostMapping("/messages/save")
    public boolean saveMessage(@RequestBody MessageDto messageDto) {
        log.info("/dialogs/messages/save");
        return dialogService.saveMessage(messageDto);
    }
}
