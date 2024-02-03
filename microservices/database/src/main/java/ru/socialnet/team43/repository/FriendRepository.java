package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.FriendshipRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.friends.FriendDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.enums.FriendshipStatus;
import ru.socialnet.team43.dto.friends.FriendSearchResponseDto;
import ru.socialnet.team43.repository.mapper.PersonDtoPersonRecordMapping;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FriendRepository {

    private final DSLContext dslContext;
    private static final int COUNT_FRIENDS_RECOMMENDATIONS = 20;

    private final PersonRepository personRepo;
    private final PersonDtoPersonRecordMapping mapper;

    public int getFriendsCount(String email) {
        Optional<Integer> count = ofNullable(dslContext.selectCount().from(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(getPersonIdConditionStep(email)))
                .and(Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq("REQUEST_FROM"))
                .fetchOne(0, int.class));
        return count.orElse(0);
    }

    public List<FriendSearchResponseDto> findFriendsIdsByStatus(String status, String email, Pageable page) {

        return dslContext.selectFrom(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq(status))
                .and(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(getPersonIdConditionStep(email)))
                .limit(page.getPageSize())
                .stream()
                .map(f -> new FriendSearchResponseDto(FriendshipStatus.valueOf(f.getFriendshipStatus()),
                        f.getId(),
                        f.getDscPersonId()))
                .collect(Collectors.toList());
    }

    public List<PersonDto> searchFriends(String status,
                                         String firstName,
                                         Integer ageFrom,
                                         Integer ageTo,
                                         String country,
                                         String city,
                                         String email,
                                         Pageable page) {

        Condition searchCondition = getConditions(firstName, ageFrom, ageTo, country, city);

        return dslContext.selectFrom(Tables.PERSON)
                .where(Tables.PERSON.ID.in(
                        dslContext.select(Tables.FRIENDSHIP.DSC_PERSON_ID)
                                .from(Tables.FRIENDSHIP)
                                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(getPersonIdConditionStep(email)))
                                .and(Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq(status))))
                .and(searchCondition)
                .limit(page.getPageSize())
                .stream()
                .map(mapper::PersonRecordToPersonDto)
                .collect(Collectors.toList());
    }

    private Condition getConditions(String firstName,
                                    Integer ageFrom,
                                    Integer ageTo,
                                    String country,
                                    String city) {

        Condition condition = trueCondition();

        if (!firstName.equals("")) {
            condition = condition.and(Tables.PERSON.FIRST_NAME.equalIgnoreCase(firstName));
        }

        if (ageFrom != 0) {
            condition = condition.and(Tables.PERSON.BIRTH_DATE.le(localDateTimeSub(
                    currentLocalDateTime(),
                    ageFrom,
                    DatePart.YEAR)));
        }

        if (ageTo != 99) {
            condition = condition.and(Tables.PERSON.BIRTH_DATE.ge(localDateTimeSub(
                    currentLocalDateTime(),
                    ageTo,
                    DatePart.YEAR)));
        }

        if (!country.equals("")) {
            condition = condition.and(Tables.PERSON.COUNTRY.eq(country));
        }

        if (!city.equals("")) {
            condition = condition.and(Tables.PERSON.CITY.eq(city));
        }

        return condition;
    }

    public int deleteFriendship(Long srcId, Long dscId) {
        return dslContext.delete(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dscId))
                .or(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(dscId)
                        .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(srcId)))
                .execute();
    }

    public int save(Long srcId, Long dscId, String status) {
        return dslContext.insertInto(Tables.FRIENDSHIP)
                .set(Tables.FRIENDSHIP.SRC_PERSON_ID, srcId)
                .set(Tables.FRIENDSHIP.DSC_PERSON_ID, dscId)
                .set(Tables.FRIENDSHIP.FRIENDSHIP_STATUS, status)
                .execute();
    }

    public int saveFriendship(Long srcId, Long dscId, String srcStatus, String dscStatus) {
        return dslContext.insertInto(Tables.FRIENDSHIP,
                        Tables.FRIENDSHIP.SRC_PERSON_ID,
                        Tables.FRIENDSHIP.DSC_PERSON_ID,
                        Tables.FRIENDSHIP.FRIENDSHIP_STATUS)
                .values(srcId, dscId, srcStatus)
                .values(dscId, srcId, dscStatus)
                .execute();
    }

    public int updateStatus(Long srcId, Long dscId, String status) {
        return dslContext.update(Tables.FRIENDSHIP)
                .set(Tables.FRIENDSHIP.FRIENDSHIP_STATUS, status)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dscId))
                .or(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(dscId)
                        .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(srcId)))
                .execute();
    }

    public String getStatus(Long srcId, Long dscId) {
        return dslContext.selectFrom(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dscId))
                .fetchOptional()
                .map(FriendshipRecord::getFriendshipStatus)
                .orElse("NONE");
    }

    public int setStatusAsFriend(Long srcId, Long dscId) {
        return dslContext.update(Tables.FRIENDSHIP)
                .set(Tables.FRIENDSHIP.FRIENDSHIP_STATUS, "FRIEND")
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dscId))
                .execute();
    }

    public Optional<FriendDto> getOptionalFriendDtoById(Long dcsId, String email) {

        Long srcId = personRepo.getPersonIdByEmail(email);

        return dslContext.selectFrom(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dcsId))
                .fetchOptional()
                .map(f -> new FriendDto(FriendshipStatus.valueOf(f.getFriendshipStatus()), dcsId));
    }

    public int delete(Long srcId, Long dscId) {
        return dslContext.delete(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(srcId))
                .and(Tables.FRIENDSHIP.DSC_PERSON_ID.eq(dscId))
                .execute();
    }

    public int getCountSearchByStatus(String status, String email) {
        return Optional.ofNullable(dslContext.selectCount()
                        .from(Tables.FRIENDSHIP)
                        .where(Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq(status))
                        .and(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(getPersonIdConditionStep(email)))
                        .fetchOne(0, int.class))
                .orElse(0);
    }

    public Map<Long, Integer> getRecommendations(String email) {
        return dslContext.select(Tables.FRIENDSHIP.SRC_PERSON_ID, count())
                .from(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.DSC_PERSON_ID.in(getIdsFriendsConditionStep(email)))
                .and(Tables.FRIENDSHIP.SRC_PERSON_ID.notEqual(getPersonIdConditionStep(email)))
                .and(Tables.FRIENDSHIP.SRC_PERSON_ID.notIn(getIdsFriendsConditionStep(email)))
                .groupBy(Tables.FRIENDSHIP.SRC_PERSON_ID)
                .orderBy(count().desc())
                .limit(COUNT_FRIENDS_RECOMMENDATIONS)
                .fetchMap(Tables.FRIENDSHIP.SRC_PERSON_ID, count());
    }

    private SelectConditionStep<Record1<Long>> getPersonIdConditionStep(String email) {
        return dslContext.select(Tables.PERSON.ID)
                .from(Tables.PERSON)
                .where(Tables.PERSON.USER_ID.eq(
                        dslContext.select(Tables.USER_AUTH.ID)
                                .from(Tables.USER_AUTH)
                                .where(Tables.USER_AUTH.EMAIL.eq(email))));
    }

    private SelectConditionStep<Record1<Long>> getIdsFriendsConditionStep(String email){
        return dslContext.select(Tables.FRIENDSHIP.DSC_PERSON_ID)
                .from(Tables.FRIENDSHIP)
                .where(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(getPersonIdConditionStep(email)))
                .and(Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq("FRIEND"));
    }
}
