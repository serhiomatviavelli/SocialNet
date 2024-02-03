package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSearchDto {
    private String id;
    private Boolean isDeleted;
    private Set<Long> ids;
    private List<String> blockedByIds;
    private Set<String> author;
    private Set<String> firstName;
    private Set<String> lastName;
    private Set<String> city;
    private Set<String> country;
    private Boolean isBlocked;
    private String statusCode;
    private Integer ageTo;
    private Integer ageFrom;
}
