package ru.socialnet.team43.repository.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.mapstruct.Mapper;
import ru.socialnet.team43.dto.PersonDto;

@Mapper(componentModel = "spring")
public interface PersonDtoPersonInfoMapper extends RecordMapper<Record, PersonDto> {

    @Override
    PersonDto map(Record record);
}

