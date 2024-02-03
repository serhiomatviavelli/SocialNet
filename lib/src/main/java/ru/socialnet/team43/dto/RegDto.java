package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegDto {
    private boolean isDeleted;
    private String email;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private String captchaCode;
    private String captchaSecret;
}
