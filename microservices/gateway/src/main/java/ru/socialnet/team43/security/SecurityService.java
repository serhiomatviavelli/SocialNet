package ru.socialnet.team43.security;

import ru.socialnet.team43.dto.RegDto;
import ru.socialnet.team43.exception.RefreshTokenException;
import ru.socialnet.team43.web.model.AuthRequest;
import ru.socialnet.team43.web.model.JwtResponse;
import ru.socialnet.team43.web.model.RefreshRequest;
import ru.socialnet.team43.web.model.SimpleResponse;

public interface SecurityService {
    JwtResponse authenticateUser(AuthRequest authRequest);

    JwtResponse refreshToken(RefreshRequest refreshRequest) throws RefreshTokenException;

    SimpleResponse logout(String userName);

    RegDto getRegDtoWithEncryptedPassword(RegDto inputDto);

    boolean doPasswordsMatch(RegDto regDto);
}
