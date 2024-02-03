package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.LikeRecord;
import org.mapstruct.Mapper;
import ru.socialnet.team43.dto.LikeDto;

@Mapper(componentModel = "spring")
public interface LikeToPostMapper {

    LikeRecord likeDtoToLikeRecord(LikeDto likeDto);

    LikeDto likeRecordToLikeDto(LikeRecord record);
}
