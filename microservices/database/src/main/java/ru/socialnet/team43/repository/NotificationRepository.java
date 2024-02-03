package ru.socialnet.team43.repository;

import jooq.db.Tables;
import lombok.AllArgsConstructor;
import org.jooq.*;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import jooq.db.tables.records.PersonRecord;
import jooq.db.tables.records.NotificationRecord;
import jooq.db.tables.records.CommentRecord;
import ru.socialnet.team43.dto.enums.NotificationType;
import ru.socialnet.team43.dto.notifications.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class NotificationRepository {
    private final DSLContext dsl;

    public int getCount(Long personId, List<Integer> list) {
        return dsl
                .select()
                .from(Tables.NOTIFICATION)
                .where(Tables.NOTIFICATION.PERSON_ID.eq(personId)
                        .and(Tables.NOTIFICATION.TYPE_ID.in(list))
                        .and(Tables.NOTIFICATION.IS_READ.eq(false)))
                .execute();
    }

    public Optional<PersonRecord> getPersonRecordByUserId(Long userId) {
        return dsl
                .selectFrom(Tables.PERSON)
                .where(Tables.PERSON.USER_ID.eq(userId))
                .fetchOptional();
    }

    public List<NotificationsDto> findByPersonId(Long personId, List<Integer> list, Pageable pageable) {
        List<NotificationDBDto> queryResults = dsl
                .selectFrom(Tables.NOTIFICATION)
                .where(Tables.NOTIFICATION.PERSON_ID.eq(personId)
                        .and(Tables.NOTIFICATION.TYPE_ID.in(list))
                        .and(Tables.NOTIFICATION.IS_READ.eq(false)))
                .orderBy(getSortFields(pageable.getSort()))
                .fetchInto(NotificationDBDto.class);

        return convertQueryResultsToModelObjects(personId, queryResults);
    }

    public int setIsRead(Long personId, List<Integer> list) {
        return dsl
                .update(Tables.NOTIFICATION)
                .set(Tables.NOTIFICATION.IS_READ, true)
                .where(Tables.NOTIFICATION.PERSON_ID.eq(personId)
                        .and(Tables.NOTIFICATION.TYPE_ID.in(list))
                        .and(Tables.NOTIFICATION.IS_READ.eq(false)))
                .returning()
                .execute();
    }

    public List<Long> findAllPersonIdsUnequalPersonId(Long personId) {
        return dsl
                .selectFrom(Tables.PERSON)
                .where(Tables.PERSON.ID.ne(personId)
                        .and(Tables.PERSON.IS_BLOCKED.eq(false))
                        .and(Tables.PERSON.IS_DELETED.eq(false)))
                .stream()
                .map(PersonRecord::getId)
                .collect(Collectors.toList());
    }

    public Result<Record2<Long, OffsetDateTime>> findAuthorIdAndPublishDateByPostEntityId(Long entityId) {
        return dsl
                .select(Tables.POST.AUTHOR_ID, Tables.POST.PUBLISH_DATE)
                .from(Tables.POST)
                .where(Tables.POST.ID.eq(entityId))
                .fetch();
    }

    public Result<Record2<Long, OffsetDateTime>> findPersonIdAndTimeByCommentEntityId(Long entityId) {
        return dsl
                .select(Tables.POST.AUTHOR_ID, Tables.COMMENT.TIME)
                .from(Tables.COMMENT)
                .join(Tables.POST)
                .on(Tables.POST.ID.eq(Tables.COMMENT.POST_ID))
                .where(Tables.COMMENT.ID.eq(entityId))
                .fetch();
    }

    public Result<Record2<Long, OffsetDateTime>> findAuthorIdAndTimeByCommentEntityId(Long entityId) {
        return dsl
                .select(Tables.COMMENT.AUTHOR_ID, Tables.COMMENT.TIME)
                .from(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(
                        dsl
                                .select()
                                .from(Tables.COMMENT)
                                .where(Tables.COMMENT.ID.eq(entityId))
                                .fetchOne(Tables.COMMENT.PARENT_ID)))
                .fetch();
    }

    public int[] addNewEvents(List<NotificationRecord> records) {
        return dsl.batchInsert(records)
                .execute();
    }

    public int addNewFriendEvent(NotificationRecord records) {
        return dsl
                .insertInto(Tables.NOTIFICATION)
                .set(records)
                .execute();
    }

    private Collection<SortField<?>> getSortFields(Sort sortSpecification) {
        Collection<SortField<?>> querySortFields = new ArrayList<>();

        if (sortSpecification == null) {
            return querySortFields;
        }

        Iterator<Sort.Order> specifiedFields = sortSpecification.iterator();

        while (specifiedFields.hasNext()) {
            Sort.Order specifiedField = specifiedFields.next();

            String sortFieldName = specifiedField.getProperty();
            Sort.Direction sortDirection = specifiedField.getDirection();

            TableField tableField = getTableField(sortFieldName);
            SortField<?> querySortField = convertTableFieldToSortField(tableField, sortDirection);
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

    private TableField getTableField(String sortFieldName) {
        TableField sortField = null;
        try {
            Field tableField = Tables.NOTIFICATION.getClass().getField(sortFieldName);
            sortField = (TableField) tableField.get(Tables.NOTIFICATION);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            String errorMessage = String.format("Could not find table field: {}", sortFieldName);
            throw new InvalidDataAccessApiUsageException(errorMessage, ex);
        }

        return sortField;
    }

    private SortField<?> convertTableFieldToSortField(TableField tableField, Sort.Direction sortDirection) {
        if (sortDirection == Sort.Direction.ASC) {
            return tableField.asc();
        } else {
            return tableField.desc();
        }
    }

    private List<NotificationsDto> convertQueryResultsToModelObjects(
            Long personId,
            List<NotificationDBDto> queryResults) {
        List<NotificationsDto> notificationsEntries = new ArrayList<>();

        for (NotificationDBDto queryResult : queryResults) {
            NotificationsDto notificationsEntry = convertQueryResultToModelObject(personId, queryResult);
            notificationsEntries.add(notificationsEntry);
        }

        return notificationsEntries;

    }

    private NotificationsDto convertQueryResultToModelObject(Long personId, NotificationDBDto dto) {
        Long notificationId = dto.getId();
        int typeId = dto.getTypeId();
        OffsetDateTime sentTime = dto.getSentTime();
        Long entityId = dto.getEntityId();

        EventNotificationDto event = setEvent(typeId, entityId, personId, notificationId);

        return NotificationsDto.builder()
                .timeStamp(sentTime)
                .data(event)
                .build();
    }

    private EventNotificationDto setEvent(int typeId, Long entityId, Long personId, Long id) {
        return switch (typeId) {
            case 1 -> setEventPostLike(entityId, personId);
            case 2 -> setEventPost(entityId, personId);
            case 3 -> setEventPostComment(entityId, personId);
            case 4 -> setEventCommentComment(entityId, personId);
            case 5 -> setEventMessage(entityId, personId);
            case 6 -> setEventFriendship(id);
            case 7 -> setEventFriendBirthday(id);
            default -> null;
        };
    }

    private EventNotificationDto setEventMessage(Long entityId, Long personId) {
        return dsl
                .selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.ID.eq(entityId))
                .fetchOptional()
                .map(messageRecord -> EventNotificationDto.builder()
                        .authorId(messageRecord.getAuthorId())
                        .receiverId(personId)
                        .notificationType(NotificationType.MESSAGE)
                        .content(messageRecord.getMessageText())
                        .sentTime(messageRecord.getTime())
                        .build()).orElse(null);
    }

    private EventNotificationDto setEventPostLike(Long entityId, Long personId) {
        return null;
    }

    private EventNotificationDto setEventPostComment(Long entityId, Long personId) {
        return dsl
                .selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(entityId))
                .fetchOptional()
                .map(commentRecord -> EventNotificationDto.builder()
                        .authorId(commentRecord.getAuthorId())
                        .receiverId(personId)
                        .notificationType(NotificationType.POST_COMMENT)
                        .content(commentRecord.getCommentText())
                        .sentTime(commentRecord.getTime())
                        .build()).orElse(null);
    }

    private EventNotificationDto setEventCommentComment(Long entityId, Long personId) {
        return dsl
                .selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(entityId))
                .fetchOptional()
                .map(commentRecord -> EventNotificationDto.builder()
                        .authorId(commentRecord.getAuthorId())
                        .receiverId(personId)
                        .notificationType(NotificationType.COMMENT_COMMENT)
                        .content(
                                dsl.selectFrom(Tables.COMMENT)
                                        .where(Tables.COMMENT.ID.eq(commentRecord.getParentId()))
                                        .fetchOptional()
                                        .map(CommentRecord::getCommentText).orElse(null))
                        .sentTime(commentRecord.getTime())
                        .build()).orElse(null);
    }

    private EventNotificationDto setEventPost(Long entityId, Long personId) {
        return dsl
                .selectFrom(Tables.POST)
                .where(Tables.POST.ID.eq(entityId))
                .fetchOptional()
                .map(postRecord -> EventNotificationDto.builder()
                        .authorId(postRecord.getAuthorId())
                        .receiverId(personId)
                        .notificationType(NotificationType.POST)
                        .content(postRecord.getPostText().replaceAll("<[^>]*>", ""))
                        .sentTime(postRecord.getTime())
                        .build()).orElse(null);
    }

    private EventNotificationDto setEventFriendship(Long id) {
        return dsl
                .selectFrom(Tables.NOTIFICATION)
                .where(Tables.NOTIFICATION.ID.eq(id))
                .fetchOptional()
                .map(notificationRecord -> EventNotificationDto.builder()
                        .authorId(notificationRecord.getEntityId())
                        .receiverId(notificationRecord.getPersonId())
                        .notificationType(NotificationType.FRIEND_REQUEST)
                        .content(NotificationType.FRIEND_REQUEST.getName())
                        .sentTime(notificationRecord.getSentTime())
                        .build()).orElse(null);
    }

    private EventNotificationDto setEventFriendBirthday(Long id) {
        return dsl
                .selectFrom(Tables.NOTIFICATION)
                .where(Tables.NOTIFICATION.ID.eq(id))
                .fetchOptional()
                .map(notificationRecord -> EventNotificationDto.builder()
                        .authorId(notificationRecord.getEntityId())
                        .receiverId(notificationRecord.getPersonId())
                        .notificationType(NotificationType.FRIEND_BIRTHDAY)
                        .content(NotificationType.FRIEND_BIRTHDAY.getName())
                        .sentTime(notificationRecord.getSentTime())
                        .build()).orElse(null);
    }
}
