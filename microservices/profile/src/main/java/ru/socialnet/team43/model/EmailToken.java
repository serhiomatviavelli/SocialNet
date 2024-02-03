package ru.socialnet.team43.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailToken {

    private String token;
    private String email;
    private LocalDateTime expiredTime;
    private boolean isUsed;
}
