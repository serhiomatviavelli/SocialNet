package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.CommentRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.socialnet.team43.dto.CommentDto;
import ru.socialnet.team43.dto.enums.CommentType;

@Mapper(componentModel = "spring")
public interface CommentDtoCommentRecordMapper {

    CommentDto commentRecordToCommentDto(CommentRecord commentRecord);

    CommentRecord commentDtoToCommentRecordForEdit(CommentDto postCommentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "timeChanged", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(source = "commentDto", target = "commentType", qualifiedByName = "setType")
    @Mapping(target = "isBlocked", defaultValue = "false")
    @Mapping(target = "isDeleted", defaultValue = "false")
    @Mapping(target = "commentsCount", defaultValue = "0")
    @Mapping(target = "likeAmount", defaultValue = "0")
    CommentRecord commentDtoToCommentRecord(CommentDto commentDto);

    @Named("setType")
    default String setPostType(CommentDto commentDto) {
        return commentDto.getParentId() == null ? CommentType.POST.name() : CommentType.COMMENT.name();
    }

}
