package ru.socialnet.team43.service;

import jooq.db.tables.records.UserAuthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.dto.UserAuthDto;

import java.util.Optional;

/** сервис для работы с таблицей user_auth */
public interface UserAuthService {
    Optional<UserAuthRecord> createUserAuth(RegDtoDb dto);

    Optional<UserAuthDto> getUserByEmail(String email);

    int getUsersCountByEmail(String email);

    Optional<PersonDto> getAccountInfo(String email);

    Optional<PersonDto> updateAccount(PersonDto dto);

    int deleteAccount(String email);


    void deleteUserAuthById(Long id);

    Optional<PersonDto> getAccountById(Long id);

    Optional<UserAuthDto> setNewPassword(String password, String email);

    Page<PersonDto> getAccountsSearchResult(
            String userName, AccountSearchDto accountSearchDto, Pageable pageable);

    boolean updateIsOnline(String email, boolean isOnline);
}
