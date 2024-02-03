package ru.socialnet.team43.service;

import jooq.db.tables.records.PersonRecord;
import jooq.db.tables.records.UserAuthRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.dto.AccountSearchDto;
import ru.socialnet.team43.dto.UserAuthDto;
import ru.socialnet.team43.repository.PersonRepository;
import ru.socialnet.team43.repository.UserAuthRepository;
import ru.socialnet.team43.repository.mapper.PersonDtoPersonRecordMapping;
import ru.socialnet.team43.repository.mapper.PersonDtoUserAuthRecordMapper;
import ru.socialnet.team43.repository.mapper.UserAuthMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserAuthRepository userAuthRepo;
    private final PersonRepository personRepo;
    private final PersonDtoUserAuthRecordMapper mapper;
    private final PersonDtoPersonRecordMapping personMapper;

    private final UserAuthMapper userAuthMapper;

    @Override
    public Optional<UserAuthRecord> createUserAuth(RegDtoDb regDtoDb) {
        return userAuthRepo.insertUserAuth(mapper.regDtoDbToUserAuthRecord(regDtoDb));
    }

    @Override
    public Optional<UserAuthDto> getUserByEmail(String email) {
        return userAuthRepo.getUserByEmail(email);
    }

    @Override
    public int getUsersCountByEmail(String email) {
        return userAuthRepo.getUsersCountByEmail(email);
    }

    @Override
    public Optional<PersonDto> getAccountInfo(String email) {
        return userAuthRepo.getAccountInfo(email);
    }

    @Override
    public Optional<PersonDto> updateAccount(PersonDto dto) {
        PersonRecord record = personMapper.PersonDtoToPersonRecord(dto);
        String email = dto.getEmail();
        Optional<PersonRecord> resultRecord = personRepo.updatePerson(record, email);
        log.info("Update person with email: {}", dto.getEmail());
        return resultRecord.map(personMapper::PersonRecordToPersonDto);
    }

    @Override
    public int deleteAccount(String email) {
        int result = personRepo.deletePerson(email);
        log.info("delete person with email: {}, modify {} fields", email, result);
        return result;
    }

    @Override
    public void deleteUserAuthById(Long id) {
        userAuthRepo.deleteUserAuthById(id);
    }

    @Override
    public Optional<PersonDto> getAccountById(Long id) {
        Optional<PersonRecord> resultRecord = personRepo.getPersonById(id);
        log.info("get person by id: {}", id);
        return resultRecord.map(personMapper::PersonRecordToPersonDto);
    }

    @Override
    public Optional<UserAuthDto> setNewPassword(String password, String email) {
        Optional<UserAuthRecord> resultRecord = userAuthRepo.setNewPassword(password, email);
        log.info("set new password for email: {}", email);
        return resultRecord.map(userAuthMapper::userRecordToDtoMapper);
    }

    @Override
    public Page<PersonDto> getAccountsSearchResult(
            String userName, AccountSearchDto accountSearchDto, Pageable pageable) {
        Page<PersonDto> searchResult;

        SelectConditionStep<Record> selectConditionStep =
                userAuthRepo.getSearchSelectConditionStep(userName, accountSearchDto);

        int resultsQty = userAuthRepo.getAccountSearchResultsQty(selectConditionStep);

        if (0 != resultsQty) {
            List<PersonDto> resultsList =
                    userAuthRepo.getAccountSearchResultsList(selectConditionStep, pageable);
            searchResult = new PageImpl<>(resultsList, pageable, resultsQty);
        } else {
            searchResult = new PageImpl<>(Collections.emptyList());
        }

        return searchResult;
    }

    @Override
    public boolean updateIsOnline(String email, boolean isOnline) {
        int result = personRepo.updateIsOnline(email, isOnline);
        return result == 1;
    }
}
