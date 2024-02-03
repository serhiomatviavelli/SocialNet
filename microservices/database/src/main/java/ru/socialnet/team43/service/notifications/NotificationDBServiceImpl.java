package ru.socialnet.team43.service.notifications;

import jooq.db.tables.records.NotificationRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.DataCount;
import ru.socialnet.team43.dto.enums.NotificationType;
import ru.socialnet.team43.dto.notifications.*;
import ru.socialnet.team43.repository.*;
import jooq.db.tables.records.PersonRecord;
import ru.socialnet.team43.repository.mapper.NotificationMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDBServiceImpl implements NotificationDBService {
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final PersonRepository personRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationsDto> findByPersonId(String email, Pageable pageable) {
        Long userId = personRepository.findUserIdByEmail(email);
        Long personId = notificationRepository.getPersonRecordByUserId(userId)
                .map(PersonRecord::getId)
                .orElse(0L);
        List<Integer> list = settingRepository.getListNotificationType(userId);
        log.info("notifications to user: {}; userId: {}; personId: {}; list: {};",
                email, userId, personId, list
        );

        List<NotificationsDto> contents = notificationRepository.findByPersonId(personId, list, pageable);
        log.info("receiving all notifications for the user: {}, userId: {}; personIds: {}; contents: {}",
                email, userId, personId, contents.size()
        );
        return contents;
    }

    @Override
    public CountDto getNotificationCount(String email) {
        Long userId = personRepository.findUserIdByEmail(email);
        Long personId = notificationRepository.getPersonRecordByUserId(userId)
                .map(PersonRecord::getId)
                .orElse(0L);
        List<Integer> list = settingRepository.getListNotificationType(userId);

        int count = notificationRepository.getCount(personId, list);

        CountDto countDto = CountDto.builder()
                .data(new DataCount(count))
                .timeStamp(LocalDateTime.now())
                .build();
        log.info("Count notifications for the user: {} - {}", email, countDto.getData().getCount());
        return countDto;
    }

    @Override
    public void setIsRead(String email){
        Long userId = personRepository.findUserIdByEmail(email);
        Long personId = notificationRepository.getPersonRecordByUserId(userId)
                .map(PersonRecord::getId)
                .orElse(0L);
        List<Integer> list = settingRepository.getListNotificationType(userId);
        int result = notificationRepository.setIsRead(personId, list);
        log.info("all notifications for user: {} - personId {} are marked as read {}", email, personId, result);
    }

    @Override
    public void addNewEvent(Long entityId, int typeId) {
        List<NotificationDBDto> notificationDBDtoList = new ArrayList<>(0);

        if (NotificationType.POST.getId() == typeId) {
            Result<Record2<Long, OffsetDateTime>> result = notificationRepository
                    .findAuthorIdAndPublishDateByPostEntityId(entityId);
            Long personId = result.get(0).component1();
            OffsetDateTime time = result.get(0).component2();

            List<Long> listPersonIds = notificationRepository.findAllPersonIdsUnequalPersonId(personId);
            listPersonIds.forEach((it) ->
                    notificationDBDtoList
                            .add(setNewNotificationDBDto(entityId, typeId, it, time)));
        }

        if (NotificationType.POST_COMMENT.getId() == typeId) {
            Result<Record2<Long, OffsetDateTime>> result = notificationRepository
                    .findPersonIdAndTimeByCommentEntityId(entityId);
            Long personId = result.get(0).component1();
            OffsetDateTime time = result.get(0).component2();

            notificationDBDtoList
                    .add(setNewNotificationDBDto(entityId, typeId, personId, time));
        }

        if (NotificationType.COMMENT_COMMENT.getId() == typeId) {
            Result<Record2<Long, OffsetDateTime>> result = notificationRepository
                    .findAuthorIdAndTimeByCommentEntityId(entityId);
            Long personId = result.get(0).component1();
            OffsetDateTime time = result.get(0).component2();

            notificationDBDtoList
                    .add(setNewNotificationDBDto(entityId, typeId, personId, time));
        }

        List<NotificationRecord> notificationRecords = notificationMapper.mapToList(notificationDBDtoList);
        int[] newEvent = notificationRepository.addNewEvents(notificationRecords);
        log.info("Create notifications (Post/Comment) for entityId: {} - typeId {};" +
                        " count - {}; newEvent {};",
                entityId, typeId, notificationRecords.size(), newEvent);
    }

    @Override
    public void addNewFriendEvent(Long dstPersonId, Long srcPersonId, int typeId) {
        NotificationDBDto dto = setNewNotificationDBDto(srcPersonId, typeId, dstPersonId, OffsetDateTime.now());
        NotificationRecord record = notificationMapper.notificationDBDtoToNotificationRecord(dto);
        int newEvent = notificationRepository.addNewFriendEvent(record);
        log.info("Create notifications (Friend) for dstPersonId: {} - typeId {};" +
                        " srcPersonId - {}; newEvent {};",
                dstPersonId, typeId, srcPersonId, newEvent);
    }

    private NotificationDBDto setNewNotificationDBDto(
            Long entityId, int typeId,
            Long personId, OffsetDateTime time) {

        NotificationDBDto dto = NotificationDBDto.builder()
                .typeId(typeId)
                .sentTime(time)
                .personId(personId)
                .entityId(entityId)
                .contact(settingRepository.findEmailByPersonIdIfSendEmail(personId))
                .isRead(false)
                .build();

        log.info("Create notifications for entityId: {} - typeId {}; dto {}; ",
                entityId, typeId, dto);
        return dto;
    }
}
