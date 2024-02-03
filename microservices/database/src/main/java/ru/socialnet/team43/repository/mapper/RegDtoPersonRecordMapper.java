package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.PersonRecord;
import org.mapstruct.Mapper;
import ru.socialnet.team43.dto.RegDtoDb;


@Mapper(componentModel = "spring")
public interface RegDtoPersonRecordMapper {

    PersonRecord regDtoDbToPersonRecord(RegDtoDb regDto);
}
