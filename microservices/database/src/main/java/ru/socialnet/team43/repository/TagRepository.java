package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.TagRecord;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.TagDto;

import java.util.List;

import static org.jooq.impl.DSL.asterisk;

@AllArgsConstructor
@Repository
public class TagRepository {

    private final DSLContext context;

    public List<TagDto> getByName(String name) {
        return context.select(asterisk()).from(Tables.TAG)
                .where(Tables.TAG.NAME.like("%" + name + "%"))
                .and(Tables.TAG.IS_DELETED.eq(false))
                .fetchInto(TagDto.class);
    }

    public TagRecord getByFullName(String name) {
        return context.selectFrom(Tables.TAG)
                .where(Tables.TAG.NAME.eq(name))
                .fetchAny();
    }

    public Long addNewTag(String name) {
       TagRecord tagRecord = getByFullName(name);
        if (tagRecord == null) {
            return context.insertInto(Tables.TAG)
                    .set(Tables.TAG.NAME, name)
                    .set(Tables.TAG.IS_DELETED, false)
                    .returning()
                    .fetchOptional()
                    .map(TagRecord::getId)
                    .orElse(0L);
        } else {
            return tagRecord.getId();
        }
    }

}
