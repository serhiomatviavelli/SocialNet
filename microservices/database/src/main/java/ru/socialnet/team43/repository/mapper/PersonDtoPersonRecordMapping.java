package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.PersonRecord;
import org.mapstruct.Mapper;
import ru.socialnet.team43.dto.PersonDto;
@Mapper(componentModel = "spring")
public interface PersonDtoPersonRecordMapping {

    PersonRecord PersonDtoToPersonRecord(PersonDto regDto);
    PersonDto PersonRecordToPersonDto(PersonRecord record);

}
