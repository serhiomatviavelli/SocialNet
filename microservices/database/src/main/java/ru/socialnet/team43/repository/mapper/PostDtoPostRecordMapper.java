package ru.socialnet.team43.repository.mapper;

import jooq.db.tables.records.PostRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.socialnet.team43.dto.PostDto;
import ru.socialnet.team43.dto.enums.PostType;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface PostDtoPostRecordMapper {

    PostDto postRecordToPostDto(PostRecord postRecord);

    PostRecord postDtoToPostRecordForEdit(PostDto postDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "isBlocked", defaultValue = "false")
    @Mapping(target = "isDeleted", defaultValue = "false")
    @Mapping(target = "commentsCount", defaultValue = "0")
    @Mapping(source = "postDto", target = "type", qualifiedByName = "setType")
    @Mapping(source = "postDto", target = "publishDate", qualifiedByName = "setPublishDate")
    PostRecord postDtoToPostRecordForCreate(PostDto postDto);

    @Named("setType")
    default String setPostType(PostDto postDto) {
        return postDto.getPublishDate() == null ? PostType.POSTED.name() : PostType.QUEUED.name();
    }

    @Named("setPublishDate")
    default OffsetDateTime setPublishDate(PostDto postDto) {
        return postDto.getPublishDate() == null ? OffsetDateTime.now() : postDto.getPublishDate();
    }

}
