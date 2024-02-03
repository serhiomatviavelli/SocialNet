package ru.socialnet.team43.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.socialnet.team43.dto.RegDto;
import ru.socialnet.team43.dto.CaptchaDto;
import ru.socialnet.team43.service.AuthService;
import ru.socialnet.team43.service.ForgotPasswordService;
import ru.socialnet.team43.service.RegistrationService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/register")
    public ResponseEntity<Void> registrationPerson(@RequestBody RegDto regDto) {
        boolean result = registrationService.registrationPerson(regDto);
        return result ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();

    }

    @PostMapping("/password/recovery/")
    public ResponseEntity<Void> sendEmailRecovery(@RequestParam("email") String email){
        try {
            forgotPasswordService.sendMailWithRecoveryLink(email);
        } catch (Exception e){
            log.info(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/recovery/{token}")
    public ResponseEntity<Void> resetForgotPassword(@PathVariable("token") String token,
                                                    @RequestParam("password") String password){

        boolean result = forgotPasswordService.setNewPassword(token, password);

        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> getCaptcha() {
        return ResponseEntity.ok(authService.getCaptcha());
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<Void> handler(FeignException ex) {
        log.warn("Error in profile {}", ex.getMessage());
        return ResponseEntity.status(ex.status()).build();
    }
}
