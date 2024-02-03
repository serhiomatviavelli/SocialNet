package ru.socialnet.team43.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;

@FeignClient(name = "databaseClient", url = "${database.url}")
public interface DatabaseClient {

    @GetMapping("/dialogs")
    Page<DialogDto> getDialogs(@RequestParam String email, Pageable page);

    @GetMapping("/dialogs/unread")
    Integer getCountUnreadDialogs(@RequestParam String email);

    @GetMapping("/dialogs/recipientId")
    DialogDto getDialogByRecipientId(@RequestParam Long id, @RequestParam String email);

    @GetMapping("/dialogs/messages")
    Page<MessageShortDto> getMessagesByRecipientId(
            @RequestParam Long recipientId,
            @RequestParam String email, Pageable page);

    @PutMapping("/dialogs/{dialogId}")
    boolean putDialog(@PathVariable Long dialogId, @RequestParam String email);

    @PostMapping("/dialogs/messages/save")
    boolean saveMessage(@RequestBody MessageDto messageDto);

    @GetMapping("/auth/user")
    UserAuthDto getUserByEmail(@RequestParam String email);
}
