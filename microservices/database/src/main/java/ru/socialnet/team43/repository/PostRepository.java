package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.PostRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.PostDto;
import ru.socialnet.team43.dto.TagDto;
import ru.socialnet.team43.dto.enums.PostType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

@AllArgsConstructor
@Repository
@Slf4j
public class PostRepository implements PostIteraction {

    private final DSLContext context;

    public Page<PostDto> getAll(List<Long> ids, List<Long> accountsId,
                                List<Long> blockedIds, String author,
                                String text, Boolean withFriends,
                                Boolean isBlocked, Boolean isDeleted,
                                OffsetDateTime dateFrom, OffsetDateTime dateTo,
                                List<String> tags, String sort, Pageable pageable) {
        List<PostDto> posts = context.select(asterisk()).from(Tables.POST)
                .where(getAllPostCondition(ids, accountsId, blockedIds, author,
                        text, withFriends, isBlocked, isDeleted, dateFrom, dateTo, tags))
                .orderBy((OrderField<?>) sorted(sort))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(PostDto.class);

        posts.forEach(post -> post.setTags(getTagsByPostId(post.getId())));

        long postsCount = getPostsCount(ids, accountsId, blockedIds, author, text, withFriends,
                isBlocked, isDeleted, dateFrom, dateTo, tags);

        return new PageImpl<>(posts, pageable, postsCount);
    }

    private int getPostsCount(List<Long> ids, List<Long> accountsId,
                               List<Long> blockedIds, String author,
                               String text, Boolean withFriends,
                               Boolean isBlocked, Boolean isDeleted,
                               OffsetDateTime dateFrom, OffsetDateTime dateTo, List<String> tags) {
        return Optional.ofNullable(context.selectCount().from(Tables.POST)
                .where(getAllPostCondition(ids, accountsId, blockedIds, author, text, withFriends,
                        isBlocked, isDeleted, dateFrom, dateTo, tags))
                .fetchOne(count())).orElse(0);
    }

    public Condition getAllPostCondition(List<Long> ids, List<Long> accountIds,
                                         List<Long> blockedIds, String author,
                                         String text, Boolean withFriends,
                                         Boolean isBlocked, Boolean isDeleted,
                                         OffsetDateTime dateFrom, OffsetDateTime dateTo,
                                         List<String> tags) {

        Condition condition = Tables.POST.IS_DELETED.eq(isDeleted);

        if (ids != null) {
            condition = condition.and(Tables.POST.ID.in(ids));
        }
        if (accountIds != null) {
            condition = condition.and(Tables.POST.AUTHOR_ID.in(accountIds));
        }
        if (blockedIds != null) {
            condition = condition.and(Tables.POST.AUTHOR_ID.in(blockedIds));
        }
        if (author != null) {
            condition = condition.and(Tables.POST.AUTHOR_ID.in(authorsIdsByFullName(author)));
        }
        if (text != null) {
            condition = condition.and(Tables.POST.POST_TEXT.like("%" + text + "%"));
        }
        if (isBlocked != null) {
            condition = condition.and(Tables.POST.IS_BLOCKED.eq(isBlocked));
        }
        if (dateFrom != null) {
            condition = condition.and(Tables.POST.TIME.greaterOrEqual(dateFrom));
        }
        if (dateTo != null) {
            condition = condition.and(Tables.POST.TIME.lessOrEqual(dateTo));
        }
        if (tags != null) {
            condition = condition.and(Tables.POST.ID.in(postsIdsByTags(tags)));
        }
        return condition;
    }

    public List<Long> authorsIdsByFullName(String author) {
        return context.select(Tables.PERSON.ID).from(Tables.PERSON)
                .where(concat(Tables.PERSON.FIRST_NAME, Tables.PERSON.LAST_NAME).like("%" + author + "%"))
                .fetchInto(Long.class);
    }

    public List<Long> postsIdsByTags(List<String> tags) {
        return context.select(Tables.POST2TAG.POST_ID).from(Tables.POST2TAG)
                .where(Tables.POST2TAG.TAG_ID.in(context.select(Tables.TAG.ID).from(Tables.TAG)
                        .where(Tables.TAG.NAME.in(tags))))
                .fetchInto(Long.class);
    }

    public Object sorted(String sort) {
        Object sortField = null;
        String[] params = sort.split(",+");
        if (Objects.equals(params[0], "time")) {
            if (Objects.equals(params[1], "desc")) {
                sortField = Tables.POST.TIME.desc();
            } else {
                sortField = Tables.POST.TIME.asc();
            }
        }
        return sortField;
    }

    public Optional<PostRecord> getPostById(Long id) {
        return context.selectFrom(Tables.POST)
                .where(Tables.POST.ID.eq(id))
                .and(Tables.POST.IS_DELETED.eq(false))
                .and(Tables.POST.IS_BLOCKED.eq(false))
                .and(Tables.POST.PUBLISH_DATE.lessOrEqual(OffsetDateTime.now()))
                .fetchOptional();
    }

    public Long addNewPost(PostRecord postRecord) {
        return context.insertInto(Tables.POST)
                .set(postRecord)
                .returning()
                .fetchOptional()
                .map(PostRecord::getId)
                .orElse(0L);
    }

    public void publishQueuedPost(Long id) {
        context.update(Tables.POST)
                .set(Tables.POST.TYPE, PostType.POSTED.name())
                .where(Tables.POST.ID.eq(id))
                .execute();
    }

    public void editPost(PostRecord postRecord) {
        context.update(Tables.POST)
                .set(Tables.POST.POST_TEXT, postRecord.getPostText())
                .set(Tables.POST.TITLE, postRecord.getTitle())
                .set(Tables.POST.TIME_CHANGED, OffsetDateTime.now())
                .set(Tables.POST.IMAGE_PATH, postRecord.getImagePath())
                .where(Tables.POST.ID.eq(postRecord.getId()))
                .execute();
    }

    public void deletePostById(Long id) {
        context.update(Tables.POST)
                .set(Tables.POST.IS_DELETED, true)
                .where(Tables.POST.ID.eq(id))
                .execute();
    }

    public void incrementCommentsCount(PostRecord postRecord) {
        context.update(Tables.POST)
                .set(Tables.POST.COMMENTS_COUNT, Tables.POST.COMMENTS_COUNT.add(1))
                .where(Tables.POST.ID.eq(postRecord.getId()))
                .execute();
    }

    public void decrementCommentsCount(PostRecord postRecord) {
        context.update(Tables.POST)
                .set(Tables.POST.COMMENTS_COUNT, postRecord.getCommentsCount() - 1)
                .where(Tables.POST.ID.eq(postRecord.getId()))
                .execute();
    }

    public List<TagDto> getTagsByPostId(Long postId) {
        return context.select(asterisk()).from(Tables.TAG)
                .where(Tables.TAG.ID.in(context.select(Tables.POST2TAG.TAG_ID).from(Tables.POST2TAG)
                        .where(Tables.POST2TAG.POST_ID.eq(postId))))
                .fetchInto(TagDto.class);
    }

}
