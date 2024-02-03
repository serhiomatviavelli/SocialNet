package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.PersonRecord;
import jooq.db.tables.records.UserAuthRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PersonRepository implements UserInteraction, PersonIteraction {

    private final DSLContext dslContext;

    public Optional<PersonRecord> insertPerson(PersonRecord personRecord) {
        return dslContext.insertInto(Tables.PERSON)
                .set(personRecord)
                .returning()
                .fetchOptional();
    }

    public int deletePerson(String email) {

        Long id = findUserIdByEmail(email);

        return dslContext.update(Tables.PERSON)
                .set(Tables.PERSON.IS_DELETED, true)
                .where(Tables.PERSON.USER_ID.eq(id))
                .execute();
    }

    public Optional<PersonRecord> updatePerson(PersonRecord record, String email) {

        Long id = findUserIdByEmail(email);

        Optional<PersonRecord> optionalDbRecord = dslContext.selectFrom(Tables.PERSON)
                .where(Tables.PERSON.USER_ID.eq(id))
                .fetchOptional();

        if (optionalDbRecord.isPresent()) {
            PersonRecord dbRecord = optionalDbRecord.get();
            fillAccount(record, dbRecord);

            return dslContext.update(Tables.PERSON)
                    .set(dslContext.newRecord(Tables.PERSON, record))
                    .where(Tables.PERSON.USER_ID.eq(id))
                    .returning()
                    .fetchOptional();
        }
        return Optional.empty();
    }

    public int updateIsOnline(String email, boolean isOnline) {
        Long id = findUserIdByEmail(email);

        return dslContext
                .update(Tables.PERSON)
                .set(Tables.PERSON.IS_ONLINE, isOnline)
                .set(Tables.PERSON.LAST_ONLINE_TIME, OffsetDateTime.now())
                .where(Tables.PERSON.USER_ID.eq(id))
                .execute();
    }

    public Long findUserIdByEmail(String email) {
        return dslContext.selectFrom(Tables.USER_AUTH)
                .where(Tables.USER_AUTH.EMAIL.eq(email))
                .fetchOptional()
                .map(UserAuthRecord::getId)
                .orElse(0L);
    }

    public Long getPersonIdByEmail(String email) {
        return dslContext.selectFrom(Tables.PERSON)
                .where(Tables.PERSON.USER_ID.eq(
                        dslContext.select(Tables.USER_AUTH.ID)
                        .from(Tables.USER_AUTH)
                        .where(Tables.USER_AUTH.EMAIL.eq(email))
                ))
                .fetchOptional()
                .map(PersonRecord::getId)
                .orElse(null);
    }

    public Optional<PersonRecord> getPersonById(Long id) {
        return dslContext.selectFrom(Tables.PERSON)
                .where(Tables.PERSON.ID.eq(id))
                .fetchOptional();

    }

    private void fillAccount(PersonRecord dest, PersonRecord src) {
        dest.setRegDate(src.getRegDate());
        dest.setStatusCode(src.getStatusCode());
        dest.setMessagesPermission(src.getMessagesPermission());
        dest.setIsBlocked(src.getIsBlocked());
        dest.setCreatedDate(src.getCreatedDate());
        dest.setLastOnlineTime(OffsetDateTime.now());
        dest.setIsDeleted(src.getIsDeleted());
        dest.setIsOnline(true);
        dest.setLastModifiedDate(LocalDateTime.now());
    }

    public boolean isBlocked(Long id) {

        return dslContext.selectFrom(Tables.PERSON)
                .where(Tables.PERSON.ID.eq(id))
                .fetchOptional()
                .map(PersonRecord::getIsBlocked)
                .orElse(true);
    }

}
