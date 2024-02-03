package ru.socialnet.team43.service.dialogs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.dialogs.MessageShortDto;
import ru.socialnet.team43.repository.DialogRepository;
import ru.socialnet.team43.repository.MessageRepository;
import ru.socialnet.team43.repository.PersonRepository;
import ru.socialnet.team43.repository.mapper.DialogMapper;
import ru.socialnet.team43.repository.mapper.MessageMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogServiceImpl implements DialogService {

    private final DialogRepository dialogRepo;
    private final MessageRepository messageRepo;
    private final PersonRepository personRepo;

    private final MessageMapper messageMapper;
    private final DialogMapper dialogMapper;

    @Override
    public Page<DialogDto> getDialogs(String email, Pageable page) {

        Long userId = personRepo.getPersonIdByEmail(email);
        if (userId == null) {
            return new PageImpl<>(Collections.emptyList());
        }

        List<DialogDto> resultsList =
                dialogMapper.mapToList(
                        dialogRepo.getDialogsByUser(userId), userId, messageMapper, messageRepo);
        int total = resultsList.size();

        try {
            int endIndex =
                    (int)
                            ((page.getOffset() + page.getPageSize()) > total
                                    ? total
                                    : (page.getOffset() + page.getPageSize()));
            if (page.getOffset() > total) throw new NumberFormatException();
            int offset = (int) page.getOffset();

            sortDialogs(resultsList, page.getSort());
            resultsList = resultsList.subList(offset, endIndex);

        } catch (Exception ex) {
            log.warn("getDialogs: ", ex);
            return new PageImpl<>(Collections.emptyList());
        }

        if (!resultsList.isEmpty()) {
            return new PageImpl<>(resultsList, page, total);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Override
    public Integer getCountUnreadDialogs(String email) {
        Long userId = personRepo.getPersonIdByEmail(email);
        return messageRepo.allUnreadCountByUser(userId);
    }

    @Override
    public DialogDto getDialogByRecipientId(Long recipientId, String email) {
        Long userId = personRepo.getPersonIdByEmail(email);
        DialogDto dialogDto =
                dialogMapper.dialogRecordToDialogDto(
                        dialogRepo.getDialogByRecipientId(recipientId, userId),
                        userId,
                        messageMapper,
                        messageRepo);

        // Если диалог не найден, создать его.
        if (dialogDto == null && !Objects.equals(userId, recipientId)) {
            dialogDto = createDialog(userId, recipientId);
        }
        if (dialogDto != null) {
            updateMessageStatuses(dialogDto.getId(), email);
        }

        return dialogDto;
    }

    @Override
    public Page<MessageShortDto> getMessagesByRecipientId(
            Long recipientId, String email, Pageable page) {
        
        DialogDto dialog = getDialogByRecipientId(recipientId, email);
        
        List<MessageShortDto> resultsList =
                messageMapper.mapToShortList(
                        messageRepo.getMessagesByDialogId(dialog.getId(), page));
        Integer total = messageRepo.totalCountMessagesByDialogId(dialog.getId());

        if (!resultsList.isEmpty()) {
            return new PageImpl<>(resultsList, page, total);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Override
    public boolean saveMessage(MessageDto messageDto) {
        return messageRepo.insertMessage(messageMapper.messageDtoToMessageRecord(messageDto));
    }

    @Override
    public boolean updateMessageStatuses(Long dialogId, String email) {
        Long userId = personRepo.getPersonIdByEmail(email);
        return messageRepo.updateMessages(dialogId, userId);
    }

    private void sortDialogs(List<DialogDto> dialogs, Sort sort) {
        Comparator<DialogDto> comparator1 = Comparator.comparing(DialogDto::getUnreadCount);
        Comparator<DialogDto> comparator2 =
                Comparator.comparing(
                        dialog ->
                                (dialog.getLastMessage().isEmpty()
                                                || dialog.getLastMessage().get(0) == null)
                                        ? ZonedDateTime.of(
                                                2000, 1, 1, 0, 0, 0,
                                        0, ZoneId.systemDefault())
                                        : dialog.getLastMessage().get(0).getTime());
        Comparator<DialogDto> comparator3 =
                Comparator.comparingInt(dialog -> Integer.compare(dialog.getUnreadCount(), 0));

        if (sort.isSorted()) {
            Comparator<DialogDto> sortingComparator = null;
            for (Sort.Order order : sort) {
                if (order.getProperty().equals("unreadCount")) {
                    sortingComparator =
                            order.isDescending()
                                    ? comparator1.thenComparing(comparator2).reversed()
                                    : comparator1.thenComparing(comparator2);
                }
            }

            dialogs.sort(sortingComparator);
        } else {
            // Default sorting
            dialogs.sort(comparator3.thenComparing(comparator2).reversed());
        }
    }

    private DialogDto createDialog(Long userId, Long recipientId) {
        DialogDto dialogDto = DialogDto.builder()
                .conversationPartner1(userId)
                .conversationPartner2(recipientId)
                .build();

        return dialogMapper.dialogRecordToDialogDto(
                dialogRepo.insertDialog(dialogMapper.dialogDtoToDialogRecord(dialogDto)),
                userId,
                messageMapper,
                messageRepo);
    }
}
