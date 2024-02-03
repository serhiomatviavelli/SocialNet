package ru.socialnet.team43.repository;

import jooq.db.Tables;
import jooq.db.tables.records.UserAuthRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.dto.enums.FriendshipStatus;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserAuthRepositoryImpl implements UserAuthRepository {
    private final DSLContext dslContext;

    @Override
    public Optional<UserAuthDto> getUserByEmail(String email) {
        List<UserAuthDto> userList =
                dslContext
                        .selectFrom(Tables.USER_AUTH)
                        .where(Tables.USER_AUTH.EMAIL.eq(email))
                        .fetchInto(UserAuthDto.class);

        UserAuthDto user = (0 != userList.size()) ? userList.get(0) : null;

        return Optional.ofNullable(user);
    }

    @Override
    public int getUsersCountByEmail(String email) {
        return dslContext
                .selectFrom(Tables.USER_AUTH)
                .where(Tables.USER_AUTH.EMAIL.eq(email))
                .execute();
    }

    @Override
    public Optional<UserAuthRecord> insertUserAuth(UserAuthRecord userAuthRecord) {
        return dslContext
                .insertInto(Tables.USER_AUTH)
                .set(userAuthRecord)
                .returning()
                .fetchOptional();
    }

    @Override
    public Optional<PersonDto> getAccountInfo(String email) {
        return dslContext
                .select()
                .from(
                        Tables.USER_AUTH
                                .leftOuterJoin(Tables.PERSON)
                                .on(Tables.USER_AUTH.ID.eq(Tables.PERSON.USER_ID)))
                .where(Tables.USER_AUTH.EMAIL.eq(email))
                .fetchOptionalInto(PersonDto.class);
    }

    @Override
    public void deleteUserAuthById(Long id) {
        int result =
                dslContext.deleteFrom(Tables.USER_AUTH).where(Tables.USER_AUTH.ID.eq(id)).execute();
    }

    @Override
    public SelectConditionStep<Record> getSearchSelectConditionStep(
            String userName, AccountSearchDto accountSearchDto) {
        Condition condition = getAccountSearchCondition(userName, accountSearchDto);

        SelectConditionStep<Record> selectConditionStep =
                dslContext
                        .select()
                        .from(
                                Tables.USER_AUTH
                                        .innerJoin(Tables.PERSON)
                                        .on(Tables.USER_AUTH.ID.eq(Tables.PERSON.USER_ID)))
                        .where(condition);

        return selectConditionStep;
    }

    @Override
    public Optional<UserAuthRecord> setNewPassword(String password, String email) {

        return dslContext
                .update(Tables.USER_AUTH)
                .set(Tables.USER_AUTH.PASSWORD, password)
                .where(Tables.USER_AUTH.EMAIL.eq(email))
                .returning()
                .fetchOptional();
    }

    /**
     * создание условий поиска профилей пользователей
     *
     * @param userName аккаунт (email) пользователя, осуществляющего поиск
     * @param accountSearchDto объект, содержащий параметры поиска профилей пользователей
     * @return экземпляр класса Condition, содержащий условия поиска профилей пользователей
     */
    private Condition getAccountSearchCondition(
            String userName, AccountSearchDto accountSearchDto) {
        Condition condition = Tables.PERSON.IS_DELETED.eq(accountSearchDto.getIsDeleted());

        condition = condition.and(Tables.USER_AUTH.EMAIL.ne(userName));

        if (accountSearchDto.getAuthor() != null && !accountSearchDto.getAuthor().isEmpty()) {
            condition =
                    condition.and(lower(Tables.USER_AUTH.EMAIL).in(accountSearchDto.getAuthor()));
        }

        if (accountSearchDto.getIds() != null && !accountSearchDto.getIds().isEmpty()) {
            condition = condition.and(Tables.PERSON.ID.in(accountSearchDto.getIds()));
        }

        boolean isFirstNameFilled =
                (accountSearchDto.getFirstName() != null)
                        && !accountSearchDto.getFirstName().isEmpty();
        boolean isLastNameFilled =
                (accountSearchDto.getLastName() != null
                        && !accountSearchDto.getLastName().isEmpty());

        if (isFirstNameFilled && isLastNameFilled) {
            Condition nameCondition =
                    lower(Tables.PERSON.FIRST_NAME).in(accountSearchDto.getFirstName());
            nameCondition =
                    nameCondition.or(
                            lower(Tables.PERSON.LAST_NAME).in(accountSearchDto.getLastName()));
            condition = condition.and(nameCondition);
        } else if (isFirstNameFilled) {
            condition =
                    condition.and(
                            lower(Tables.PERSON.FIRST_NAME).in(accountSearchDto.getFirstName()));
        } else if (isLastNameFilled) {
            condition =
                    condition.and(
                            lower(Tables.PERSON.LAST_NAME).in(accountSearchDto.getLastName()));
        }

        boolean isCountryFilled =
                (accountSearchDto.getCountry() != null) && !accountSearchDto.getCountry().isEmpty();
        boolean isCityFilled =
                (accountSearchDto.getCity() != null) && !accountSearchDto.getCity().isEmpty();

        if (isCountryFilled && isCityFilled) {
            Condition locationCondition =
                    lower(Tables.PERSON.COUNTRY).in(accountSearchDto.getCountry());
            locationCondition =
                    locationCondition.or(lower(Tables.PERSON.CITY).in(accountSearchDto.getCity()));
            condition = condition.and(locationCondition);
        } else if (isCountryFilled) {
            condition =
                    condition.and(lower(Tables.PERSON.COUNTRY).in(accountSearchDto.getCountry()));
        } else if (isCityFilled) {
            condition = condition.and(lower(Tables.PERSON.CITY).in(accountSearchDto.getCity()));
        }

        if (accountSearchDto.getAgeFrom() != null && accountSearchDto.getAgeFrom() != 0) {
            condition =
                    condition.and(
                            Tables.PERSON.BIRTH_DATE.le(
                                    localDateTimeSub(
                                            currentLocalDateTime(),
                                            accountSearchDto.getAgeFrom(),
                                            DatePart.YEAR)));
        }

        if (accountSearchDto.getAgeTo() != null && accountSearchDto.getAgeTo() != 0) {
            condition =
                    condition.and(
                            Tables.PERSON.BIRTH_DATE.ge(
                                    localDateTimeSub(
                                            currentLocalDateTime(),
                                            accountSearchDto.getAgeTo(),
                                            DatePart.YEAR)));
        }

        if (isFriendshipStatusCorrect(accountSearchDto.getStatusCode())) {
            List<Long> counterpartsIds =
                    getCounterpartsIdsByFriendshipStatus(
                            userName, accountSearchDto.getStatusCode());
            if (counterpartsIds.size() > 0) {
                condition = condition.and(Tables.PERSON.ID.in(counterpartsIds));
            }
        } else if (areEmptyRequestParams(accountSearchDto)) {
            List<Long> counterpartsIds =
                    getCounterpartsIdsByFriendshipStatus(userName, FriendshipStatus.FRIEND.name());
            if (counterpartsIds.size() > 0) {
                condition = condition.and(Tables.PERSON.ID.notIn(counterpartsIds));
            }
        }

        return condition;
    }

    @Override
    public int getAccountSearchResultsQty(SelectConditionStep<Record> selectConditionStep) {
        int resultsQty = dslContext.fetchCount(selectConditionStep);

        return resultsQty;
    }

    @Override
    public List<PersonDto> getAccountSearchResultsList(
            SelectConditionStep<Record> selectConditionStep, Pageable pageable) {
        List<PersonDto> foundAccounts =
                selectConditionStep
                        .orderBy(Tables.USER_AUTH.EMAIL)
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetchInto(PersonDto.class);

        return foundAccounts;
    }

    private boolean isFriendshipStatusCorrect(String friendshipStatus) {
        try {
            FriendshipStatus.valueOf(friendshipStatus);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            return false;
        }
        return true;
    }

    private List<Long> getCounterpartsIdsByFriendshipStatus(
            String userName, String inputFriendshipStatus) {
        List<Long> counterpartsIds;

        counterpartsIds =
                dslContext
                        .select(Tables.FRIENDSHIP.DSC_PERSON_ID)
                        .from(
                                Tables.FRIENDSHIP
                                        .join(Tables.PERSON)
                                        .on(Tables.FRIENDSHIP.SRC_PERSON_ID.eq(Tables.PERSON.ID))
                                        .join(Tables.USER_AUTH)
                                        .on(Tables.PERSON.USER_ID.eq(Tables.USER_AUTH.ID)))
                        .where(
                                Tables.USER_AUTH
                                        .EMAIL
                                        .eq(userName)
                                        .and(
                                                Tables.FRIENDSHIP.FRIENDSHIP_STATUS.eq(
                                                        inputFriendshipStatus)))
                        .fetchInto(Long.class);

        return counterpartsIds;
    }

    private boolean areEmptyRequestParams(AccountSearchDto accountSearchDto) {
        return (accountSearchDto.getId() == null
                && accountSearchDto.getIds() == null
                && accountSearchDto.getBlockedByIds() == null
                && accountSearchDto.getAuthor() == null
                && accountSearchDto.getFirstName() == null
                && accountSearchDto.getLastName() == null
                && accountSearchDto.getCity() == null
                && accountSearchDto.getCountry() == null
                && accountSearchDto.getIsBlocked() == null
                && accountSearchDto.getStatusCode() == null
                && accountSearchDto.getAgeTo() == null
                && accountSearchDto.getAgeFrom() == null);
    }
}
