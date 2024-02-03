package ru.socialnet.team43.repository;

import jooq.db.tables.records.UserAuthRecord;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.data.domain.Pageable;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.UserAuthDto;

import java.util.List;
import java.util.Optional;

/** репозиторий для работы с таблицей user_auth */
public interface UserAuthRepository {
    Optional<UserAuthDto> getUserByEmail(String email);

    int getUsersCountByEmail(String email);

    Optional<UserAuthRecord> insertUserAuth(UserAuthRecord userAuthRecord);

    Optional<PersonDto> getAccountInfo(String email);

    void deleteUserAuthById(Long id);

    Optional<UserAuthRecord> setNewPassword(String password, String email);

    /**
     * получение объекта класса SelectConditionStep, использующегося для: - получения количества
     * результатов поиска профилей пользователей; - получения данных профилей пользователей.
     *
     * @param userName аккаунт (email) пользователя, осуществляющего поиск
     * @param accountSearchDto объект, содержащий параметры поиска профилей пользователей
     * @return объект класса SelectConditionStep
     */
    SelectConditionStep<Record> getSearchSelectConditionStep(
            String userName, AccountSearchDto accountSearchDto);

    /**
     * получение количества результатов поиска профилей пользователей
     *
     * @param selectConditionStep объект класса SelectConditionStep, содержащий в себе параметры
     *     запроса к БД
     * @return количество профилей пользователей, удовлетворяющих параметрам запроса
     */
    int getAccountSearchResultsQty(SelectConditionStep<Record> selectConditionStep);

    /**
     * получение данных профилей пользователей
     *
     * @param selectConditionStep объект класса SelectConditionStep, содержащий в себе параметры
     *     запроса к БД
     * @param pageable объект класса Pageable, содержащий в себе параметры постраничного вывода
     * @return список профилей пользователей, удовлетворяющих параметрам запроса
     */
    List<PersonDto> getAccountSearchResultsList(
            SelectConditionStep<Record> selectConditionStep, Pageable pageable);
}
