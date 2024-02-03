package ru.socialnet.team43.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.DatabaseClient;
import ru.socialnet.team43.dto.UserAuthDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final DatabaseClient databaseClient;

    @Override
    public Optional<UserAuthDto> findUserByEmail(String email) {
        return Optional.ofNullable(databaseClient.getUserByEmail(email));
    }
}
