package ru.socialnet.team43.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.UserAuthDto;

@RequiredArgsConstructor
@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final DatabaseClient databaseClient;

    @Override
    public UserAuthDto getUserByEmail(String email, String password) {
        UserAuthDto user = databaseClient.getUserByEmail(email);
        if (validatePassword(user, password)) {
            return user;
        }
        return null;
    }

    private boolean validatePassword(UserAuthDto userAuthDto, String password) {
        return userAuthDto.getPassword().equals(password);
    }
}
