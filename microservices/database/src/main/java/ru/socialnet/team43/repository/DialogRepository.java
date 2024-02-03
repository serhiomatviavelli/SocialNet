package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.DialogRecord;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogRepository {

    private final DSLContext dsl;

    public List<DialogRecord> getDialogsByUser(Long userId) {
        return dsl.selectFrom(Tables.DIALOG)
                .where(Tables.DIALOG.CONVERSATION_PARTNER_1.eq(userId)
                        .or(Tables.DIALOG.CONVERSATION_PARTNER_2.eq(userId)))
                .and(Tables.DIALOG.IS_DELETED.eq(false))
                .fetchInto(DialogRecord.class);
    }

    public DialogRecord getDialogByRecipientId(Long recipientId, Long userId) {
        return dsl.selectFrom(Tables.DIALOG)
                .where(Tables.DIALOG.CONVERSATION_PARTNER_1.eq(userId)
                        .and(Tables.DIALOG.CONVERSATION_PARTNER_2.eq(recipientId)))
                .or(Tables.DIALOG.CONVERSATION_PARTNER_1.eq(recipientId)
                        .and(Tables.DIALOG.CONVERSATION_PARTNER_2.eq(userId)))
                .and(Tables.DIALOG.IS_DELETED.eq(false))
                .fetchOne();
    }

    public Integer totalCountDialogsByUser(Long userId) {
        return dsl.selectCount()
                .from(Tables.DIALOG)
                .where(Tables.DIALOG.CONVERSATION_PARTNER_1.eq(userId)
                        .or(Tables.DIALOG.CONVERSATION_PARTNER_2.eq(userId)))
                .and(Tables.DIALOG.IS_DELETED.eq(false))
                .fetchAny(0, Integer.class);
    }

    public DialogRecord insertDialog(DialogRecord dialogRecord) {
        dialogRecord.setIsDeleted(false);
        return dsl.insertInto(Tables.DIALOG)
                .set(dialogRecord)
                .returning()
                .fetchOne();
    }
}
