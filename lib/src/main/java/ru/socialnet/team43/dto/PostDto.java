package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.PostType;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;
    private OffsetDateTime time;
    private OffsetDateTime timeChanged;
    private Long authorId;
    private String title;
    private PostType type;
    private String postText;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Integer commentsCount;
    private String reactionType;
    private String myReaction;
    private Integer likeAmount;
    private Boolean myLike;
    private OffsetDateTime publishDate;
    private String imagePath;
    private List<TagDto> tags;

}
