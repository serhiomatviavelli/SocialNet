package ru.socialnet.team43.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.dto.CaptchaDto;
import ru.socialnet.team43.util.CaptchaCreator;

@AllArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CaptchaCreator captchaCreator;

    @Override
    public void login() {
        log.info("Signed in");
    }

    @Override
    public void logout() {
        log.info("Signed out");
    }




    @Override
    public CaptchaDto getCaptcha() {
       return captchaCreator.createCaptcha();
    }
}
