package ru.socialnet.team43.repository;

import jooq.db.tables.records.PersonRecord;

import java.util.Optional;

public interface UserInteraction {

    Optional<PersonRecord> insertPerson(PersonRecord personRecord);
    int deletePerson(String email);
    Optional<PersonRecord> updatePerson(PersonRecord record, String email);
    Long findUserIdByEmail(String email);
    Optional<PersonRecord> getPersonById(Long id);

}
