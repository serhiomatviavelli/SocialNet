package ru.socialnet.team43.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.Roles;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegDtoDb {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Roles role;
}
