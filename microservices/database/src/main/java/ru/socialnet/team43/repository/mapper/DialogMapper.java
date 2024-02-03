package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.DialogRecord;
import jooq.db.tables.records.MessageRecord;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import ru.socialnet.team43.dto.dialogs.DialogDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.repository.MessageRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface DialogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "conversationPartner1", target = "conversationPartner_1")
    @Mapping(source = "conversationPartner2", target = "conversationPartner_2")
    DialogRecord dialogDtoToDialogRecord(DialogDto dialogDto);

    default List<DialogDto> mapToList(
            List<DialogRecord> dialogRecords,
            Long userId,
            MessageMapper messageMapper,
            MessageRepository messageRepo) {

        if (dialogRecords == null) {
            return null;
        }

        List<DialogDto> list = new ArrayList<>(dialogRecords.size());
        for (DialogRecord dialogRecord : dialogRecords) {
            list.add(dialogRecordToDialogDto(dialogRecord, userId, messageMapper, messageRepo));
        }

        return list;
    }

    default DialogDto dialogRecordToDialogDto(
            DialogRecord dialogRecord,
            Long userId,
            MessageMapper messageMapper,
            MessageRepository messageRepo) {

        if (dialogRecord == null) {
            return null;
        }

        List<MessageRecord> lastMessageRecords =
                messageRepo.getLastMessageByDialogId(dialogRecord.getId(), userId);
        List<MessageDto> lastMessage = messageMapper.mapToList(lastMessageRecords);

        if (lastMessage.size() > 1) {
            lastMessage.sort(Comparator.comparing(MessageDto::getTime).reversed());
        }
        Integer unreadCount = messageRepo.unreadCountForDialog(dialogRecord.getId(), userId);

        DialogDto dialogDto = new DialogDto();
        dialogDto.setId(dialogRecord.getId());
        if (Objects.equals(dialogRecord.getConversationPartner_1(), userId)) {
            dialogDto.setConversationPartner1(dialogRecord.getConversationPartner_2());
            dialogDto.setConversationPartner2(dialogRecord.getConversationPartner_1());
        } else {
            dialogDto.setConversationPartner1(dialogRecord.getConversationPartner_1());
            dialogDto.setConversationPartner2(dialogRecord.getConversationPartner_2());
        }
        dialogDto.setLastMessage(lastMessage);
        dialogDto.setUnreadCount(unreadCount);

        return dialogDto;
    }
}
