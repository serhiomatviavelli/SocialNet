package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.CommentRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.CommentDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.asterisk;

@AllArgsConstructor
@Repository
public class CommentRepository {

    private final DSLContext context;

    public Page<CommentDto> getComments(Long postId, Boolean isDeleted, String sort, Pageable pageable) {
        List<CommentDto> comments = context.select(asterisk()).from(Tables.COMMENT)
                .where(getCommentsCondition(postId, isDeleted))
                .orderBy((OrderField<?>) sorted(sort))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(CommentDto.class);
        return new PageImpl<>(comments, pageable, getCommentsCount(postId, isDeleted));
    }

    public Page<CommentDto> getSubComments(Long postId, Long commentId, Boolean isDeleted,
                                           String sort, Pageable pageable) {
        List<CommentDto> subComments = context.select(asterisk()).from(Tables.COMMENT)
                .where(getSubCommentsCondition(postId, commentId, isDeleted))
                .orderBy((OrderField<?>) sorted(sort))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(CommentDto.class);
        return new PageImpl<>(subComments, pageable, getSubCommentsCount(postId, commentId, isDeleted));
    }

    private long getCommentsCount(Long postId, Boolean isDeleted) {
        return context.fetchCount(context.select().from(Tables.COMMENT)
                .where(getCommentsCondition(postId, isDeleted)));
    }

    private long getSubCommentsCount(Long postId, Long commentId, Boolean isDeleted) {
        return context.fetchCount(context.select().from(Tables.COMMENT)
                .where(getSubCommentsCondition(postId, commentId, isDeleted)));
    }

    public Optional<CommentRecord> getCommentById(Long id) {
        return context.selectFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(id))
                .and(Tables.COMMENT.IS_DELETED.eq(false))
                .and(Tables.COMMENT.IS_BLOCKED.eq(false))
                .fetchOptional();
    }

    public Condition getCommentsCondition(Long postId, Boolean isDeleted) {
        Condition condition = Tables.COMMENT.POST_ID.eq(postId)
                .and(Tables.COMMENT.COMMENT_TYPE.eq("POST"))
                .and(Tables.COMMENT.PARENT_ID.isNull());
        if (isDeleted != null) {
            condition = condition.and(Tables.COMMENT.IS_DELETED.eq(isDeleted));
        }
        return condition;
    }

    public Condition getSubCommentsCondition(Long postId, Long commentId, Boolean isDeleted) {
        Condition condition = Tables.COMMENT.POST_ID.eq(postId)
                .and(Tables.COMMENT.COMMENT_TYPE.eq("COMMENT"))
                .and(Tables.COMMENT.PARENT_ID.eq(commentId));
        if (isDeleted != null) {
            condition = condition.and(Tables.COMMENT.IS_DELETED.eq(isDeleted));
        }
        return condition;
    }

    public Object sorted(String sort) {
        Object sortField = null;
        String[] params = sort.split(",+");
        if (Objects.equals(params[0], "time")) {
            if (Objects.equals(params[1], "desc")) {
                sortField = Tables.COMMENT.TIME_CHANGED.desc();
            } else {
                sortField = Tables.COMMENT.TIME_CHANGED.asc();
            }
        }
        return sortField;
    }

    public Long addNewComment(Long postId, CommentRecord commentRecord) {
        return context.insertInto(Tables.COMMENT)
                .set(commentRecord)
                .set(Tables.COMMENT.POST_ID, postId)
                .returning()
                .fetchOptional()
                .map(CommentRecord::getId)
                .orElse(0L);
    }

    public void editComment(CommentRecord commentRecord) {
        context.update(Tables.COMMENT)
                .set(Tables.COMMENT.COMMENT_TEXT, commentRecord.getCommentText())
                .set(Tables.COMMENT.TIME_CHANGED, OffsetDateTime.now())
                .where(Tables.COMMENT.ID.eq(commentRecord.getId()))
                .execute();
    }

    public void deleteComment(Long postId, Long commentId) {
        context.update(Tables.COMMENT)
                .set(Tables.COMMENT.IS_DELETED, true)
                .where(Tables.COMMENT.ID.eq(commentId))
                .and(Tables.COMMENT.POST_ID.eq(postId))
                .execute();
    }

    public List<CommentRecord> getSubCommentsByParentId(Long parentId) {
        return context.select(asterisk()).from(Tables.COMMENT)
                .where(Tables.COMMENT.PARENT_ID.eq(parentId))
                .orderBy(Tables.COMMENT.TIME_CHANGED.desc())
                .fetchInto(CommentRecord.class);
    }

    public void incrementCommentsCount(CommentRecord commentRecord) {
        context.update(Tables.COMMENT)
                .set(Tables.COMMENT.COMMENTS_COUNT, Tables.COMMENT.COMMENTS_COUNT.add(1))
                .where(Tables.COMMENT.ID.eq(commentRecord.getParentId()))
                .execute();
    }

    public void decrementCommentsCount(CommentRecord commentRecord) {
        context.update(Tables.COMMENT)
                .set(Tables.COMMENT.COMMENTS_COUNT, commentRecord.getCommentsCount() - 1)
                .where(Tables.COMMENT.ID.eq(commentRecord.getId()))
                .execute();
    }

}
