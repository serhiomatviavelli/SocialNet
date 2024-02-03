package ru.socialnet.team43.service;

import ru.socialnet.team43.dto.CaptchaDto;

public interface AuthService {

    void login();

    void logout();

    CaptchaDto getCaptcha();
}
