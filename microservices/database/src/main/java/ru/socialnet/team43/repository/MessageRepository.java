package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.MessageRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final DSLContext dsl;

    public List<MessageRecord> getMessagesByDialogId(Long dialogId, Pageable page) {
        SortField<?>[] sortFields = page.getSort().stream()
                .map(order -> {
                    Field<?> field = Tables.MESSAGE.field(order.getProperty());
                    return field == null ? null : (order.getDirection().isDescending() ? field.desc() : field.asc());
                })
                .toArray(SortField[]::new);

        return dsl.selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.IS_DELETED.eq(false))
                .orderBy(sortFields)
                .limit(page.getPageSize())
                .offset(page.getOffset())
                .fetchInto(MessageRecord.class);
    }

    public List<MessageRecord> getLastMessageByDialogId(Long dialogId, Long userId) {
        List<MessageRecord> messageList = dsl.selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.RECIPIENT_ID.eq(userId))
                .and(Tables.MESSAGE.READ_STATUS.eq("SENT"))
                .and(Tables.MESSAGE.IS_DELETED.eq(false))
                .fetchInto(MessageRecord.class);

        if (messageList.size() > 0) {
            return messageList;
        }

        MessageRecord lastMessage = dsl.selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.IS_DELETED.eq(false))
                .orderBy(Tables.MESSAGE.TIME.desc())
                .fetchAny();

        messageList.add(lastMessage);
        return messageList;
    }

    public Integer unreadCountForDialog(Long dialogId, Long userId) {
        return dsl.selectCount().from(Tables.MESSAGE)
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.RECIPIENT_ID.eq(userId))
                .and(Tables.MESSAGE.READ_STATUS.eq("SENT"))
                .and(Tables.MESSAGE.IS_DELETED.eq(false))
                .fetchOne(0, Integer.class);
    }

    public Integer allUnreadCountByUser(Long userId) {
        return dsl.selectCount().from(dsl.selectDistinct(Tables.MESSAGE.AUTHOR_ID)
                        .from(Tables.MESSAGE)
                        .where(Tables.MESSAGE.RECIPIENT_ID.eq(userId))
                        .and(Tables.MESSAGE.READ_STATUS.eq("SENT"))
                        .and(Tables.MESSAGE.IS_DELETED.eq(false)))
                .fetchOne(0, Integer.class);
    }

    public Integer totalCountMessagesByDialogId(Long dialogId) {
        return dsl.selectCount().from(Tables.MESSAGE)
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.IS_DELETED.eq(false))
                .fetchAny(0, Integer.class);
    }

    public boolean insertMessage(MessageRecord messageRecord) {
        messageRecord.setIsDeleted(false);
        dsl.insertInto(Tables.MESSAGE).set(messageRecord).execute();
        return true;
    }

    public boolean updateMessages(Long dialogId, Long userId) {
        dsl.update(Tables.MESSAGE)
                .set(Tables.MESSAGE.READ_STATUS, "READ")
                .where(Tables.MESSAGE.DIALOG_ID.eq(dialogId))
                .and(Tables.MESSAGE.RECIPIENT_ID.eq(userId))
                .and(Tables.MESSAGE.READ_STATUS.eq("SENT"))
                .execute();
        return true;
    }
}
