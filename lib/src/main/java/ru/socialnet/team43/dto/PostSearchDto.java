package ru.socialnet.team43.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchDto {

    private List<Long> ids;
    private List<Long> accountsId;
    private List<Long> blockedIds;
    private String author;
    private String text;
    private Boolean withFriends;
    private Boolean isDeleted;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dateTo;
    private List<String> tags;
}
