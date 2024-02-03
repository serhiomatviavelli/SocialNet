package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.LikeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.enums.LikeType;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static org.jooq.impl.DSL.field;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private final DSLContext context;
    private final AtomicLong postLikesCount = new AtomicLong();
    private final AtomicLong commentLikesCount = new AtomicLong();

    public LikeRecord addLikeToPost(Long id, LikeRecord record, Long userId) {
        context.update(Tables.POST)
                .set(field("LIKES_COUNT"), postLikesCount.incrementAndGet())
                .where(Tables.POST.ID.eq(id))
                .execute();
        return Objects.requireNonNull(context
                        .insertInto(Tables.LIKE)
                        .set(record)
                        .set(Tables.LIKE.ITEM_ID, id)
                        .set(Tables.LIKE.AUTHOR_ID, userId)
                        .set(Tables.LIKE.TIME, ZonedDateTime.now().toString())
                        .set(Tables.LIKE.IS_DELETED, false)
                        .returning()
                        .fetchOne())
                .into(LikeRecord.class);
    }

    public boolean deleteLikePost(Long id) {
        int result = context.update(Tables.LIKE)
                .set(Tables.LIKE.IS_DELETED, true)
                .where(Tables.LIKE.ITEM_ID.eq(id))
                .execute();
        if (result > 0) {
            context.update(Tables.POST)
                    .set(field("LIKES_COUNT"), postLikesCount.decrementAndGet())
                    .where(Tables.POST.ID.eq(id))
                    .execute();
        }
        return result > 0;
    }

    public LikeRecord addLikeToComment(Long commentId, Long authorId) {
        context.update(Tables.POST)
                .set(field("LIKES_COUNT"), postLikesCount.incrementAndGet())
                .where(Tables.POST.ID.eq(commentId))
                .execute();
        return Objects.requireNonNull(context
                        .insertInto(Tables.LIKE)
                        .set(Tables.LIKE.ITEM_ID, commentId)
                        .set(Tables.LIKE.AUTHOR_ID, authorId)
                        .set(Tables.LIKE.TIME, ZonedDateTime.now().toString())
                        .set(Tables.LIKE.IS_DELETED, false)
                        .set(Tables.LIKE.TYPE, LikeType.COMMENT.name())
                        .returning()
                        .fetchOne())
                .into(LikeRecord.class);
    }

    public boolean deleteCommentLike(Long commentId) {
        var result = context.update(Tables.LIKE)
                .set(Tables.LIKE.IS_DELETED, true)
                .where(Tables.LIKE.ITEM_ID.eq(commentId).and(Tables.LIKE.TYPE.eq(LikeType.COMMENT.name())))
                .execute();
        if (result > 0) {
            context.update(Tables.COMMENT)
                    .set(field("LIKE_AMOUNT"), commentLikesCount.decrementAndGet())
                    .where(Tables.COMMENT.ID.eq(commentId))
                    .execute();
        }
        return result > 0;
    }
}
