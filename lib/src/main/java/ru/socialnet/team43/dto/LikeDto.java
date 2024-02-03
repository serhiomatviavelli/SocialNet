package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.LikeType;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {

    private Long authorId;
    private Boolean isDeleted;
    private ZonedDateTime time;
    private Long itemId;
    private LikeType type;
    private String reactionType;
}
