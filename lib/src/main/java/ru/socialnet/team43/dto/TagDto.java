package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {

    private Long id;
    private Boolean isDeleted;
    private String name;

}
