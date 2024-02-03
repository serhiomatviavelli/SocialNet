package ru.socialnet.team43.service;

import ru.socialnet.team43.dto.UserAuthDto;

public interface UserAuthService {

    UserAuthDto getUserByEmail(String email, String password);
}
