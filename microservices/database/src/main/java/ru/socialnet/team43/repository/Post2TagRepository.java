package ru.socialnet.team43.repository;

import jooq.db.Tables;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class Post2TagRepository {

    private final DSLContext context;

    public void addNewRecords(Long postId, List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            context.insertInto(Tables.POST2TAG)
                    .set(Tables.POST2TAG.POST_ID, postId)
                    .set(Tables.POST2TAG.TAG_ID, tagId)
                    .returning()
                    .fetch();
        });
    }

}
