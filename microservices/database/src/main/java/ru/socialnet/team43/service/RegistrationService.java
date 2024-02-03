package ru.socialnet.team43.service;

import jooq.db.tables.records.PersonRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.RegDtoDb;
import ru.socialnet.team43.repository.PersonRepository;
import ru.socialnet.team43.repository.mapper.RegDtoPersonRecordMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final PersonRepository personRepo;
    private final RegDtoPersonRecordMapper mapper;

    public Optional<PersonRecord> createPerson(RegDtoDb regDtoDb, long id)
    {
        PersonRecord recordToSave = mapper.regDtoDbToPersonRecord(regDtoDb);
        fillPersonDefaultValues(recordToSave);
        recordToSave.setUserId(id);
        return personRepo.insertPerson(recordToSave);
    }

    private void fillPersonDefaultValues(PersonRecord record){
        record.setIsOnline(true);
        record.setIsDeleted(false);
        record.setIsBlocked(false);
        record.setLastOnlineTime(OffsetDateTime.now());
        record.setLastModifiedDate(LocalDateTime.now());
        record.setRegDate(LocalDateTime.now());
        record.setCreatedDate(LocalDateTime.now());
    }
}
