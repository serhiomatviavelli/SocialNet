package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.CommentType;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private Boolean isDeleted;
    private CommentType commentType;
    private OffsetDateTime time;
    private OffsetDateTime timeChanged;
    private Long authorId;
    private Long parentId;
    private String commentText;
    private Long postId;
    private Boolean isBlocked;
    private Integer likeAmount;
    private Boolean myLike;
    private Integer commentsCount;
    private String imagePath;
}
