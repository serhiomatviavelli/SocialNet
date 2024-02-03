package ru.socialnet.team43.dto;

import lombok.*;
import ru.socialnet.team43.dto.enums.Roles;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthDto {
    private Long id;
    private String email;
    private String password;
    private Roles role;
}
