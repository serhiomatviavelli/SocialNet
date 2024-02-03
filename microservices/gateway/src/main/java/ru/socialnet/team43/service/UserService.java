package ru.socialnet.team43.service;

import ru.socialnet.team43.dto.UserAuthDto;

import java.util.Optional;

public interface UserService {
    Optional<UserAuthDto> findUserByEmail(String email);
}
