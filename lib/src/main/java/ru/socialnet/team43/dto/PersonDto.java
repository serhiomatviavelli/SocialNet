package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.StatusCode;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private LocalDateTime regDate;
    private StatusCode statusCode;
    private LocalDateTime birthDate;
    private String messagePermission;
    private OffsetDateTime lastOnlineTime;
    private Boolean isOnline;
    private Boolean isBlocked;
    private String emojiStatus;
    private LocalDateTime deletionTimestamp;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
